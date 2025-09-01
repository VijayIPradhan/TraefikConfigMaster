package com.traefikconfig.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Service information extracted from Traefik configuration")
public class ServiceInfo {
    
    @Schema(description = "Service name", example = "devcrm-crmbackend-service")
    private String serviceName;
    
    @Schema(description = "Service URL", example = "http://devcrm-crmbackend-4qxr51:8070")
    private String serviceUrl;
    
    @Schema(description = "Load balancer type", example = "roundrobin")
    private String loadBalancer;

    public ServiceInfo() {}

    public ServiceInfo(String serviceName, String serviceUrl, String loadBalancer) {
        this.serviceName = serviceName;
        this.serviceUrl = serviceUrl;
        this.loadBalancer = loadBalancer;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
}