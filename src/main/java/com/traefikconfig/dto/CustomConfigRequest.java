package com.traefikconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for operations with custom configuration")
public class CustomConfigRequest {
    
    @Schema(description = "The hostname to add or delete", example = "example.com", required = true)
    @NotBlank(message = "Hostname is required")
    private String hostname;
    
    @Schema(description = "Custom Dokploy API key (optional, uses default if not provided)")
    private String dokployApiKey;
    
    @Schema(description = "Custom API domain (optional, uses default if not provided)", example = "https://custom.imvj.in")
    private String apiDomain;
    
    @Schema(description = "Custom Application ID (optional, uses default if not provided)")
    private String applicationId;
    
    @Schema(description = "Custom Backend service name (optional, uses default if not provided)")
    private String backendService;
    
    @Schema(description = "Custom Frontend service name (optional, uses default if not provided)")
    private String frontendService;
    
    @Schema(description = "Single service name for simple configurations (when you only have one service for both HTTP and HTTPS)")
    private String serviceName;
    
    @Schema(description = "Port for the single service (optional, defaults to 8080)", example = "8080")
    private Integer servicePort;
    
    @Schema(description = "Skip middlewares in configuration (for simple setups without middleware section)", example = "true")
    private Boolean skipMiddlewares;

    // Getters and Setters
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getDokployApiKey() {
        return dokployApiKey;
    }

    public void setDokployApiKey(String dokployApiKey) {
        this.dokployApiKey = dokployApiKey;
    }

    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getBackendService() {
        return backendService;
    }

    public void setBackendService(String backendService) {
        this.backendService = backendService;
    }

    public String getFrontendService() {
        return frontendService;
    }

    public void setFrontendService(String frontendService) {
        this.frontendService = frontendService;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public void setServicePort(Integer servicePort) {
        this.servicePort = servicePort;
    }

    public Boolean getSkipMiddlewares() {
        return skipMiddlewares;
    }

    public void setSkipMiddlewares(Boolean skipMiddlewares) {
        this.skipMiddlewares = skipMiddlewares;
    }
}