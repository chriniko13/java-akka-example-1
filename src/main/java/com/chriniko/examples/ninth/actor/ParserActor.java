package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.CacheUrlResultRequest;
import com.chriniko.examples.ninth.message.ParseBodyRequest;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class ParserActor extends AbstractLoggingActor {

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(ParseBodyRequest.class, msg -> {

                    String bodyToClean = msg.getBodyToClean();

                    String cleanedBody = ArticleExtractor.getInstance().getText(bodyToClean);

                    sender().tell(new CacheUrlResultRequest(msg.getUrl(), cleanedBody), getSelf());

                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }

    public static Props props() {
        return Props.create(ParserActor.class);
    }
}
