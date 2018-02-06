package com.chriniko.examples.tenth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.chriniko.examples.tenth.actor.RandomGeneratorActor;
import com.chriniko.examples.tenth.message.ProduceRandomNumber;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {


        // --- initialize system ---
        ActorSystem actorSystem = ActorSystem.create("ask-design-example");

        ActorRef randomGeneratorActor = actorSystem.actorOf(RandomGeneratorActor.props(), "random-generator-actor");


        // --- run your scenarios ---
        PatternsCS
                .ask(randomGeneratorActor,
                        new ProduceRandomNumber(),
                        Timeout.apply(3, TimeUnit.SECONDS))
                .whenComplete((result, error) -> {
                    System.out.println("result = " + result + ", error = " + error);
                });


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
