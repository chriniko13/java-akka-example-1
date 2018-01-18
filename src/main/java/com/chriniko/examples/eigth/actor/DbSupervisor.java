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

    private final Router router;

    public DbSupervisor() {
        router = initRouter();
        initMessageHandling();
    }

    private void initMessageHandling() {
        receive(
                ReceiveBuilder
                        .match(GetStudent.class, getStudentMessage -> router.route(getStudentMessage, sender()))
                        //TODO should handle Terminated message from child actor...
                        .build()
        );
    }

    private Router initRouter() {
        final List<Routee> routees = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
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

    public static Props props() {
        return Props.create(DbSupervisor.class);
    }
}
