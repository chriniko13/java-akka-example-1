package com.chriniko.examples.twelvth.actor;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.twelvth.exception.DeathException;
import com.chriniko.examples.twelvth.exception.MajorAccidentException;
import com.chriniko.examples.twelvth.exception.MinorAccidentException;
import com.chriniko.examples.twelvth.exception.RejectTaskException;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class Supervisor extends AbstractLoggingActor {

    private ActorRef worker;

    @Override
    public void preStart() throws Exception {
        log().info("preStart fired!");
        worker = context().actorOf(Worker.props(), "worker-actor");
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        log().info("postStop fired!");
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(StartWork.class, msg -> {
                    log().info("Will tell worker to start working...");

                    for (Worker.Behaviour behaviour : Worker.Behaviour.values()) {

                        worker.tell(new Worker.Work(behaviour), self());

                        sleep();
                    }

                })
                .matchAny(msg -> {
                    log().warning("Received unknown message: " + msg);
                    unhandled(msg);
                })
                .build();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(
                5,
                Duration.Inf(),
                DeciderBuilder
                        .match(MinorAccidentException.class, error -> {
                            log().info("minor accident occurred ---> will RESUME worker");
                            return SupervisorStrategy.resume();
                        })
                        .match(MajorAccidentException.class, error -> {
                            log().info("major accident occurred ---> will STOP worker");
                            return SupervisorStrategy.stop();
                        })
                        .match(DeathException.class, error -> {
                            log().info("death occurred ---> will RESTART worker");
                            return SupervisorStrategy.restart();
                        })
                        .match(RejectTaskException.class, error -> {
                            log().info("task rejection occurred ---> will ESCALATE worker");
                            return SupervisorStrategy.escalate();
                        })
                        .matchAny(error -> SupervisorStrategy.escalate())
                        .build()
        );
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    public static Props props() {
        return Props.create(Supervisor.class);
    }

    // ---- protocol messages ----
    public static class StartWork {
    }

}
