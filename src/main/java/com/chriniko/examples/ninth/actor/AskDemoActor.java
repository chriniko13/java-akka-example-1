package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.*;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class AskDemoActor extends AbstractLoggingActor {

    private final ActorRef cacheActor;
    private final ActorRef httpActor;
    private final ActorRef parserActor;

    private int requestsCounter;

    public AskDemoActor(ActorRef cacheActor, ActorRef httpActor, ActorRef parserActor) {
        this.cacheActor = cacheActor;
        this.httpActor = httpActor;
        this.parserActor = parserActor;

        requestsCounter = 0;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(HttpUrlGetRequest.class, msg -> {

                    log().info("Total requests = " + (++requestsCounter));

                    cacheActor.tell(new HttpUrlGetCacheRequest(msg.getUrl()), self());

                    //Scheduling a Tell Timeout
                    context()
                            .system()
                            .scheduler()
                            .scheduleOnce(
                                    Duration.create(7, TimeUnit.SECONDS),
                                    self(),
                                    "timeout",
                                    context().system().dispatcher(),
                                    ActorRef.noSender());

                })
                .match(NoCacheResultResponse.class, msg -> {

                    httpActor.tell(new SearchForUrlRequest(msg.getUrl()), self());

                })
                .match(CleanBodyRequest.class, msg -> {

                    parserActor.tell(new ParseBodyRequest(msg.getUrl(), msg.getBodyToClean()), self());
                })

                .match(CacheUrlResultRequest.class, msg -> {

                    cacheActor.tell(new CacheUrlResultToStoreRequest(msg.getUrl(), msg.getBody()), sender());

                    self().tell(new HttpBodyResultResponse(msg.getUrl(), msg.getBody()), self());
                })
                .match(HttpBodyResultResponse.class, msg -> {

                    String resultMessage = "\n\n~~~~~~~~~~~"
                            + "\nresult received for url: "
                            + msg.getUrl()
                            + ", result(body) = \n"
                            + msg.getBody()
                            + "\n~~~~~~~~~~~~~~~~~\n\n";

                    log().info(resultMessage);
                })
                .match(String.class, msg -> msg.equals("timeout"), msg -> {

                    log().warning("Failed to process the request fast....");
                    context().stop(self());

                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }

    public static Props props(ActorRef cacheActor, ActorRef httpActor, ActorRef parserActor) {
        return Props.create(AskDemoActor.class, cacheActor, httpActor, parserActor);
    }
}
