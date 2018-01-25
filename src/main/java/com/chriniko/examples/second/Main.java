package com.chriniko.examples.second;

import akka.actor.ActorSystem;
import com.chriniko.examples.second.actor.AkkademyDb;

public class Main {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("akkademy-db-system");

        actorSystem.actorOf(AkkademyDb.props(), "akkademy-db-1");
    }
}
