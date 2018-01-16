package com.chriniko.examples.first;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class FirstExample {


    public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create("first-example");

        ActorRef greeterActor = actorSystem.actorOf(Greeter.props(), "greeter-1");

        greeterActor.tell(new Greet(), ActorRef.noSender());
        greeterActor.tell(new Greet(), ActorRef.noSender());
        greeterActor.tell(new Greet(), ActorRef.noSender());
        greeterActor.tell(new Greet(), ActorRef.noSender());

        greeterActor.tell(new Foo(), ActorRef.noSender());


        actorSystem.awaitTermination();
    }



    // ---- ACTORS DEFINITION ----
    static class Greeter extends AbstractLoggingActor {

        private int counter = 0;

        public Greeter() {
            receive(ReceiveBuilder
                    .match(Greet.class, this::onGreet)
                    .matchAny(o -> fallback())
                    .build());
        }

        private void onGreet(Greet greet) {
            counter++;
            log().info("Hello there, counter is: " + counter);
        }

        private void fallback() {
            log().info("Fallback method called!");
        }

        public static Props props() {
            return Props.create(Greeter.class);
        }
    }


    // ---- MESSAGES DEFINITION ----
    static class Greet {
        // Note: no data for the moment.
    }

    static class Foo {
        // Note: no data for the moment.
    }

}
