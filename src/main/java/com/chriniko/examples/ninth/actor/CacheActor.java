package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.CacheUrlResultRequest;
import com.chriniko.examples.ninth.message.HttpBodyResultResponse;
import com.chriniko.examples.ninth.message.HttpUrlGetCacheRequest;
import com.chriniko.examples.ninth.message.NoCacheResultResponse;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractLoggingActor {

    private final Map<String, String> urlsToCleanBodies;
    private final ActorRef httpActor;

    public CacheActor(ActorRef httpActor) {
        this.urlsToCleanBodies = new HashMap<>();
        this.httpActor = httpActor;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(HttpUrlGetCacheRequest.class, msg -> {

                    String url = msg.getUrl();
                    String body = urlsToCleanBodies.get(url);

                    if (body != null) {
                        sender().tell(new HttpBodyResultResponse(url, body), getContext().self());
                    } else {
                        sender().tell(new NoCacheResultResponse(url), getContext().self());
                    }

                })
                .match(CacheUrlResultRequest.class, msg -> {

                    urlsToCleanBodies.put(msg.getUrl(), msg.getBody());

                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }

    public static Props props(ActorRef httpActor) {
        return Props.create(CacheActor.class, httpActor);
    }
}
