package com.example.currency_converter.service;

import com.example.currency_converter.dto.ConversionResponse;
import com.example.currency_converter.exception.CurrencyNotFoundException;
import com.example.currency_converter.exception.InvalidAmountException;
import com.example.currency_converter.model.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(currencyService, "apiUrl", "https://api.mock.com/");
    }

    @Test
    void convert_ValidRequest_Success() {
        // Arrange
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setResult("success");
        Map<String, Double> rates = new HashMap<>();
        rates.put("INR", 83.50);
        mockResponse.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(mockResponse);

        // Act
        ConversionResponse response = currencyService.convert("USD", "INR", 100.0);

        // Assert
        assertNotNull(response);
        assertEquals(8350.0, response.getConvertedAmount());
        assertEquals(83.50, response.getExchangeRate());
    }

    @Test
    void convert_InvalidAmount_ThrowsException() {
        assertThrows(InvalidAmountException.class, () -> {
            currencyService.convert("USD", "INR", -10.0);
        });
    }

    @Test
    void convert_InvalidTargetCurrency_ThrowsException() {
        // Arrange
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setResult("success");
        mockResponse.setRates(new HashMap<>()); // Empty rates

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        assertThrows(CurrencyNotFoundException.class, () -> {
            currencyService.convert("USD", "INVALID", 100.0);
        });
    }
}
