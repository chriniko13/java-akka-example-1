package com.chriniko.examples.eigth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectionLostException extends RuntimeException {

    private final String message;
}
