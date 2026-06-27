package com.example.currency_converter.controller;

import com.example.currency_converter.dto.ConversionRequest;
import com.example.currency_converter.dto.ConversionResponse;
import com.example.currency_converter.dto.HistoryResponse;
import com.example.currency_converter.service.CurrencyService;
import com.example.currency_converter.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST Controller for currency conversion operations.
 * All endpoints require HTTP Basic Authentication.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Currency Converter", description = "APIs for currency conversion and history")
public class CurrencyController {

    private static final Logger log = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyService currencyService;
    private final HistoryService historyService;

    public CurrencyController(CurrencyService currencyService, HistoryService historyService) {
        this.currencyService = currencyService;
        this.historyService = historyService;
    }

    @GetMapping("/currencies")
    @Operation(summary = "Get supported currencies", description = "Returns a list of all supported currency codes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved currencies"),
            @ApiResponse(responseCode = "503", description = "Exchange rate service unavailable")
    })
    public ResponseEntity<Set<String>> getSupportedCurrencies() {
        log.info("GET /api/currencies");
        return ResponseEntity.ok(currencyService.getSupportedCurrencies());
    }

    @GetMapping("/convert")
    @Operation(summary = "Convert currency (GET)", description = "Converts an amount from one currency to another using query parameters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversion successful"),
            @ApiResponse(responseCode = "400", description = "Invalid currency or amount"),
            @ApiResponse(responseCode = "503", description = "Exchange rate service unavailable")
    })
    public ResponseEntity<ConversionResponse> convertGet(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount,
            Authentication authentication) {

        log.info("GET /api/convert - {} {} → {} by user: {}", amount, from, to, authentication.getName());

        ConversionResponse result = currencyService.convert(from.toUpperCase(), to.toUpperCase(), amount);

        // Save to history
        historyService.saveHistory(
                authentication.getName(),
                from.toUpperCase(),
                to.toUpperCase(),
                amount,
                result.getConvertedAmount()
        );

        return ResponseEntity.ok(result);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert currency (POST)", description = "Converts an amount using a JSON request body with validation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conversion successful"),
            @ApiResponse(responseCode = "400", description = "Validation error or invalid currency"),
            @ApiResponse(responseCode = "503", description = "Exchange rate service unavailable")
    })
    public ResponseEntity<ConversionResponse> convertPost(
            @Valid @RequestBody ConversionRequest request,
            Authentication authentication) {

        log.info("POST /api/convert - {} {} → {} by user: {}",
                request.getAmount(), request.getFromCurrency(), request.getToCurrency(), authentication.getName());

        ConversionResponse result = currencyService.convert(
                request.getFromCurrency().toUpperCase(),
                request.getToCurrency().toUpperCase(),
                request.getAmount()
        );

        // Save to history
        historyService.saveHistory(
                authentication.getName(),
                request.getFromCurrency().toUpperCase(),
                request.getToCurrency().toUpperCase(),
                request.getAmount(),
                result.getConvertedAmount()
        );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    @Operation(summary = "Get conversion history", description = "Returns the authenticated user's conversion history")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved history"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<List<HistoryResponse>> getHistory(Authentication authentication) {
        log.info("GET /api/history by user: {}", authentication.getName());
        return ResponseEntity.ok(historyService.getHistoryForUser(authentication.getName()));
    }
}
