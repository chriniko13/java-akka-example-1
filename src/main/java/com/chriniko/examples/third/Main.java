package com.chriniko.examples.third;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.third.actors.JavaPingActor;
import com.chriniko.examples.third.actors.JavaPongActor;

public class Main {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("ping-pong-example");

        //actor path: akka://ping-pong-example/user/ping-actor-1
        ActorRef pingActor1 = actorSystem.actorOf(JavaPingActor.props(), "ping-actor-1");

        //actor path: akka://ping-pong-example/user/pong-actor-1
        ActorRef pongActor1 = actorSystem.actorOf(JavaPongActor.props(), "pong-actor-1");


        pongActor1.tell("ping", pingActor1);


    }
}
