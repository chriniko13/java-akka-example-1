package com.chriniko.examples.tenth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.tenth.message.ProduceRandomNumber;
import com.chriniko.examples.tenth.message.RandomNumber;

import java.util.Random;

public class RandomGeneratorActor extends AbstractLoggingActor {

    private Random random;

    @Override
    public void preStart() throws Exception {
        super.preStart();

        log().info("Starting actor...");

        random = new Random();
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
                .match(ProduceRandomNumber.class, msg -> {

                    log().info("will produce a random number for: " + sender().path().name());

                    sender().tell(new RandomNumber(random.nextInt()), self());

                })
                .matchAny(this::unhandled)
                .build();
    }

    public static Props props() {
        return Props.create(RandomGeneratorActor.class);
    }
}
