package com.chriniko.examples.eigth.message;

import com.chriniko.examples.eigth.domain.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class StudentResult {

    private final Student student;
}
