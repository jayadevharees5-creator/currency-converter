package com.example.currency_converter.controller;

import com.example.currency_converter.dto.HomeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Home/Health endpoint — provides application metadata and API directory.
 * This endpoint is publicly accessible (no authentication required).
 */
@RestController
@Tag(name = "Home", description = "Application info and health check")
public class HomeController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @GetMapping("/")
    @Operation(summary = "Application info", description = "Returns application metadata and available API endpoints")
    public ResponseEntity<HomeResponse> home() {
        HomeResponse response = new HomeResponse(
                appName,
                appVersion,
                "UP",
                LocalDateTime.now(),
                List.of(
                        "GET  /                  - Application info (public)",
                        "GET  /api/currencies     - List supported currencies",
                        "GET  /api/convert        - Convert currency (query params)",
                        "POST /api/convert        - Convert currency (JSON body)",
                        "GET  /api/history        - Get conversion history",
                        "GET  /swagger-ui.html    - API documentation",
                        "GET  /h2-console         - Database console (dev only)"
                )
        );
        return ResponseEntity.ok(response);
    }
}
