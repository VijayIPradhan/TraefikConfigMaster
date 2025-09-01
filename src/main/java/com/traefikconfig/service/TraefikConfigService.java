package com.traefikconfig.service;

import com.traefikconfig.dto.CustomConfigRequest;
import com.traefikconfig.dto.HostOperationResponse;

public interface TraefikConfigService {

    /**
     * Add a new host configuration to Traefik
     * 
     * @param hostname The hostname to add
     * @return Operation response with message and updated config
     * @throws Exception if operation fails
     */
    HostOperationResponse addHost(String hostname) throws Exception;

    /**
     * Delete a host configuration from Traefik
     * 
     * @param hostname The hostname to delete
     * @return Operation response with message and updated config
     * @throws Exception if operation fails
     */
    HostOperationResponse deleteHost(String hostname) throws Exception;

    /**
     * Add a new host configuration to Traefik with custom configuration
     * 
     * @param request Custom configuration request
     * @return Operation response with message and updated config
     * @throws Exception if operation fails
     */
    HostOperationResponse addHostWithCustomConfig(CustomConfigRequest request) throws Exception;

    /**
     * Delete a host configuration from Traefik with custom configuration
     * 
     * @param request Custom configuration request
     * @return Operation response with message and updated config
     * @throws Exception if operation fails
     */
    HostOperationResponse deleteHostWithCustomConfig(CustomConfigRequest request) throws Exception;

    /**
     * Get the current Traefik configuration with custom configuration
     * 
     * @param request Custom configuration request
     * @return Current configuration as string
     * @throws Exception if operation fails
     */
    String getCurrentConfigWithCustomConfig(CustomConfigRequest request) throws Exception;

    /**
     * Get the current Traefik configuration
     * 
     * @return Current configuration as string
     * @throws Exception if operation fails
     */
    String getCurrentConfig() throws Exception;

    /**
     * Extract service URLs from the current Traefik configuration
     * 
     * @return List of service information
     * @throws Exception if operation fails
     */
    java.util.List<com.traefikconfig.dto.ServiceInfo> getServiceUrls() throws Exception;

    /**
     * Extract service URLs from Traefik configuration with custom config
     * 
     * @param request Custom configuration request
     * @return List of service information
     * @throws Exception if operation fails
     */
    java.util.List<com.traefikconfig.dto.ServiceInfo> getServiceUrlsWithCustomConfig(CustomConfigRequest request)
            throws Exception;
}