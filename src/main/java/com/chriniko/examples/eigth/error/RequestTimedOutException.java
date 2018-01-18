package com.chriniko.examples.eigth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestTimedOutException extends RuntimeException {
    private final String message;
}
