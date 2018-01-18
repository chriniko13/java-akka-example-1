package com.chriniko.examples.eigth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.eigth.domain.Student;
import com.chriniko.examples.eigth.message.GetStudent;
import com.chriniko.examples.eigth.message.StudentResult;
import com.chriniko.examples.eigth.service.StudentService;

public class DbActor extends AbstractLoggingActor {

    private final StudentService studentService;

    public DbActor() {

        studentService = new StudentService();

        receive(
                ReceiveBuilder
                        .match(GetStudent.class, getStudentMsg -> getStudent(getStudentMsg.getId()))
                        .build()
        );

    }

    private void getStudent(String studentId) {
        final Student result = studentService.fetch(studentId);
        sender().tell(new StudentResult(result), self());
    }

    public static Props props() {
        return Props.create(DbActor.class);
    }
}
