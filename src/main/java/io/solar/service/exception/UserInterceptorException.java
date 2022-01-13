package io.solar.service.exception;

public class UserInterceptorException extends RuntimeException {

    public UserInterceptorException() {
    }

    public UserInterceptorException(String message) {
        super(message);
    }
}
