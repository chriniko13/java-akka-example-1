package com.chriniko.examples.eigth.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class Student {

    private final String firstname;
    private final String initials;
    private final String surname;
}
