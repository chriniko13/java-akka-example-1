package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.CacheUrlResultRequest;
import com.chriniko.examples.ninth.message.CleanBodyRequest;
import com.chriniko.examples.ninth.message.HttpBodyResultResponse;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class ParserActor extends AbstractLoggingActor {

    private final ActorRef cacheActor;
    private final ActorRef askDemoActor;

    public ParserActor(ActorRef cacheActor, ActorRef askDemoActor) {
        this.cacheActor = cacheActor;
        this.askDemoActor = askDemoActor;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(CleanBodyRequest.class, msg -> {

                    String bodyToClean = msg.getBodyToClean();

                    String cleanedBody = ArticleExtractor.getInstance().getText(bodyToClean);

                    //cache cleaned body...
                    cacheActor.tell(new CacheUrlResultRequest(msg.getUrl(), cleanedBody), getSelf());

                    //send result...
                    HttpBodyResultResponse httpBodyResultResponse = new HttpBodyResultResponse(msg.getUrl(), cleanedBody);
                    askDemoActor.tell(httpBodyResultResponse, getSelf());

                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }

    public static Props props(ActorRef cacheActor, ActorRef askDemoActor) {
        return Props.create(ParserActor.class, cacheActor, askDemoActor);
    }
}
