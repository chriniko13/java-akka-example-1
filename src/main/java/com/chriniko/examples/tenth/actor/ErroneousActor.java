package com.chriniko.examples.tenth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.tenth.message.ProduceWork;
import com.chriniko.examples.tenth.message.WorkCompletedSuccessfully;

import java.util.Random;

public class ErroneousActor extends AbstractLoggingActor {

    private final Random random = new Random();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(ProduceWork.class, msg -> {

                    //create message to send...
                    boolean fail = random.nextInt(2) == 1;

                    if (fail) {
                        sender().tell(new Status.Failure(new IllegalArgumentException()), self());
                    } else {
                        sender().tell(new WorkCompletedSuccessfully(), self());
                    }

                })
                .build();
    }

    public static Props props() {
        return Props.create(ErroneousActor.class);
    }
}
