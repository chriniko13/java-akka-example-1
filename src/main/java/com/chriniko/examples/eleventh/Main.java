package com.chriniko.examples.eleventh;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.eleventh.actor.ScrumMaster;
import com.chriniko.examples.eleventh.message.PipeUserStory;
import com.chriniko.examples.eleventh.message.UserStory;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        // --- initialize system ---
        ActorSystem actorSystem = ActorSystem.create("forward-design-example");

        ActorRef scrumMasterActor = actorSystem.actorOf(ScrumMaster.props(), "scrum-master-actor");

        // --- run your scenarios ---

        //scrumMasterActor.tell(new UserStory("US 1"), ActorRef.noSender()); // Note: forward example.

        scrumMasterActor.tell(new PipeUserStory("US 2"), ActorRef.noSender()); // Note: pipe example.

        // --- shutdown system ---
        try {
            System.out.println("will terminate actor system in 5 seconds...");
            TimeUnit.SECONDS.sleep(5);
            actorSystem.terminate();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
    }
}
