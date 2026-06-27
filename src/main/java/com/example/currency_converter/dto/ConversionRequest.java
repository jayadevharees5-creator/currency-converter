package com.example.currency_converter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for currency conversion.
 * Uses Jakarta Validation annotations to validate user input
 * before it reaches the service layer.
 */
public class ConversionRequest {

    @NotBlank(message = "Source currency code is required")
    private String fromCurrency;

    @NotBlank(message = "Target currency code is required")
    private String toCurrency;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    public ConversionRequest() {}

    public ConversionRequest(String fromCurrency, String toCurrency, double amount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }

    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }

    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
