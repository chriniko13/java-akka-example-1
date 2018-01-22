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

    public DbActor() {

        receive(
                ReceiveBuilder
                        .match(GetStudent.class, getStudentMsg -> {

                            if (new Random().nextInt(2) == 1) {
                                context().stop(self());
                            } else {
                                getStudent(getStudentMsg.getId());
                            }
                        })
                        .build()
        );

    }

    @Override
    public void postStop() {
        log().info("i just stopped, path = {}", self().path());
    }

    private void getStudent(String studentId) {
        final Student result = StudentService.getInstance().fetch(studentId);
        sender().tell(new StudentResult(result), self());
    }

    public static Props props() {
        return Props.create(DbActor.class).withDispatcher("akkasample.blocking-dispatcher");
    }
}
