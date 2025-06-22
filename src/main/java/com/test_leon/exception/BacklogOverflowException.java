package com.test_leon.exception;

public class BacklogOverflowException extends RuntimeException {

    public BacklogOverflowException(String message) {
        super(message);
    }
}
