package com.chriniko.examples.tenth.message;

public class Id {

    private final String id;

    public Id(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Id{" +
                "id='" + id + '\'' +
                '}';
    }
}
