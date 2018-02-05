package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.*;

public class AskDemoActor extends AbstractLoggingActor {

    private final ActorRef cacheActor;
    private final ActorRef httpActor;
    private final ActorRef parserActor;

    public AskDemoActor() {
        this.cacheActor = cacheActor;
        this.httpActor = httpActor;
        this.parserActor = parserActor;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(HttpUrlGetRequest.class, msg -> {

                    cacheActor.tell(new HttpUrlGetCacheRequest(msg.getUrl()), getSelf());
                })
                .match(NoCacheResultResponse.class, msg -> {

                    httpActor.tell(new SearchForUrlRequest(msg.getUrl()), getContext().self());

                })
                .match(HttpBodyResultResponse.class, msg -> {

                    log().info("result received for url: " + msg.getUrl());
                    //TODO add implementation...

                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }

    public static Props props() {
        return Props.create(AskDemoActor.class);
    }
}
