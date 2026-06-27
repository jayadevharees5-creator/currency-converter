package com.example.currency_converter.service;

import com.example.currency_converter.model.ExchangeRateResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
public class CurrencyService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://open.er-api.com/v6/latest/";

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Set<String> getSupportedCurrencies() {
        ExchangeRateResponse response = restTemplate.getForObject(API_URL + "USD", ExchangeRateResponse.class);
        if (response != null && response.getRates() != null) {
            return response.getRates().keySet();
        }
        return Set.of();
    }

    public double convert(String from, String to, double amount) {
        ExchangeRateResponse response = restTemplate.getForObject(API_URL + from, ExchangeRateResponse.class);
        if (response != null && response.getRates() != null) {
            Double rate = response.getRates().get(to);
            if (rate != null) {
                return amount * rate;
            } else {
                throw new IllegalArgumentException("Invalid target currency: " + to);
            }
        }
        throw new RuntimeException("Failed to fetch exchange rates");
    }
}
