package com.wine.api.api_gateway.exception;

public class GoogleAuthenticationException extends RuntimeException {

    private final int statusCode;

    public GoogleAuthenticationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
