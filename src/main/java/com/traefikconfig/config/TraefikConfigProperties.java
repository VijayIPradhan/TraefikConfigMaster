package com.traefikconfig.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "traefik.config")
public class TraefikConfigProperties {
    
    private String dokployApiKey;
    private String apiDomain;
    private String applicationId;
    private String backendService;
    private String frontendService;
    private String singleService;
    private Integer singleServicePort;
    private Boolean skipMiddlewares;

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
    
    public String getUpdateApiUrl() {
        return apiDomain + "/api/application.updateTraefikConfig";
    }

    public String getReadApiUrl() {
        return apiDomain + "/api/application.readTraefikConfig";
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

    public String getSingleService() {
        return singleService;
    }

    public void setSingleService(String singleService) {
        this.singleService = singleService;
    }

    public Integer getSingleServicePort() {
        return singleServicePort;
    }

    public void setSingleServicePort(Integer singleServicePort) {
        this.singleServicePort = singleServicePort;
    }

    public Boolean getSkipMiddlewares() {
        return skipMiddlewares;
    }

    public void setSkipMiddlewares(Boolean skipMiddlewares) {
        this.skipMiddlewares = skipMiddlewares;
    }
}