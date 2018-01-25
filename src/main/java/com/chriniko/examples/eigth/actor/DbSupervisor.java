package com.chriniko.examples.eigth.actor;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.chriniko.examples.eigth.error.ConnectionLostException;
import com.chriniko.examples.eigth.error.RequestTimedOutException;
import com.chriniko.examples.eigth.message.GetStudent;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;

public class DbSupervisor extends AbstractLoggingActor {

    private final static SupervisorStrategy SUPERVISOR_STRATEGY =
            new OneForOneStrategy(10,
                    Duration.create("1 minute"),
                    DeciderBuilder
                            .match(ConnectionLostException.class, e -> SupervisorStrategy.restart())
                            .match(RequestTimedOutException.class, e -> SupervisorStrategy.resume())
                            .build()
            );
    private static final int TOTAL_ROUTEES = 5;

    private Router router;

    private final Receive receive;

    public DbSupervisor() {
        router = initRouter();
        receive = initMessageHandling();
    }

    private Receive initMessageHandling() {
        return ReceiveBuilder
                .create()
                .match(GetStudent.class, getStudentMessage -> router.route(getStudentMessage, sender()))
                .match(Terminated.class, terminatedChildMessage -> {

                    log().info("terminated received from child actor with path: {}", terminatedChildMessage.getActor().path());

                    // remove terminated child
                    router = this.router.removeRoutee(terminatedChildMessage.actor());

                    // create a new child and watch it
                    ActorRef dbActor = getContext().actorOf(DbActor.props());
                    getContext().watch(dbActor);

                    // add it to router
                    router = router.addRoutee(new ActorRefRoutee(dbActor));

                })
                .build();
    }

    private Router initRouter() {
        final List<Routee> routees = new ArrayList<>();

        for (int i = 0; i < TOTAL_ROUTEES; i++) {
            ActorRef dbActor = getContext().actorOf(DbActor.props());

            getContext().watch(dbActor);

            routees.add(new ActorRefRoutee(dbActor));
        }

        return new Router(new RoundRobinRoutingLogic(), routees);
    }


    @Override
    public SupervisorStrategy supervisorStrategy() {
        return SUPERVISOR_STRATEGY;
    }

    @Override
    public Receive createReceive() {
        return receive;
    }

    public static Props props() {
        return Props.create(DbSupervisor.class);
    }
}
