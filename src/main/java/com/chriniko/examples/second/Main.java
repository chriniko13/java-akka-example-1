package com.chriniko.examples.second;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.second.actor.AkkademyDb;
import com.chriniko.examples.second.message.SetRequest;

public class Main {

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("akkademy-db-system");

        //actor path: akka://akkademy-db-system/user/akkademy-db-1
        ActorRef akkademyDb1 = actorSystem.actorOf(AkkademyDb.props(), "akkademy-db-1");

        akkademyDb1.tell(new SetRequest("firstname", "nikos"), ActorRef.noSender());


        actorSystem.awaitTermination();
    }
}
