package com.chriniko.examples.ninth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.ninth.actor.AskDemoActor;
import com.chriniko.examples.ninth.actor.CacheActor;
import com.chriniko.examples.ninth.actor.HttpActor;
import com.chriniko.examples.ninth.actor.ParserActor;
import com.chriniko.examples.ninth.message.HttpUrlGetRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {

        // --- initialize system ---
        ActorSystem actorSystem = ActorSystem.create("tell-design-example");

        ActorRef cacheActor = actorSystem.actorOf(CacheActor.props(), "cache-actor");

        ActorRef httpActor = actorSystem.actorOf(
                HttpActor.props().withDispatcher("akka.akkasample.blocking-dispatcher"),
                "http-actor");

        ActorRef parserActor = actorSystem.actorOf(ParserActor.props(), "parser-actor");

        ActorRef askDemoActor = actorSystem.actorOf(AskDemoActor.props(cacheActor, httpActor, parserActor), "ask-demo-actor");

        // --- run your scenarios ---
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        IntStream
                .rangeClosed(1, 5)
                .forEach(idx -> executorService.submit(() -> askDemoActor.tell(new HttpUrlGetRequest("https://en.wikipedia.org/wiki/HTTP_403"), ActorRef.noSender())));

        // --- shutdown system ---
        try {
            System.out.println("will terminate actor system in 5 seconds...");
            TimeUnit.SECONDS.sleep(5);

            executorService.shutdown();
            actorSystem.terminate();

        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

    }

}
