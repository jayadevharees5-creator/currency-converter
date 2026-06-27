package com.example.currency_converter.exception;

/**
 * Thrown when a requested currency code is not supported.
 */
public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException(String currencyCode) {
        super("Currency not found or not supported: " + currencyCode);
    }
}
