package com.chriniko.examples.tenth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.chriniko.examples.tenth.actor.ErroneousActor;
import com.chriniko.examples.tenth.actor.IdProviderActor;
import com.chriniko.examples.tenth.actor.RandomGeneratorActor;
import com.chriniko.examples.tenth.message.*;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        // --- initialize system ---
        ActorSystem actorSystem = ActorSystem.create("ask-design-example");

        ActorRef randomGeneratorActor = actorSystem.actorOf(RandomGeneratorActor.props(), "random-generator-actor");
        ActorRef idProviderActor = actorSystem.actorOf(IdProviderActor.props(), "id-provider-actor");
        ActorRef erroneousActor = actorSystem.actorOf(ErroneousActor.props(), "erroneous-actor");

        // --- run your scenarios ---
        PatternsCS
                .ask(randomGeneratorActor,
                        new ProduceRandomNumber(),
                        Timeout.apply(3, TimeUnit.SECONDS))
                .thenCombine(PatternsCS.ask(idProviderActor, new ProduceId(), Timeout.apply(3, TimeUnit.SECONDS)),
                        (res1, res2) -> {

                            RandomNumber randomNumberMessage = (RandomNumber) res1;
                            Id id = (Id) res2;

                            System.out.println("res1 = " + res1 + ", res2 = " + res2);

                            return randomNumberMessage.getNumber() + id.getId();
                        })
                .thenCompose(res -> PatternsCS.ask(erroneousActor, new ProduceWork(), Timeout.apply(3, TimeUnit.SECONDS)))
                .thenApply(erroneousActorResult -> {
                    System.out.println("thenApply#stage --- erroneousActorResult = " + erroneousActorResult);
                    return "success";
                })
                .whenComplete((result, error) -> {
                    System.out.println("whenComplete#stage --- result = " + result + ", error = " + error);
                })
                .thenAccept(result -> System.out.println("thenAccept#stage --- result = " + result))
                .exceptionally(error -> {
                    System.out.println("exceptionally#stage --- error = " + error);
                    return null;
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
