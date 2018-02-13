package com.chriniko.examples.eleventh.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.PatternsCS;
import com.chriniko.examples.eleventh.message.EstimationCompleted;
import com.chriniko.examples.eleventh.message.PipeUserStory;
import com.chriniko.examples.eleventh.message.UserStory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Developer extends AbstractLoggingActor {

    private List<UserStory> tasks;

    @Override
    public void preStart() throws Exception {
        tasks = new LinkedList<>();
        super.preStart();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(UserStory.class, msg -> {

                    log().info("received message = " + msg + ", from: " + sender().path().name());
                    tasks.add(msg);

                })
                .match(PipeUserStory.class, msg -> {

                    log().info("received message = " + msg + ", from: " + sender().path().name());

                    CompletableFuture<EstimationCompleted> estimationWork
                            = CompletableFuture.supplyAsync(() -> new EstimationCompleted(5));

                    // pipe example usage...
                    PatternsCS
                            .pipe(estimationWork, context().system().dispatcher())
                            .to(sender());

                })
                .matchAny(this::unhandled)
                .build();
    }

    public static Props props() {
        return Props.create(Developer.class);
    }
}
