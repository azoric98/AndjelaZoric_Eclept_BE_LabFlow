package com.eclept.andjelazoric_eclept_be_labflow.exception;

public class QueueFullException extends RuntimeException {
    public QueueFullException(String message) {
        super(message);
    }
}
