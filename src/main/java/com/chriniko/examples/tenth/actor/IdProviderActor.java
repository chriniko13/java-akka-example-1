package com.chriniko.examples.tenth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.tenth.message.Id;
import com.chriniko.examples.tenth.message.ProduceId;

import java.util.UUID;

public class IdProviderActor extends AbstractLoggingActor {

    @Override
    public void preStart() throws Exception {
        super.preStart();

        log().info("Starting actor...");
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        log().info("Actor just stopped...");
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(ProduceId.class, msg -> {

                    sender().tell(new Id(UUID.randomUUID().toString()), self());

                })
                .matchAny(this::unhandled)
                .build();
    }

    public static Props props() {
        return Props.create(IdProviderActor.class);
    }
}
