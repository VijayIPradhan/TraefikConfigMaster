package com.traefikconfig.controller;

import com.traefikconfig.config.TraefikConfigProperties;
import com.traefikconfig.dto.ApiResponse;
import com.traefikconfig.dto.ConfigUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@Tag(name = "Configuration Management", description = "API for managing application configuration properties")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private TraefikConfigProperties configProperties;

    @GetMapping("/current")
    @Operation(
        summary = "Get current configuration properties",
        description = "Retrieves the current configuration properties used by the application"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Configuration retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ApiResponse.class),
            examples = @ExampleObject(
                value = "{\"success\": true, \"message\": \"Configuration retrieved successfully\", \"data\": {\"dokployApiKey\": \"DokployAPI...\", \"apiDomain\": \"https://dp.imvj.in\", \"applicationId\": \"mp7_3lbuC06Ok3VXbGF0n\", \"backendService\": \"devcrm-crmbackend-service\", \"frontendService\": \"devcrm-crmfrontend-service\"}}"
            )
        )
    )
    public ResponseEntity<ApiResponse<TraefikConfigProperties>> getCurrentConfig() {
        logger.info("📋 Received request to get current configuration properties");
        
        try {
            logger.info("✅ Configuration properties retrieved successfully");
            return ResponseEntity.ok(ApiResponse.success("Configuration retrieved successfully", configProperties));
        } catch (Exception e) {
            logger.error("❌ Error retrieving configuration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/update")
    @Operation(
        summary = "Update configuration properties",
        description = "Updates the application configuration properties. Only non-null values will be updated."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Configuration updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Configuration updated successfully\", \"data\": {\"dokployApiKey\": \"NewAPIKey...\", \"apiDomain\": \"https://new-domain.com\", \"applicationId\": \"new-app-id\", \"backendService\": \"new-backend-service\", \"frontendService\": \"new-frontend-service\"}}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"Error: Failed to update configuration\", \"data\": null}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<TraefikConfigProperties>> updateConfig(
        @Parameter(description = "Configuration update request", required = true)
        @RequestBody ConfigUpdateRequest request
    ) {
        logger.info("🔧 Received request to update configuration properties");
        
        try {
            // Update only non-null values
            if (request.getDokployApiKey() != null) {
                logger.info("🔑 Updating Dokploy API key");
                configProperties.setDokployApiKey(request.getDokployApiKey());
            }
            
            if (request.getApiDomain() != null) {
                logger.info("🔗 Updating API Domain to: {}", request.getApiDomain());
                configProperties.setApiDomain(request.getApiDomain());
            }
            
            if (request.getApplicationId() != null) {
                logger.info("🆔 Updating Application ID to: {}", request.getApplicationId());
                configProperties.setApplicationId(request.getApplicationId());
            }
            
            if (request.getBackendService() != null) {
                logger.info("🔧 Updating Backend Service to: {}", request.getBackendService());
                configProperties.setBackendService(request.getBackendService());
            }
            
            if (request.getFrontendService() != null) {
                logger.info("🔧 Updating Frontend Service to: {}", request.getFrontendService());
                configProperties.setFrontendService(request.getFrontendService());
            }
            
            logger.info("✅ Configuration updated successfully");
            return ResponseEntity.ok(ApiResponse.success("Configuration updated successfully", configProperties));
        } catch (Exception e) {
            logger.error("❌ Error updating configuration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/reset")
    @Operation(
        summary = "Reset configuration to defaults",
        description = "Resets all configuration properties to their default values from application.yml"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Configuration reset successfully",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = "{\"success\": true, \"message\": \"Configuration reset to defaults successfully\", \"data\": null}"
            )
        )
    )
    public ResponseEntity<ApiResponse<Void>> resetConfig() {
        logger.info("🔄 Received request to reset configuration to defaults");
        
        try {
            // Reset to default values from application.yml
            configProperties.setDokployApiKey("DokployAPIfiIorjkXByApJeEEeXIrCkxtMntiEbLFBdBwypmBzSiIhuUuQlALRdTuEBxSYRDt");
            configProperties.setApiDomain("https://dp.imvj.in");
            configProperties.setApplicationId("mp7_3lbuC06Ok3VXbGF0n");
            configProperties.setBackendService("devcrm-crmbackend-service");
            configProperties.setFrontendService("devcrm-crmfrontend-service");
            
            logger.info("✅ Configuration reset to defaults successfully");
            return ResponseEntity.ok(ApiResponse.success("Configuration reset to defaults successfully"));
        } catch (Exception e) {
            logger.error("❌ Error resetting configuration: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}