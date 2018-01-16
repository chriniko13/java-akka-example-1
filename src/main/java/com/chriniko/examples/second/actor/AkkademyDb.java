package com.chriniko.examples.second.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.second.message.SetRequest;

import java.util.HashMap;
import java.util.Map;

public class AkkademyDb extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    protected final Map<String, Object> db = new HashMap<>();

    private AkkademyDb() {

        receive(ReceiveBuilder
                .match(SetRequest.class, req -> {
                    log.info("Received set request, key: {} --- value: {}", req.getKey(), req.getValue());
                    db.put(req.getKey(), req.getValue());
                })
                .matchAny(msg -> log.info("received unknown message {}", msg))
                .build());

    }

    public static Props props() {
        return Props.create(AkkademyDb.class);
    }

    public Map<String, Object> getDb() {
        return db;
    }
}
