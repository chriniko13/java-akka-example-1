package com.chriniko.examples.tenth.message;

public class RandomNumber {

    private final int number;

    public RandomNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "RandomNumber{" +
                "number=" + number +
                '}';
    }
}
