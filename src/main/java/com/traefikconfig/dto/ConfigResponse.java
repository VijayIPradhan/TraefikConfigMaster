package com.traefikconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing Traefik configuration")
public class ConfigResponse {
    
    @Schema(
        description = "The current Traefik configuration in YAML format",
        example = "http:\n  routers:\n    example-router:\n      rule: Host(`example.com`)\n      service: example-service"
    )
    private String config;

    public ConfigResponse() {}

    public ConfigResponse(String config) {
        this.config = config;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}