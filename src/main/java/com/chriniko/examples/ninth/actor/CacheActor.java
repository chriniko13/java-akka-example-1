package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.CacheUrlResultToStoreRequest;
import com.chriniko.examples.ninth.message.HttpBodyResultResponse;
import com.chriniko.examples.ninth.message.HttpUrlGetCacheRequest;
import com.chriniko.examples.ninth.message.NoCacheResultResponse;

import java.util.HashMap;
import java.util.Map;

public class CacheActor extends AbstractLoggingActor {

    private final Map<String, String> urlsToCleanBodies;

    public CacheActor() {
        this.urlsToCleanBodies = new HashMap<>();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(HttpUrlGetCacheRequest.class, msg -> {

                    String url = msg.getUrl();
                    String body = urlsToCleanBodies.get(url);

                    if (body != null) {

                        log().info("cache hit!!!");
                        sender().tell(new HttpBodyResultResponse(url, body), getContext().self());

                    } else {

                        log().info("cache mis!!!");
                        sender().tell(new NoCacheResultResponse(url), getContext().self());

                    }

                })
                .match(CacheUrlResultToStoreRequest.class, msg -> {
                    urlsToCleanBodies.put(msg.getUrl(), msg.getCleanedBody());
                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }

    public static Props props() {
        return Props.create(CacheActor.class);
    }
}
