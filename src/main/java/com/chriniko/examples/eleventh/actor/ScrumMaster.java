package com.chriniko.examples.eleventh.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.eleventh.message.EstimationCompleted;
import com.chriniko.examples.eleventh.message.PipeUserStory;
import com.chriniko.examples.eleventh.message.UserStory;

public class ScrumMaster extends AbstractLoggingActor {

    private ActorRef developer;

    @Override
    public void preStart() throws Exception {

        developer = getContext().actorOf(Developer.props(), "developer-actor");

        super.preStart();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(UserStory.class, msg -> {

                    log().info("scrum master with name: "
                            + self().path().name()
                            + " just forward user story to developer with name: "
                            + developer.path().name());

                    developer.forward(msg, context());

                })
                .match(PipeUserStory.class, msg -> {

                    log().info("scrum master with name: "
                            + self().path().name()
                            + " just tell user story to developer with name: "
                            + developer.path().name());

                    developer.tell(msg, self());


                })
                .match(EstimationCompleted.class, msg -> {

                    log().info("estimation completed..." + msg);

                })
                .matchAny(this::unhandled)
                .build();
    }

    public static Props props() {
        return Props.create(ScrumMaster.class);
    }
}
