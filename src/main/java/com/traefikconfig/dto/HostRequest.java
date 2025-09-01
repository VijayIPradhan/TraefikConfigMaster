package com.traefikconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for host operations")
public class HostRequest {
    
    @Schema(
        description = "The hostname to add or delete from Traefik configuration",
        example = "example.com",
        required = true
    )
    @NotBlank(message = "Hostname is required")
    private String hostname;

    public HostRequest() {}

    public HostRequest(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}