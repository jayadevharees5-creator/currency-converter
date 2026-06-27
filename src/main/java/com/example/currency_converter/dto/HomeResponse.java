package com.example.currency_converter.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for the home/health endpoint.
 * Provides application metadata and available API endpoints.
 */
public class HomeResponse {

    private String application;
    private String version;
    private String status;
    private LocalDateTime timestamp;
    private List<String> availableEndpoints;

    public HomeResponse() {}

    public HomeResponse(String application, String version, String status,
                        LocalDateTime timestamp, List<String> availableEndpoints) {
        this.application = application;
        this.version = version;
        this.status = status;
        this.timestamp = timestamp;
        this.availableEndpoints = availableEndpoints;
    }

    public String getApplication() { return application; }
    public void setApplication(String application) { this.application = application; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<String> getAvailableEndpoints() { return availableEndpoints; }
    public void setAvailableEndpoints(List<String> availableEndpoints) { this.availableEndpoints = availableEndpoints; }
}
