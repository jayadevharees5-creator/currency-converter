package com.example.currency_converter.exception;

/**
 * Thrown when the external exchange rate API fails or is unreachable.
 */
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
