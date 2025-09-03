package com.traefikconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for updating Traefik configuration properties")
public class ConfigUpdateRequest {
    
    @Schema(description = "Dokploy API key", example = "DokployAPIfiIorjkXByApJeEEeXIrCkxtMntiEbLFBdBwypmBzSiIhuUuQlALRdTuEBxSYRDt")
    private String dokployApiKey;
    
    @Schema(description = "API domain for both update and read operations", example = "https://dp.imvj.in")
    private String apiDomain;
    
    @Schema(description = "Application ID", example = "mp7_3lbuC06Ok3VXbGF0n")
    private String applicationId;
    
    @Schema(description = "Backend service name", example = "devcrm-crmbackend-service")
    private String backendService;
    
    @Schema(description = "Frontend service name", example = "devcrm-crmfrontend-service")
    private String frontendService;

    // Getters and Setters
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
}