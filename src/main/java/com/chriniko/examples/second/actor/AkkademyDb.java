package com.chriniko.examples.second.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.second.message.GetRequest;
import com.chriniko.examples.second.message.SetRequest;
import com.chriniko.examples.second.message.error.KeyNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    protected final Map<String, Object> db = new HashMap<>();

    private final Receive receive;

    private AkkademyDb() {

        receive = ReceiveBuilder
                .create()
                .match(SetRequest.class, req -> {

                    log.info("Received set request, key: {} --- value: {}", req.getKey(), req.getValue());

                    db.put(req.getKey(), req.getValue());

                    sender().tell(new Status.Success(req.getKey()), self());
                })
                .match(GetRequest.class, req -> {

                    Object result = db.get(req.getKey());

                    if (result == null) {
                        sender().tell(new Status.Failure(new KeyNotFoundException(req.getKey())), self());
                    } else {
                        sender().tell(result, self());
                    }

                })
                .matchAny(msg -> {

                    log.info("received unknown message {}", msg);

                    sender().tell(new Status.Failure(new ClassNotFoundException()), self());
                })
                .build();

    }

    public static Props props() {
        return Props.create(AkkademyDb.class);
    }

    public Map<String, Object> getDb() {
        return db;
    }

    @Override
    public Receive createReceive() {
        return receive;
    }
}
