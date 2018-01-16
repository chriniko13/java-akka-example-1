package com.chriniko.examples.fourth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.fourth.actor.ChiefWaiter;
import com.chriniko.examples.fourth.message.Order;

import java.util.Arrays;
import java.util.Collections;

public class Main {


    public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create("waiters-example");

        //actor path: akka://waiters-example/user/chief-waiter-1
        ActorRef chiefWaiter = actorSystem.actorOf(ChiefWaiter.props(), "chief-waiter-1");

        firstExample(chiefWaiter); // Note: comment-uncomment

        //secondExample(chiefWaiter); // Note: comment-uncomment

    }

    private static void firstExample(ActorRef chiefWaiter) {
        //with the following 3 messages we will make the BasicWaiter actor to become unavailable due to a lot of workload...
        chiefWaiter.tell(new Order(Arrays.asList("itemA", "itemB", "itemC")), ActorRef.noSender());
        chiefWaiter.tell(new Order(Arrays.asList("itemD", "itemE")), ActorRef.noSender());
        chiefWaiter.tell(new Order(Collections.singletonList("itemF")), ActorRef.noSender());

        //with the following message ChiefWaiter actor will handle the upcoming orders (change of behaviour)...
        chiefWaiter.tell(new Order(Collections.singletonList("itemG")), ActorRef.noSender());

    }

    private static void secondExample(ActorRef chiefWaiter) {

        chiefWaiter.tell("hey there!", ActorRef.noSender());

    }
}
