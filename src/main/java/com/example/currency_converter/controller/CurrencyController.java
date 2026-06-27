package com.example.currency_converter.controller;

import com.example.currency_converter.model.ConversionHistory;
import com.example.currency_converter.service.CurrencyService;
import com.example.currency_converter.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    private final CurrencyService currencyService;
    private final HistoryService historyService;

    public CurrencyController(CurrencyService currencyService, HistoryService historyService) {
        this.currencyService = currencyService;
        this.historyService = historyService;
    }

    @GetMapping("/currencies")
    public ResponseEntity<Set<String>> getSupportedCurrencies() {
        return ResponseEntity.ok(currencyService.getSupportedCurrencies());
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount,
            Authentication authentication) {

        double convertedAmount = currencyService.convert(from.toUpperCase(), to.toUpperCase(), amount);

        // Save to history
        historyService.saveHistory(authentication.getName(), from.toUpperCase(), to.toUpperCase(), amount, convertedAmount);

        return ResponseEntity.ok(convertedAmount);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ConversionHistory>> getHistory(Authentication authentication) {
        return ResponseEntity.ok(historyService.getHistoryForUser(authentication.getName()));
    }
}
