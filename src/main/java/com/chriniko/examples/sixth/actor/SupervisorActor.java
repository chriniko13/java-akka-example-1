package com.chriniko.examples.sixth.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.sixth.message.WorkToDo;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;


public class SupervisorActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final OneForOneStrategy STRATEGY = new OneForOneStrategy(
            10,
            Duration.apply(10, TimeUnit.SECONDS),
            DeciderBuilder
                    .match(RuntimeException.class, ex -> SupervisorStrategy.restart())
                    .build()
    );


    private final Receive receive;

    public SupervisorActor() {
        ActorRef unstableActorRef = getContext().actorOf(UnstableActor.props(), "unstable-actor");

        receive = ReceiveBuilder
                .create()
                .match(WorkToDo.class, msg -> unstableActorRef.forward(new WorkToDo(), getContext()))
                .build();
    }


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return STRATEGY;
    }

    @Override
    public Receive createReceive() {
        return receive;
    }

    public static Props props() {
        return Props.create(SupervisorActor.class);
    }

}
