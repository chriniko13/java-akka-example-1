package com.chriniko.examples.sixth.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.sixth.message.WorkToDo;
import scala.Option;

import java.util.Random;

public class UnstableActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Receive receive;

    public UnstableActor() {

        receive = ReceiveBuilder
                .create()
                .match(WorkToDo.class, msg -> {

                    if (new Random().nextInt(2) == 1) {
                        log.error("could not process work");
                        throw new IllegalStateException("[EXCEPTION]ooops, error occurred!");
                    } else {
                        log.info("work processed successfully");
                    }

                })
                .build();

    }

    @Override
    public Receive createReceive() {
        return null;
    }

    @Override
    public void preStart() throws Exception {
        log.info("[HOOKUP]unstable actor is going to start!");
    }

    @Override
    public void postStop() throws Exception {
        log.info("[HOOKUP]unstable actor has stopped!");
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        log.info("[HOOKUP]unstable actor just restarted!");
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        log.info("[HOOKUP]unstable actor is going to restart!");
    }

    public static Props props() {
        return Props.create(UnstableActor.class);
    }
}
