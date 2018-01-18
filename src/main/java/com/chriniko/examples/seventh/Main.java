package com.chriniko.examples.seventh;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class Main {

    // -------- bootstrap section ----------
    public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create("alarm-example");

        ActorRef alarmActor = actorSystem.actorOf(Main.Alarm.props("chriniko"), "alarm-actor");

        //send some messages to see the behaviour...
        alarmActor.tell(new Main.Alarm.Activity(), ActorRef.noSender());
        alarmActor.tell(new Main.Alarm.Enable("123"), ActorRef.noSender());
        alarmActor.tell(new Main.Alarm.Enable("chriniko"), ActorRef.noSender());
        alarmActor.tell(new Main.Alarm.Activity(), ActorRef.noSender());
        alarmActor.tell(new Main.Alarm.Disable("123"), ActorRef.noSender());
        alarmActor.tell(new Main.Alarm.Disable("chriniko"), ActorRef.noSender());
        alarmActor.tell(new Main.Alarm.Activity(), ActorRef.noSender());


        actorSystem.awaitTermination();
    }


    // -------- actors section -------------
    static class Alarm extends AbstractLoggingActor {

        // ----protocol----
        static class Activity {
        }

        @Getter
        @AllArgsConstructor
        static class Enable {
            private final String password;
        }

        @Getter
        @AllArgsConstructor
        static class Disable {
            private final String password;
        }


        // ----actor behaviour----
        private String password;

        private PartialFunction<Object, BoxedUnit> disabled;
        private PartialFunction<Object, BoxedUnit> enabled;

        public Alarm(String password) {
            this.password = password;

            disabled = ReceiveBuilder
                    .match(Enable.class,
                            msg -> {
                                if (msg.getPassword().equals(password)) {
                                    log().info("alarm enabled successfully!");
                                    getContext().become(enabled);
                                } else {
                                    log().info("someone tried to enable the alarm with wrong password!");
                                }
                            })
                    .build();

            enabled = ReceiveBuilder
                    .match(Activity.class, msg -> {
                        log().info("Alarm! Alarm!");
                    })
                    .match(Disable.class, msg -> {
                        if (msg.getPassword().contains(password)) {
                            log().info("alarm disabled successfully!");
                            getContext().become(disabled);
                        } else {
                            log().info("someone tried to disable the alarm with wrong password!");
                        }
                    })
                    .build();

            receive(disabled);
        }


        public static Props props(String password) {
            return Props.create(Alarm.class, password);
        }
    }


}
