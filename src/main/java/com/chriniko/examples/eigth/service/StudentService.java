package com.chriniko.examples.eigth.service;

import com.chriniko.examples.eigth.domain.Student;

public class StudentService {

    public Student fetch(String id) {
        //TODO ConnectionLostException and RequestTimedOutException throw it also...
        return new Student("dummyF", "dummyI", "dummyS"); //TODO enrich it...
    }
}
