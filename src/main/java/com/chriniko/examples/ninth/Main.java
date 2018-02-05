package com.chriniko.examples.ninth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.ninth.actor.AskDemoActor;
import com.chriniko.examples.ninth.actor.CacheActor;
import com.chriniko.examples.ninth.actor.HttpActor;
import com.chriniko.examples.ninth.actor.ParserActor;

public class Main {

    public static void main(String[] args) {

        // --- initialize system ---
        ActorSystem actorSystem = ActorSystem.create("tell-design-example");

        ActorRef askDemoActor = actorSystem.actorOf(AskDemoActor.props(null, null), "ask-demo-actor");

        ActorRef cacheActor = actorSystem.actorOf(CacheActor.props(null), "cache-actor");

        ActorRef httpActor = actorSystem.actorOf(HttpActor.props(null), "http-actor");

        ActorRef parserActor = actorSystem.actorOf(ParserActor.props(null, null), "parser-actor");


        // --- run your scenarios ---



        // --- shutdown system ---

    }

}
