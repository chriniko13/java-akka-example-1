package com.chriniko.examples.twelvth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import com.chriniko.examples.twelvth.actor.Supervisor;

public class Main {

    public static void main(String[] args) {

        // --- initialize system ---
        ActorSystem actorSystem = ActorSystem.create("supervisor-error-handling-design-example");
        ActorRef supervisor = actorSystem.actorOf(Supervisor.props(), "supervisor-actor");


        // --- run your scenarios ---
        supervisor.tell(new Supervisor.StartWork(), ActorRef.noSender());

        supervisor.tell(PoisonPill.getInstance(), ActorRef.noSender());
    }
}
