package com.example.currency_converter.exception;

/**
 * Thrown when the conversion amount is invalid (e.g., zero or negative).
 */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
