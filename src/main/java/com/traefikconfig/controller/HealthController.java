package com.traefikconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Application health monitoring endpoints")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private static final String EXTERNAL_HEALTH_URL = "https://trcon.devcrm.seabed2crest.com/api/health";

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    @Operation(
        summary = "Health check endpoint",
        description = "Returns the current health status of the application"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Application is healthy",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"status\": \"UP\", \"timestamp\": \"2024-01-01T12:00:00\", \"service\": \"Traefik Config Manager\", \"version\": \"1.0.0\"}"
            )
        )
    )
    public Map<String, Object> health() {
        logger.info("🏥 Health check endpoint called");
        logger.debug("📊 Application health status: OK");

        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "Traefik Config Manager",
            "version", "1.0.0"
        );
    }

    @GetMapping("/external")
    @Operation(
        summary = "External health check endpoint",
        description = "Calls external service health endpoint and returns its response"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved external service health status",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"status\": \"UP\", \"timestamp\": \"2024-01-01T12:00:00\", \"service\": \"External Service\"}"
            )
        )
    )
    public ResponseEntity<Object> externalHealth() {
        logger.info("🌐 Calling external health endpoint: {}", EXTERNAL_HEALTH_URL);

        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(EXTERNAL_HEALTH_URL, Object.class);
            logger.debug("✅ External health check response: {}", response.getBody());
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            logger.error("❌ Failed to call external health endpoint", e);
            return ResponseEntity.status(502).body(Map.of(
                "status", "DOWN",
                "error", "Unable to reach external service",
                "timestamp", LocalDateTime.now()
            ));
        }
    }
}
