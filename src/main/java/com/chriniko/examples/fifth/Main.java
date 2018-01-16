package com.chriniko.examples.fifth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.fifth.actor.DPili;
import com.chriniko.examples.fifth.actor.NChristi;
import com.chriniko.examples.fifth.message.WantCoffee;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        //bootstrapping process...
        ActorSystem actorSystem = ActorSystem.create("nchristi-dpili-example");

        //actor path: akka://waiters-example/user/chief-waiter-1
        ActorRef nchristi = actorSystem.actorOf(NChristi.props(), "nchristi");
        ActorRef dpili = actorSystem.actorOf(DPili.props(), "dpili");

        dpili.tell(new WantCoffee(nchristi), ActorRef.noSender());


        try {
            TimeUnit.SECONDS.sleep(5);
            actorSystem.terminate();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

    }
}
