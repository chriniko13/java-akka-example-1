package com.chriniko.examples.second.message.error;

import java.io.Serializable;

public class KeyNotFoundException extends Exception implements Serializable {

    private final String key;

    public KeyNotFoundException(String key) {
        this.key = key;
    }
}
