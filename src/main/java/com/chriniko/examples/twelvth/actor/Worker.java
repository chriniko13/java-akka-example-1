package com.chriniko.examples.twelvth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.twelvth.exception.DeathException;
import com.chriniko.examples.twelvth.exception.MajorAccidentException;
import com.chriniko.examples.twelvth.exception.MinorAccidentException;
import com.chriniko.examples.twelvth.exception.WorkerException;
import scala.Option;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class Worker extends AbstractLoggingActor {


    private Map<Behaviour, WorkerException> exceptionsToThrow = new HashMap<Behaviour, WorkerException>() {
        {
            put(Behaviour.MINOR_SIMULATION, new MinorAccidentException("minor-accident-occurred")); /* the worker will continue == RESUME */
            put(Behaviour.MAJOR_SIMULATION, new MajorAccidentException("major-accident-occurred")); /* the worker will stop == STOP */
            put(Behaviour.DEATH_SIMULATION, new DeathException("death-occurred")); /* the worker will be replaced == RESTART */
            //put(Behaviour.REJECT_SIMULATION, new RejectTaskException("task-rejection-occurred")); /* the incident will be escalated == ESCALATE */
        }
    };

    private ZonedDateTime creationDateTime;

    @Override
    public void preStart() throws Exception {
        log().info("preStart fired!");
        creationDateTime = ZonedDateTime.now();
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        log().info("postStop fired!");
        super.postStop();
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        log().info("preRestart fired!");
        super.preRestart(reason, message);
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        log().info("postRestart fired!");
        super.postRestart(reason);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(Work.class, msg -> {

                    log().info("started working...creationDateTime = " + creationDateTime);
                    throw exceptionsToThrow.get(msg.behaviour);
                })
                .matchAny(msg -> {
                    log().warning("Received unknown message: " + msg);
                    unhandled(msg);
                })
                .build();
    }

    public static Props props() {
        return Props.create(Worker.class);
    }

    // --- protocol messages ---
    static class Work {
        Behaviour behaviour;

        Work(Behaviour behaviour) {
            this.behaviour = behaviour;
        }
    }

    public enum Behaviour {
        MINOR_SIMULATION,
        DEATH_SIMULATION,
        //REJECT_SIMULATION,
        MAJOR_SIMULATION
    }

}
