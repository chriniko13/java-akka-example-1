package com.chriniko.examples.eigth;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.chriniko.examples.eigth.actor.DbSupervisor;
import com.chriniko.examples.eigth.actor.WebServer;

public class Main {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("db-service-example");

        ActorRef dbSupervisor = system.actorOf(DbSupervisor.props(), "db-supervisor");

        system.actorOf(WebServer.props(dbSupervisor, "localhost", 8080), "webserver");

    }
}
