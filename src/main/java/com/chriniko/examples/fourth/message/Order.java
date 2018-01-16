package com.chriniko.examples.fourth.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class Order {

    private final List<String> items;
}
