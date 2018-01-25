package com.chriniko.examples.third.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.concurrent.TimeUnit;

public class JavaPongActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final Receive receive;

    public JavaPongActor() {
        receive = ReceiveBuilder
                .create()
                .match(String.class, "ping"::equals, msg -> {
                    log.info("message = " + msg);

                    TimeUnit.SECONDS.sleep(2);

                    sender().tell("pong", self());
                })
                .matchAny(msg -> sender().tell(new Status.Failure(new IllegalStateException("unknown message")), self()))
                .build();
    }


    public static Props props() {
        return Props.create(JavaPongActor.class);
    }

    @Override
    public Receive createReceive() {
        return receive;
    }
}
