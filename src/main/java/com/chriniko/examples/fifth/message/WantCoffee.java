package com.chriniko.examples.fifth.message;

import akka.actor.ActorRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WantCoffee {
    private final ActorRef target;
}
