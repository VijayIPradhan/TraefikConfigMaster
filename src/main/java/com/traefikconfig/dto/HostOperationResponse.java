package com.traefikconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object for host add/delete operations including the updated configuration")
public class HostOperationResponse {
    
    @Schema(description = "Operation result message", example = "Host 'example.com' added successfully!")
    private String message;
    
    @Schema(description = "The updated Traefik configuration in YAML format")
    private String updatedConfig;
    
    @Schema(description = "The hostname that was processed", example = "example.com")
    private String hostname;

    public HostOperationResponse() {}

    public HostOperationResponse(String message, String updatedConfig, String hostname) {
        this.message = message;
        this.updatedConfig = updatedConfig;
        this.hostname = hostname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUpdatedConfig() {
        return updatedConfig;
    }

    public void setUpdatedConfig(String updatedConfig) {
        this.updatedConfig = updatedConfig;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}