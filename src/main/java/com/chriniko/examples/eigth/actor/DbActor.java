package com.chriniko.examples.eigth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.eigth.domain.Student;
import com.chriniko.examples.eigth.message.GetStudent;
import com.chriniko.examples.eigth.message.StudentResult;
import com.chriniko.examples.eigth.service.StudentService;

import java.util.Random;

public class DbActor extends AbstractLoggingActor {

    private final StudentService studentService;

    private static final boolean ENABLE_SELF_STOP_SCENARIO = false;

    private final Receive receive;

    public DbActor() {

        studentService = new StudentService();

        receive = ReceiveBuilder
                .create()
                .match(GetStudent.class, getStudentMsg -> {

                    if (ENABLE_SELF_STOP_SCENARIO) {
                        // Note: make sometimes this actor to stop himself, so the supervisor receives a Terminated message.
                        if (new Random().nextInt(2) == 1) {
                            context().stop(self());
                            return;
                        }
                    }

                    getStudent(getStudentMsg.getId());

                })
                .build();
    }

    @Override
    public void postStop() {
        log().info("i just stopped, path = {}", self().path());
    }

    @Override
    public void postRestart(Throwable reason) {
        log().info("i just restarted, reason = {}, path = {}", reason, self().path());
    }

    @Override
    public Receive createReceive() {
        return receive;
    }

    private void getStudent(String studentId) {
        final Student result = studentService.fetch(studentId);
        sender().tell(new StudentResult(result), self());
    }

    public static Props props() {
        return Props
                .create(DbActor.class)
                .withDispatcher("akkasample.blocking-dispatcher");
    }
}
