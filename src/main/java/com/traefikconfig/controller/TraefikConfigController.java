package com.traefikconfig.controller;

import com.traefikconfig.dto.ApiResponse;
import com.traefikconfig.dto.ConfigResponse;
import com.traefikconfig.dto.CustomConfigRequest;
import com.traefikconfig.dto.HostOperationResponse;
import com.traefikconfig.dto.HostRequest;
import com.traefikconfig.dto.ServiceInfo;
import com.traefikconfig.service.TraefikConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/traefik")
@Tag(name = "Traefik Configuration", description = "API for managing Traefik configurations via Dokploy")
public class TraefikConfigController {

    private static final Logger logger = LoggerFactory.getLogger(TraefikConfigController.class);

    @Autowired
    private TraefikConfigService traefikConfigService;

    @PostMapping("/add-host")
    @Operation(summary = "Add a new host configuration", description = "Adds a new hostname to the Traefik configuration with both frontend and backend routes")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Host added successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"success\": true, \"message\": \"Host 'example.com' added successfully!\", \"data\": {\"message\": \"Host 'example.com' added successfully!\", \"hostname\": \"example.com\", \"updatedConfig\": \"http:\\n  routers:\\n    ...\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid hostname provided", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Hostname is required\", \"data\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Error: Failed to update configuration\", \"data\": null}")))
    })
    public ResponseEntity<ApiResponse<HostOperationResponse>> addHost(
            @Parameter(description = "Host configuration request", required = true) @Valid @RequestBody HostRequest request) {
        String hostname = request.getHostname().trim();
        logger.info("🚀 Received request to add host: {}", hostname);

        try {
            HostOperationResponse result = traefikConfigService.addHost(hostname);
            logger.info("✅ Successfully processed add-host request for: {}", hostname);
            logger.info("📄 Updated configuration logged in service layer");
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
        } catch (Exception e) {
            logger.error("❌ Error adding host '{}': {}", hostname, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-host")
    @Operation(summary = "Delete a host configuration", description = "Removes all router configurations for the specified hostname from Traefik")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Host deleted successfully or not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = {
                    @ExampleObject(name = "Success", value = "{\"success\": true, \"message\": \"Host 'example.com' deleted successfully!\", \"data\": {\"message\": \"Host 'example.com' deleted successfully!\", \"hostname\": \"example.com\", \"updatedConfig\": \"http:\\n  routers:\\n    ...\"}}"),
                    @ExampleObject(name = "Not Found", value = "{\"success\": true, \"message\": \"Host 'example.com' not found in configuration. Nothing to delete.\", \"data\": {\"message\": \"Host 'example.com' not found in configuration. Nothing to delete.\", \"hostname\": \"example.com\", \"updatedConfig\": \"http:\\n  routers:\\n    ...\"}}")
            })),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid hostname provided", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Hostname is required\", \"data\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Error: Failed to delete configuration\", \"data\": null}")))
    })
    public ResponseEntity<ApiResponse<HostOperationResponse>> deleteHost(
            @Parameter(description = "Host configuration request", required = true) @Valid @RequestBody HostRequest request) {
        String hostname = request.getHostname().trim();
        logger.info("🗑️ Received request to delete host: {}", hostname);

        try {
            HostOperationResponse result = traefikConfigService.deleteHost(hostname);
            logger.info("✅ Successfully processed delete-host request for: {}", hostname);
            logger.info("📄 Updated configuration logged in service layer");
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
        } catch (Exception e) {
            logger.error("❌ Error deleting host '{}': {}", hostname, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/config")
    @Operation(summary = "Get current Traefik configuration", description = "Retrieves the current Traefik configuration from Dokploy")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"success\": true, \"message\": \"Configuration retrieved successfully\", \"data\": {\"config\": \"http:\\n  routers:\\n    ...\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Error: Failed to fetch configuration\", \"data\": null}")))
    })
    public ResponseEntity<ApiResponse<ConfigResponse>> getCurrentConfig() {
        logger.info("📋 Get config request");

        try {
            String config = traefikConfigService.getCurrentConfig();
            logger.info("✅ Get config completed [size={}]", config.length());

            ConfigResponse configResponse = new ConfigResponse(config);
            return ResponseEntity.ok(ApiResponse.success("Configuration retrieved successfully", configResponse));
        } catch (Exception e) {
            logger.error("❌ Get config failed: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/add-host-custom")
    @Operation(summary = "Add a new host configuration with custom settings", description = "Adds a new hostname to the Traefik configuration using custom API keys, URLs, and service names")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Host added successfully with custom config", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"success\": true, \"message\": \"Host 'example.com' added successfully with custom config!\", \"data\": {\"message\": \"Host 'example.com' added successfully with custom config!\", \"hostname\": \"example.com\", \"updatedConfig\": \"http:\\n  routers:\\n    ...\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Hostname is required\", \"data\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Error: Failed to update configuration\", \"data\": null}")))
    })
    public ResponseEntity<ApiResponse<HostOperationResponse>> addHostWithCustomConfig(
            @Parameter(description = "Custom host configuration request", required = true) @Valid @RequestBody CustomConfigRequest request) {
        String hostname = request.getHostname().trim();
        logger.info("🚀 Received request to add host with custom config: {}", hostname);
        logger.debug("🔧 Custom config provided - API Key: {}, App ID: {}, Backend: {}, Frontend: {}",
                request.getDokployApiKey() != null ? "***PROVIDED***" : "DEFAULT",
                request.getApplicationId() != null ? request.getApplicationId() : "DEFAULT",
                request.getBackendService() != null ? request.getBackendService() : "DEFAULT",
                request.getFrontendService() != null ? request.getFrontendService() : "DEFAULT");

        try {
            HostOperationResponse result = traefikConfigService.addHostWithCustomConfig(request);
            logger.info("✅ Successfully processed add-host-custom request for: {}", hostname);
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
        } catch (Exception e) {
            logger.error("❌ Error adding host '{}' with custom config: {}", hostname, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-host-custom")
    @Operation(summary = "Delete a host configuration with custom settings", description = "Removes all router configurations for the specified hostname using custom API keys and URLs")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Host deleted successfully with custom config", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"success\": true, \"message\": \"Host 'example.com' deleted successfully with custom config!\", \"data\": {\"message\": \"Host 'example.com' deleted successfully with custom config!\", \"hostname\": \"example.com\", \"updatedConfig\": \"http:\\n  routers:\\n    ...\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Hostname is required\", \"data\": null}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Error: Failed to delete configuration\", \"data\": null}")))
    })
    public ResponseEntity<ApiResponse<HostOperationResponse>> deleteHostWithCustomConfig(
            @Parameter(description = "Custom host configuration request", required = true) @Valid @RequestBody CustomConfigRequest request) {
        String hostname = request.getHostname().trim();
        logger.info("🗑️ Received request to delete host with custom config: {}", hostname);
        logger.debug("🔧 Custom config provided - API Key: {}, App ID: {}",
                request.getDokployApiKey() != null ? "***PROVIDED***" : "DEFAULT",
                request.getApplicationId() != null ? request.getApplicationId() : "DEFAULT");

        try {
            HostOperationResponse result = traefikConfigService.deleteHostWithCustomConfig(request);
            logger.info("✅ Successfully processed delete-host-custom request for: {}", hostname);
            return ResponseEntity.ok(ApiResponse.success(result.getMessage(), result));
        } catch (Exception e) {
            logger.error("❌ Error deleting host '{}' with custom config: {}", hostname, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/config-custom")
    @Operation(summary = "Get current Traefik configuration with custom settings", description = "Retrieves the current Traefik configuration using custom API keys and URLs")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Configuration retrieved successfully with custom config", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = "{\"success\": true, \"message\": \"Configuration retrieved successfully with custom config\", \"data\": {\"config\": \"http:\\n  routers:\\n    ...\"}}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"success\": false, \"message\": \"Error: Failed to fetch configuration\", \"data\": null}")))
    })
    public ResponseEntity<ApiResponse<ConfigResponse>> getCurrentConfigWithCustom(
            @Parameter(description = "Custom configuration request", required = true) @RequestBody CustomConfigRequest request) {
        logger.info("📋 Received request to get current Traefik configuration with custom config");
        logger.debug("🔧 Custom config provided - API Key: {}, App ID: {}",
                request.getDokployApiKey() != null ? "***PROVIDED***" : "DEFAULT",
                request.getApplicationId() != null ? request.getApplicationId() : "DEFAULT");

        try {
            String config = traefikConfigService.getCurrentConfigWithCustomConfig(request);
            logger.info("✅ Successfully retrieved configuration with custom config (length: {} characters)",
                    config.length());

            ConfigResponse configResponse = new ConfigResponse(config);
            return ResponseEntity
                    .ok(ApiResponse.success("Configuration retrieved successfully with custom config", configResponse));
        } catch (Exception e) {
            logger.error("❌ Error retrieving configuration with custom config: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/services")
    @Operation(
        summary = "Get service URLs from Traefik configuration",
        description = "Extracts and returns all service URLs from the current Traefik configuration"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Service URLs retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Service URLs retrieved successfully\", \"data\": [{\"serviceName\": \"devcrm-crmbackend-service\", \"serviceUrl\": \"http://devcrm-crmbackend-4qxr51:8070\", \"loadBalancer\": \"roundrobin\"}, {\"serviceName\": \"devcrm-crmfrontend-service\", \"serviceUrl\": \"http://devcrm-crmfrontend-abc123:3000\", \"loadBalancer\": \"roundrobin\"}]}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"Error: Failed to extract service URLs\", \"data\": null}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<java.util.List<com.traefikconfig.dto.ServiceInfo>>> getServiceUrls() {
        logger.info("🔍 Received request to get service URLs from Traefik configuration");

        try {
            java.util.List<com.traefikconfig.dto.ServiceInfo> services = traefikConfigService.getServiceUrls();
            logger.info("✅ Successfully extracted {} service URLs", services.size());

            return ResponseEntity.ok(ApiResponse.success("Service URLs retrieved successfully", services));
        } catch (Exception e) {
            logger.error("❌ Error extracting service URLs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/services-custom")
    @Operation(
        summary = "Get service URLs with custom configuration",
        description = "Extracts and returns all service URLs using custom API keys and URLs"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Service URLs retrieved successfully with custom config",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class),
                examples = @ExampleObject(
                    value = "{\"success\": true, \"message\": \"Service URLs retrieved successfully with custom config\", \"data\": [{\"serviceName\": \"custom-backend-service\", \"serviceUrl\": \"http://custom-backend-xyz789:8080\", \"loadBalancer\": \"roundrobin\"}]}"
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"success\": false, \"message\": \"Error: Failed to extract service URLs\", \"data\": null}"
                )
            )
        )
    })
    public ResponseEntity<ApiResponse<java.util.List<com.traefikconfig.dto.ServiceInfo>>> getServiceUrlsWithCustomConfig(
        @Parameter(description = "Custom configuration request", required = true)
        @RequestBody CustomConfigRequest request
    ) {
        logger.info("🔍 Received request to get service URLs with custom config");
        logger.debug("🔧 Custom config provided - API Key: {}, App ID: {}", 
                    request.getDokployApiKey() != null ? "***PROVIDED***" : "DEFAULT",
                    request.getApplicationId() != null ? request.getApplicationId() : "DEFAULT");

        try {
            java.util.List<com.traefikconfig.dto.ServiceInfo> services = traefikConfigService.getServiceUrlsWithCustomConfig(request);
            logger.info("✅ Successfully extracted {} service URLs with custom config", services.size());

            return ResponseEntity.ok(ApiResponse.success("Service URLs retrieved successfully with custom config", services));
        } catch (Exception e) {
            logger.error("❌ Error extracting service URLs with custom config: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}