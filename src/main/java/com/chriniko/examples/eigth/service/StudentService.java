package com.chriniko.examples.eigth.service;

import com.chriniko.examples.eigth.domain.Student;
import com.chriniko.examples.eigth.error.ConnectionLostException;
import com.chriniko.examples.eigth.error.RequestTimedOutException;

import java.util.Random;

public class StudentService {

    public Student fetch(String id) {

        int randomInt = new Random().nextInt(3);

        if (randomInt == 1) {
            throw new ConnectionLostException("connection-lost");
        }

        if (randomInt == 2) {
            throw new RequestTimedOutException("request-timed-out");
        }

        return Long.parseLong(id) > 5
                ? null :
                new Student("firstname" + id, "initials" + id, "surname" + id);
    }
}
