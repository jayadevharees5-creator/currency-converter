package com.example.currency_converter.service;

import com.example.currency_converter.dto.ConversionResponse;
import com.example.currency_converter.exception.CurrencyNotFoundException;
import com.example.currency_converter.exception.ExternalApiException;
import com.example.currency_converter.exception.InvalidAmountException;
import com.example.currency_converter.model.ExchangeRateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Core business logic for currency conversion.
 * Fetches live exchange rates from an external API and performs conversions.
 */
@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);

    private final RestTemplate restTemplate;

    @Value("${app.exchange-rate.api-url}")
    private String apiUrl;

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches the list of supported currency codes from the external API.
     *
     * @return set of currency codes (e.g., "USD", "EUR", "INR")
     */
    public Set<String> getSupportedCurrencies() {
        log.debug("Fetching supported currencies from external API");
        try {
            ExchangeRateResponse response = restTemplate.getForObject(apiUrl + "USD", ExchangeRateResponse.class);
            if (response != null && response.getRates() != null) {
                log.info("Fetched {} supported currencies", response.getRates().size());
                return response.getRates().keySet();
            }
            throw new ExternalApiException("Empty response from exchange rate API");
        } catch (RestClientException ex) {
            log.error("Failed to fetch currencies from external API: {}", ex.getMessage());
            throw new ExternalApiException("Unable to reach exchange rate service", ex);
        }
    }

    /**
     * Converts an amount from one currency to another using live exchange rates.
     *
     * @param from   source currency code (e.g., "USD")
     * @param to     target currency code (e.g., "INR")
     * @param amount the amount to convert (must be > 0)
     * @return ConversionResponse with all conversion details
     */
    public ConversionResponse convert(String from, String to, double amount) {
        log.info("Converting {} {} → {}", amount, from, to);

        // Validate amount
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than 0. Received: " + amount);
        }

        try {
            ExchangeRateResponse response = restTemplate.getForObject(apiUrl + from, ExchangeRateResponse.class);

            if (response == null || response.getRates() == null) {
                throw new ExternalApiException("Empty response from exchange rate API for currency: " + from);
            }

            // Check if source currency is valid (API returns rates even for invalid base in some cases)
            if (!"success".equalsIgnoreCase(response.getResult())) {
                throw new CurrencyNotFoundException(from);
            }

            Double rate = response.getRates().get(to);
            if (rate == null) {
                throw new CurrencyNotFoundException(to);
            }

            double convertedAmount = amount * rate;
            log.info("Conversion result: {} {} = {} {} (rate: {})", amount, from, convertedAmount, to, rate);

            return new ConversionResponse(
                    from, to, amount, convertedAmount, rate, LocalDateTime.now()
            );

        } catch (RestClientException ex) {
            log.error("External API call failed for {} → {}: {}", from, to, ex.getMessage());
            throw new ExternalApiException("Unable to reach exchange rate service", ex);
        }
    }
}
