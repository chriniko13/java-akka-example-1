package com.chriniko.examples.sixth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.sixth.actor.SupervisorActor;
import com.chriniko.examples.sixth.message.WorkToDo;

import java.util.stream.IntStream;

public class Main {


    public static void main(String[] args) {

        //bootstrapping process...
        ActorSystem actorSystem = ActorSystem.create("failure-handling-example");

        //actor path: akka://failure-handling-example/user/supervisor
        ActorRef supervisor = actorSystem.actorOf(SupervisorActor.props(), "supervisor");

        IntStream.rangeClosed(1, 10)
                .forEach(idx -> supervisor.tell(new WorkToDo(), ActorRef.noSender()));

//        try {
//            TimeUnit.SECONDS.sleep(5);
//            actorSystem.terminate();
//        } catch (InterruptedException e) {
//            e.printStackTrace(System.err);
//        }
    }
}
