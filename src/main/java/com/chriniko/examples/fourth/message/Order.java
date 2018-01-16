package com.chriniko.examples.fourth.message;

import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@Getter
@ToString
public class Order {

    private final List<String> items;

    public Order(List<String> items) {
        this.items = Collections.unmodifiableList(items);
    }
}
