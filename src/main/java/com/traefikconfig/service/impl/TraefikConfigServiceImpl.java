package com.traefikconfig.service.impl;

import com.traefikconfig.config.TraefikConfigProperties;
import com.traefikconfig.dto.CustomConfigRequest;
import com.traefikconfig.dto.HostOperationResponse;
import com.traefikconfig.service.TraefikConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.stream.Collectors;
import org.json.JSONObject;

@Service
public class TraefikConfigServiceImpl implements TraefikConfigService {

    private static final Logger logger = LoggerFactory.getLogger(TraefikConfigServiceImpl.class);

    public TraefikConfigServiceImpl(TraefikConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    private final TraefikConfigProperties configProperties;

    public HostOperationResponse addHost(String host) throws Exception {
        logger.info("🔄 Adding host: {} [app={}]", host, configProperties.getApplicationId());

        String currentConfig = fetchCurrentTraefikConfig(configProperties.getApplicationId());
        logger.info("📄 Current config before adding host:\n{}", currentConfig.replace("\\n", "\n"));
        String newRoutersBlock = generateNewRoutersBlock(host);
        String updatedConfig = mergeTraefikConfig(currentConfig, host, newRoutersBlock);

        if (updatedConfig == null) {
            logger.warn("⚠️ Host '{}' already exists", host);
            return new HostOperationResponse(
                    "Host '" + host + "' already exists in the configuration. No update needed.",
                    currentConfig,
                    host);
        }

        String responseBody = updateTraefikConfig(configProperties.getApplicationId(), updatedConfig);
        logger.info("✅ Host '{}' added successfully [config_size={}]", host, updatedConfig.length());
        logger.info("📄 Updated config after adding host:\n{}", updatedConfig.replace("\\n", "\n"));

        return new HostOperationResponse(
                "Host '" + host + "' added successfully! Response: " + responseBody,
                updatedConfig,
                host);
    }

    public HostOperationResponse deleteHost(String host) throws Exception {
        logger.info("🗑️ Deleting host: {} [app={}]", host, configProperties.getApplicationId());

        String currentConfig = fetchCurrentTraefikConfig(configProperties.getApplicationId());
        logger.info("📄 Current config before deleting host:\n{}", currentConfig.replace("\\n", "\n"));
        String updatedConfig = deleteHostRouters(currentConfig, host);

        if (updatedConfig.equals(currentConfig)) {
            logger.warn("⚠️ Host '{}' not found", host);
            return new HostOperationResponse(
                    "Host '" + host + "' not found in configuration. Nothing to delete.",
                    currentConfig,
                    host);
        }

        String responseBody = updateTraefikConfig(configProperties.getApplicationId(), updatedConfig);
        logger.info("✅ Host '{}' deleted successfully [config_size={}]", host, updatedConfig.length());
        logger.info("📄 Updated config after deleting host:\n{}", updatedConfig.replace("\\n", "\n"));

        return new HostOperationResponse(
                "Host '" + host + "' deleted successfully! Response: " + responseBody,
                updatedConfig,
                host);
    }

    public String getCurrentConfig() throws Exception {
        logger.info("📋 Fetching config [app={}]", configProperties.getApplicationId());
        String config = fetchCurrentTraefikConfig(configProperties.getApplicationId());
        logger.info("✅ Config retrieved [size={}]", config.length());
        logger.info("📄 Config content:\n{}", config.replace("\\n", "\n"));
        return config;
    }

    @Override
    public HostOperationResponse addHostWithCustomConfig(CustomConfigRequest request) throws Exception {
        String host = request.getHostname();
        String appId = request.getApplicationId() != null ? request.getApplicationId()
                : configProperties.getApplicationId();
        String apiDomain = request.getApiDomain() != null ? request.getApiDomain() : configProperties.getApiDomain();

        logger.info("🔄 Adding host with custom config: {} [app={}, domain={}]", host, appId, apiDomain);

        // Use custom config or fall back to defaults
        String apiKey = request.getDokployApiKey() != null ? request.getDokployApiKey()
                : configProperties.getDokployApiKey();
        String readUrl = apiDomain + "/api/application.readTraefikConfig";
        String updateUrl = apiDomain + "/api/application.updateTraefikConfig";
        // Handle single service configuration
        String backendService, frontendService;
        Integer servicePort = null;
        if (request.getServiceName() != null) {
            // Single service mode from request
            backendService = request.getServiceName();
            frontendService = request.getServiceName();
            servicePort = request.getServicePort() != null ? request.getServicePort() : 8080;
            logger.info("🔧 Using single service mode with service: {} on port: {}", request.getServiceName(), servicePort);
        } else if (configProperties.getSingleService() != null) {
            // Single service mode from properties
            backendService = configProperties.getSingleService();
            frontendService = configProperties.getSingleService();
            servicePort = configProperties.getSingleServicePort() != null ? configProperties.getSingleServicePort() : 8080;
            logger.info("🔧 Using single service mode from config with service: {} on port: {}", configProperties.getSingleService(), servicePort);
        } else {
            // Traditional dual service mode
            backendService = request.getBackendService() != null ? request.getBackendService()
                    : configProperties.getBackendService();
            frontendService = request.getFrontendService() != null ? request.getFrontendService()
                    : configProperties.getFrontendService();
            logger.info("🔧 Using dual service mode - Backend: {}, Frontend: {}", backendService, frontendService);
        }

        // Check if we should skip middlewares
        boolean skipMiddlewares = (request.getSkipMiddlewares() != null && request.getSkipMiddlewares()) ||
                                 (configProperties.getSkipMiddlewares() != null && configProperties.getSkipMiddlewares());

        String currentConfig = fetchCurrentTraefikConfigWithCustom(appId, readUrl, apiKey);
        logger.info("📄 Current config before adding host (custom):\n{}", currentConfig.replace("\\n", "\n"));
        String newRoutersBlock = generateNewRoutersBlockWithCustom(host, backendService, frontendService, servicePort, skipMiddlewares);
        String updatedConfig = mergeTraefikConfigWithServices(currentConfig, host, newRoutersBlock, backendService, frontendService, servicePort);

        if (updatedConfig == null) {
            logger.warn("⚠️ Host '{}' already exists [custom_config]", host);
            return new HostOperationResponse(
                    "Host '" + host + "' already exists in the configuration. No update needed.",
                    currentConfig,
                    host);
        }

        String responseBody = updateTraefikConfigWithCustom(appId, updatedConfig, updateUrl, apiKey);
        logger.info("✅ Host '{}' added with custom config [config_size={}]", host, updatedConfig.length());
        logger.info("📄 Updated config after adding host (custom):\n{}", updatedConfig.replace("\\n", "\n"));

        return new HostOperationResponse(
                "Host '" + host + "' added successfully with custom config! Response: " + responseBody,
                updatedConfig,
                host);
    }

    @Override
    public HostOperationResponse deleteHostWithCustomConfig(CustomConfigRequest request) throws Exception {
        String host = request.getHostname();
        String appId = request.getApplicationId() != null ? request.getApplicationId()
                : configProperties.getApplicationId();
        String apiDomain = request.getApiDomain() != null ? request.getApiDomain() : configProperties.getApiDomain();

        logger.info("🗑️ Deleting host with custom config: {} [app={}, domain={}]", host, appId, apiDomain);

        // Use custom config or fall back to defaults
        String apiKey = request.getDokployApiKey() != null ? request.getDokployApiKey()
                : configProperties.getDokployApiKey();
        String readUrl = apiDomain + "/api/application.readTraefikConfig";
        String updateUrl = apiDomain + "/api/application.updateTraefikConfig";

        String currentConfig = fetchCurrentTraefikConfigWithCustom(appId, readUrl, apiKey);
        logger.info("📄 Current config before deleting host (custom):\n{}", currentConfig.replace("\\n", "\n"));
        String updatedConfig = deleteHostRouters(currentConfig, host);

        if (updatedConfig.equals(currentConfig)) {
            logger.warn("⚠️ Host '{}' not found [custom_config]", host);
            return new HostOperationResponse(
                    "Host '" + host + "' not found in configuration. Nothing to delete.",
                    currentConfig,
                    host);
        }

        String responseBody = updateTraefikConfigWithCustom(appId, updatedConfig, updateUrl, apiKey);
        logger.info("✅ Host '{}' deleted with custom config [config_size={}]", host, updatedConfig.length());
        logger.info("📄 Updated config after deleting host (custom):\n{}", updatedConfig.replace("\\n", "\n"));

        return new HostOperationResponse(
                "Host '" + host + "' deleted successfully with custom config! Response: " + responseBody,
                updatedConfig,
                host);
    }

    @Override
    public String getCurrentConfigWithCustomConfig(CustomConfigRequest request) throws Exception {
        String appId = request.getApplicationId() != null ? request.getApplicationId()
                : configProperties.getApplicationId();
        String apiDomain = request.getApiDomain() != null ? request.getApiDomain() : configProperties.getApiDomain();

        logger.info("📋 Fetching config with custom settings [app={}, domain={}]", appId, apiDomain);

        String apiKey = request.getDokployApiKey() != null ? request.getDokployApiKey()
                : configProperties.getDokployApiKey();
        String readUrl = apiDomain + "/api/application.readTraefikConfig";

        String config = fetchCurrentTraefikConfigWithCustom(appId, readUrl, apiKey);
        logger.info("✅ Config retrieved with custom settings [size={}]", config.length());
        logger.info("📄 Config content (custom):\n{}", config.replace("\\n", "\n"));
        return config;
    }

    /**
     * Generates new router YAML blocks for a hostname.
     */
    private String generateNewRoutersBlock(String host) {
        logger.debug("🔧 Generating router blocks for host: {}", host);
        String subdomainPrefix = host.split("\\.")[0];
        logger.debug("📝 Extracted subdomain prefix: {}", subdomainPrefix);

        String routerNameBackend = String.format("%s-devcrm-crmbackend-router", subdomainPrefix);
        String routerNameFrontend = String.format("%s-devcrm-crmfrontend-router", subdomainPrefix);

        logger.debug("🔗 Generated router names - Backend: {}, Frontend: {}", routerNameBackend, routerNameFrontend);

        String routerBlock = String.format("""
                # Backend API routes for %s
                %s:
                  rule: Host(`%s`) && PathPrefix(`/api`)
                  service: %s
                  middlewares:
                    - redirect-to-https

                %s-websecure:
                  rule: Host(`%s`) && PathPrefix(`/api`)
                  service: %s
                  middlewares: []
                  tls:
                    certResolver: letsencrypt

                # Frontend routes for %s
                %s:
                  rule: Host(`%s`) && !PathPrefix(`/api`)
                  service: %s
                  middlewares:
                    - redirect-to-https

                %s-websecure:
                  rule: Host(`%s`) && !PathPrefix(`/api`)
                  service: %s
                  middlewares: []
                  tls:
                    certResolver: letsencrypt

                """,
                host,
                routerNameBackend, host, configProperties.getBackendService(),
                routerNameBackend, host, configProperties.getBackendService(),
                host,
                routerNameFrontend, host, configProperties.getFrontendService(),
                routerNameFrontend, host, configProperties.getFrontendService());

        logger.debug("✅ Router block generated successfully (length: {} characters)", routerBlock.length());
        return routerBlock;
    }

    /**
     * Merge routers and services into http configuration.
     */
    private String mergeTraefikConfigWithServices(String currentConfig, String hostToAdd, String newRoutersBlock, 
                                                 String backendService, String frontendService, Integer servicePort) {
        logger.debug("🔍 Checking if host '{}' already exists in configuration", hostToAdd);
        if (currentConfig.contains("Host(`" + hostToAdd + "`)")) {
            logger.warn("⚠️ Host '{}' already exists in configuration", hostToAdd);
            return null; // Host already exists
        }

        // Check if we're in single service mode
        boolean isSingleService = backendService != null && frontendService != null && 
                                 backendService.equals(frontendService) && servicePort != null;

        String configWithRouters = mergeTraefikConfig(currentConfig, hostToAdd, newRoutersBlock);
        
        if (isSingleService) {
            // Add service definition for single service mode
            String subdomainPrefix = hostToAdd.split("\\.")[0];
            String serviceNameForConfig = String.format("%s-service", subdomainPrefix);
            int port = servicePort != null ? servicePort : 8080;
            
            String serviceBlock = String.format("""
                    %s:
                      loadBalancer:
                        servers:
                        - url: http://%s:%d
                        passHostHeader: true

                    """, serviceNameForConfig, backendService, port);
            
            configWithRouters = mergeServiceConfig(configWithRouters, serviceBlock);
        }
        
        return configWithRouters;
    }

    /**
     * Merge routers into http.routers section.
     */
    private String mergeTraefikConfig(String currentConfig, String hostToAdd, String newRoutersBlock) {
        logger.debug("🔍 Checking if host '{}' already exists in configuration", hostToAdd);
        if (currentConfig.contains("Host(`" + hostToAdd + "`)")) {
            logger.warn("⚠️ Host '{}' already exists in configuration", hostToAdd);
            return null; // Host already exists
        }

        logger.debug("🔍 Looking for 'routers:' section in configuration");
        int routersIndex = currentConfig.indexOf("\n  routers:");
        if (routersIndex == -1) {
            logger.error("❌ Invalid config: 'routers:' section not found");
            throw new IllegalArgumentException("Invalid config: 'routers:' section not found.");
        }
        logger.debug("✅ Found 'routers:' section at index: {}", routersIndex);

        // Find the next section after routers (could be middlewares, services, or other sections)
        int nextSectionIndex = findNextSectionAfterRouters(currentConfig, routersIndex);
        
        logger.debug("🔧 Indenting new router blocks");
        String indentedRouters = newRoutersBlock.lines()
                .map(line -> "    " + line)
                .collect(Collectors.joining("\n"));

        String beforeInsert = currentConfig.substring(0, nextSectionIndex);
        String afterInsert = currentConfig.substring(nextSectionIndex);

        String mergedConfig = beforeInsert.stripTrailing() + "\n" + indentedRouters + "\n" + afterInsert;
        logger.debug("✅ Configuration merged successfully (new length: {} characters)", mergedConfig.length());

        return mergedConfig;
    }

    /**
     * Find the next section after routers (middlewares, services, etc.)
     */
    private int findNextSectionAfterRouters(String currentConfig, int routersIndex) {
        String[] possibleSections = {"\n  middlewares:", "\n  services:", "\nentryPoints:", "\ncertificatesResolvers:", "\napi:", "\nlog:"};
        
        for (String section : possibleSections) {
            int sectionIndex = currentConfig.indexOf(section, routersIndex);
            if (sectionIndex != -1) {
                logger.debug("✅ Found next section '{}' at index: {}", section.trim(), sectionIndex);
                return sectionIndex;
            }
        }
        
        // If no section found, insert at the end of the file
        logger.debug("⚠️ No next section found after routers, inserting at end of file");
        return currentConfig.length();
    }

    /**
     * Merge service definition into http.services section.
     */
    private String mergeServiceConfig(String currentConfig, String newServiceBlock) {
        logger.debug("🔍 Looking for 'services:' section in configuration");
        int servicesIndex = currentConfig.indexOf("\n  services:");
        
        if (servicesIndex == -1) {
            // Services section doesn't exist, create it
            logger.debug("⚠️ Services section not found, creating new services section");
            return createServicesSection(currentConfig, newServiceBlock);
        }
        
        logger.debug("✅ Found 'services:' section at index: {}", servicesIndex);

        // Find the end of services section (next top-level section or end of file)
        int nextSectionIndex = currentConfig.length();
        String[] possibleSections = {"\nentryPoints:", "\ncertificatesResolvers:", "\napi:", "\nlog:"};
        for (String section : possibleSections) {
            int sectionIndex = currentConfig.indexOf(section, servicesIndex);
            if (sectionIndex != -1 && sectionIndex < nextSectionIndex) {
                nextSectionIndex = sectionIndex;
            }
        }

        logger.debug("🔧 Indenting new service block");
        String indentedService = newServiceBlock.lines()
                .map(line -> "    " + line)
                .collect(Collectors.joining("\n"));

        String beforeInsert = currentConfig.substring(0, nextSectionIndex);
        String afterInsert = currentConfig.substring(nextSectionIndex);

        String mergedConfig = beforeInsert.stripTrailing() + "\n" + indentedService + "\n" + afterInsert;
        logger.debug("✅ Service configuration merged successfully (new length: {} characters)", mergedConfig.length());

        return mergedConfig;
    }

    /**
     * Create a new services section in the configuration.
     */
    private String createServicesSection(String currentConfig, String newServiceBlock) {
        logger.debug("🔧 Creating new services section");
        
        // Find where to insert the services section (after routers, before other sections)
        int httpIndex = currentConfig.indexOf("http:");
        if (httpIndex == -1) {
            throw new IllegalArgumentException("Invalid config: 'http:' section not found.");
        }
        
        // Find the end of the http section or a good place to insert services
        String[] possibleSections = {"\nentryPoints:", "\ncertificatesResolvers:", "\napi:", "\nlog:"};
        int insertIndex = currentConfig.length();
        
        for (String section : possibleSections) {
            int sectionIndex = currentConfig.indexOf(section, httpIndex);
            if (sectionIndex != -1 && sectionIndex < insertIndex) {
                insertIndex = sectionIndex;
            }
        }
        
        logger.debug("🔧 Indenting new service block for new services section");
        String indentedService = newServiceBlock.lines()
                .map(line -> "    " + line)
                .collect(Collectors.joining("\n"));
        
        String servicesSection = "\n  services:\n" + indentedService + "\n";
        
        String beforeInsert = currentConfig.substring(0, insertIndex);
        String afterInsert = currentConfig.substring(insertIndex);
        
        String mergedConfig = beforeInsert.stripTrailing() + servicesSection + afterInsert;
        logger.debug("✅ New services section created successfully (new length: {} characters)", mergedConfig.length());
        
        return mergedConfig;
    }

    /**
     * Delete all router blocks for a hostname.
     */
    private String deleteHostRouters(String currentConfig, String host) {
        logger.debug("🗑️ Starting deletion of router blocks for host: {}", host);
        String subdomainPrefix = host.split("\\.")[0];
        logger.debug("📝 Subdomain prefix to match: {}", subdomainPrefix);

        StringBuilder result = new StringBuilder();
        boolean skip = false;
        int deletedLines = 0;
        int totalLines = 0;

        for (String line : currentConfig.split("\n")) {
            totalLines++;

            // Start skipping if comment or router name mentions this host
            if (line.contains("Host(`" + host + "`)") ||
                    line.contains("# Backend API routes for " + host) ||
                    line.contains("# Frontend routes for " + host) ||
                    line.contains("# Routes for " + host) ||
                    line.trim().startsWith(subdomainPrefix + "-devcrm-") ||
                    line.trim().startsWith(subdomainPrefix + "-router")) {

                logger.debug("🎯 Found line to delete: {}", line.trim());
                skip = true;
                deletedLines++;
                continue;
            }

            if (skip) {
                // Stop skipping once we reach the next top-level router/middleware/service
                if (line.startsWith("  ") && !line.startsWith("    ")) {
                    logger.debug("🔄 Reached next section, stopping deletion");
                    skip = false;
                    result.append(line).append("\n");
                } else {
                    deletedLines++;
                }
                continue; // don't append skipped lines
            }

            result.append(line).append("\n");
        }

        logger.debug("✅ Deletion complete - Processed {} lines, deleted {} lines", totalLines, deletedLines);
        return result.toString().trim();
    }

    /**
     * Fetch current Traefik config from Dokploy.
     */
    private String fetchCurrentTraefikConfig(String applicationId) throws IOException, InterruptedException {
        logger.debug("🌐 Making HTTP GET request to fetch configuration");
        logger.debug("📡 URL: {}?applicationId={}", configProperties.getReadApiUrl(), applicationId);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        String readUrl = configProperties.getReadApiUrl() + "?applicationId=" + applicationId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(readUrl))
                .header("accept", "application/json")
                .header("x-api-key", configProperties.getDokployApiKey())
                .GET()
                .build();

        logger.debug("📤 Sending request to Dokploy API...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("📥 Received response with status: {}", response.statusCode());

        if (response.statusCode() != 200) {
            logger.error("❌ Failed to fetch config. HTTP status: {}, Response: {}",
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch config. HTTP status: " + response.statusCode() +
                    "\nResponse: " + response.body());
        }

        String body = response.body().trim();
        logger.debug("📄 Raw response body length: {} characters", body.length());

        if (body.startsWith("\"") && body.endsWith("\"")) {
            body = body.substring(1, body.length() - 1);
            logger.debug("🔧 Removed surrounding quotes from response");
        }

        String processedBody = body.replace("\\n", "\n");
        logger.debug("✅ Configuration fetched and processed successfully");
        return processedBody;
    }

    /**
     * Send updated Traefik config to Dokploy.
     */
    private String updateTraefikConfig(String applicationId, String traefikConfig)
            throws IOException, InterruptedException {
        logger.debug("🌐 Making HTTP POST request to update configuration");
        logger.debug("📡 URL: {}", configProperties.getUpdateApiUrl());
        logger.debug("📄 Config length to send: {} characters", traefikConfig.length());

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        String jsonPayload = new JSONObject()
                .put("applicationId", applicationId)
                .put("traefikConfig", traefikConfig)
                .toString();

        logger.debug("📦 JSON payload size: {} characters", jsonPayload.length());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(configProperties.getUpdateApiUrl()))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("x-api-key", configProperties.getDokployApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        logger.debug("📤 Sending update request to Dokploy API...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("📥 Received response with status: {}", response.statusCode());

        if (response.statusCode() != 200) {
            logger.error("❌ Failed to update config. HTTP status: {}, Response: {}",
                    response.statusCode(), response.body());
            throw new IOException("Failed to update config. HTTP status: " + response.statusCode() +
                    "\nResponse: " + response.body());
        }

        logger.debug("✅ Configuration updated successfully");
        return response.body();
    }

    /**
     * Generates new router YAML blocks for a hostname with custom services.
     */
    private String generateNewRoutersBlockWithCustom(String host, String backendService, String frontendService, Integer servicePort, boolean skipMiddlewares) {
        logger.debug("🔧 Generating router blocks for host: {} with custom services", host);
        String subdomainPrefix = host.split("\\.")[0];
        logger.debug("📝 Extracted subdomain prefix: {}", subdomainPrefix);

        // Check if we have both services or just one
        boolean hasBothServices = backendService != null && frontendService != null && 
                                 !backendService.equals(frontendService);
        
        if (hasBothServices) {
            // Original logic for separate backend and frontend services
            String routerNameBackend = String.format("%s-devcrm-crmbackend-router", subdomainPrefix);
            String routerNameFrontend = String.format("%s-devcrm-crmfrontend-router", subdomainPrefix);

            logger.debug("🔗 Generated router names - Backend: {}, Frontend: {}", routerNameBackend, routerNameFrontend);
            logger.debug("🔧 Using custom services - Backend: {}, Frontend: {}", backendService, frontendService);

            String routerBlock;
            if (skipMiddlewares) {
                // Generate routers without middlewares
                routerBlock = String.format("""
                        # Backend API routes for %s
                        %s:
                          rule: Host(`%s`) && PathPrefix(`/api`)
                          service: %s
                          entryPoints:
                            - web

                        %s-websecure:
                          rule: Host(`%s`) && PathPrefix(`/api`)
                          service: %s
                          entryPoints:
                            - websecure
                          tls:
                            certResolver: letsencrypt

                        # Frontend routes for %s
                        %s:
                          rule: Host(`%s`) && !PathPrefix(`/api`)
                          service: %s
                          entryPoints:
                            - web

                        %s-websecure:
                          rule: Host(`%s`) && !PathPrefix(`/api`)
                          service: %s
                          entryPoints:
                            - websecure
                          tls:
                            certResolver: letsencrypt

                        """,
                        host,
                        routerNameBackend, host, backendService,
                        routerNameBackend, host, backendService,
                        host,
                        routerNameFrontend, host, frontendService,
                        routerNameFrontend, host, frontendService);
            } else {
                // Generate routers with middlewares (original logic)
                routerBlock = String.format("""
                        # Backend API routes for %s
                        %s:
                          rule: Host(`%s`) && PathPrefix(`/api`)
                          service: %s
                          middlewares:
                            - redirect-to-https

                        %s-websecure:
                          rule: Host(`%s`) && PathPrefix(`/api`)
                          service: %s
                          middlewares: []
                          tls:
                            certResolver: letsencrypt

                        # Frontend routes for %s
                        %s:
                          rule: Host(`%s`) && !PathPrefix(`/api`)
                          service: %s
                          middlewares:
                            - redirect-to-https

                        %s-websecure:
                          rule: Host(`%s`) && !PathPrefix(`/api`)
                          service: %s
                          middlewares: []
                          tls:
                            certResolver: letsencrypt

                        """,
                        host,
                        routerNameBackend, host, backendService,
                        routerNameBackend, host, backendService,
                        host,
                        routerNameFrontend, host, frontendService,
                        routerNameFrontend, host, frontendService);
            }

            logger.debug("✅ Router block generated successfully with custom services (length: {} characters)",
                    routerBlock.length());
            return routerBlock;
        } else {
            // Single service configuration - generate only 2 routers (HTTP and HTTPS) + service definition
            String serviceName = backendService != null ? backendService : frontendService;
            String routerName = String.format("%s-router", subdomainPrefix);
            String serviceNameForConfig = String.format("%s-service", subdomainPrefix);
            int port = servicePort != null ? servicePort : 8080;
            
            logger.debug("🔗 Generated single router name: {}", routerName);
            logger.debug("🔧 Using single service: {} on port: {}", serviceName, port);

            String routerBlock = String.format("""
                    # Routes for %s
                    %s:
                      rule: Host(`%s`)
                      service: %s
                      entryPoints:
                        - web

                    %s-websecure:
                      rule: Host(`%s`)
                      service: %s
                      entryPoints:
                        - websecure
                      tls:
                        certResolver: letsencrypt

                    """,
                    host,
                    routerName, host, serviceNameForConfig,
                    routerName, host, serviceNameForConfig);

            logger.debug("✅ Single service router block generated successfully (length: {} characters)",
                    routerBlock.length());
            return routerBlock;
        }
    }

    /**
     * Fetch current Traefik config from Dokploy with custom parameters.
     */
    private String fetchCurrentTraefikConfigWithCustom(String applicationId, String readApiUrl, String apiKey)
            throws IOException, InterruptedException {
        logger.debug("🌐 Making HTTP GET request to fetch configuration with custom params");
        logger.debug("📡 URL: {}?applicationId={}", readApiUrl, applicationId);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        String readUrl = readApiUrl + "?applicationId=" + applicationId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(readUrl))
                .header("accept", "application/json")
                .header("x-api-key", apiKey)
                .GET()
                .build();

        logger.debug("📤 Sending request to Dokploy API with custom config...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("📥 Received response with status: {}", response.statusCode());

        if (response.statusCode() != 200) {
            logger.error("❌ Failed to fetch config with custom params. HTTP status: {}, Response: {}",
                    response.statusCode(), response.body());
            throw new IOException("Failed to fetch config. HTTP status: " + response.statusCode() +
                    "\nResponse: " + response.body());
        }

        String body = response.body().trim();
        logger.debug("📄 Raw response body length: {} characters", body.length());

        if (body.startsWith("\"") && body.endsWith("\"")) {
            body = body.substring(1, body.length() - 1);
            logger.debug("🔧 Removed surrounding quotes from response");
        }

        String processedBody = body.replace("\\n", "\n");
        logger.debug("✅ Configuration fetched and processed successfully with custom config");
        return processedBody;
    }

    /**
     * Send updated Traefik config to Dokploy with custom parameters.
     */
    private String updateTraefikConfigWithCustom(String applicationId, String traefikConfig, String updateApiUrl,
            String apiKey)
            throws IOException, InterruptedException {
        logger.debug("🌐 Making HTTP POST request to update configuration with custom params");
        logger.debug("📡 URL: {}", updateApiUrl);
        logger.debug("📄 Config length to send: {} characters", traefikConfig.length());

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        String jsonPayload = new JSONObject()
                .put("applicationId", applicationId)
                .put("traefikConfig", traefikConfig)
                .toString();

        logger.debug("📦 JSON payload size: {} characters", jsonPayload.length());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(updateApiUrl))
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        logger.debug("📤 Sending update request to Dokploy API with custom config...");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        logger.debug("📥 Received response with status: {}", response.statusCode());

        if (response.statusCode() != 200) {
            logger.error("❌ Failed to update config with custom params. HTTP status: {}, Response: {}",
                    response.statusCode(), response.body());
            throw new IOException("Failed to update config. HTTP status: " + response.statusCode() +
                    "\nResponse: " + response.body());
        }

        logger.debug("✅ Configuration updated successfully with custom config");
        return response.body();
    }

    @Override
    public java.util.List<com.traefikconfig.dto.ServiceInfo> getServiceUrls() throws Exception {
        long timestamp = System.currentTimeMillis();
        logger.info("🔍 [{}] Extracting service URLs [app={}]", timestamp, configProperties.getApplicationId());
        String config = fetchCurrentTraefikConfig(configProperties.getApplicationId());
        logger.info("📄 [{}] Config for service extraction:\n{}", timestamp, config.replace("\\n", "\n"));
        java.util.List<com.traefikconfig.dto.ServiceInfo> services = parseServiceUrls(config);
        logger.info("✅ [{}] Extracted {} services", timestamp, services.size());
        for (com.traefikconfig.dto.ServiceInfo service : services) {
            logger.info("   🔗 [{}] Service: {} -> {}", timestamp, service.getServiceName(), service.getServiceUrl());
        }
        return services;
    }

    @Override
    public java.util.List<com.traefikconfig.dto.ServiceInfo> getServiceUrlsWithCustomConfig(CustomConfigRequest request)
            throws Exception {
        String appId = request.getApplicationId() != null ? request.getApplicationId()
                : configProperties.getApplicationId();
        String apiDomain = request.getApiDomain() != null ? request.getApiDomain() : configProperties.getApiDomain();

        logger.info("🔍 Extracting service URLs with custom config [app={}, domain={}]", appId, apiDomain);

        String apiKey = request.getDokployApiKey() != null ? request.getDokployApiKey()
                : configProperties.getDokployApiKey();
        String readUrl = apiDomain + "/api/application.readTraefikConfig";

        String config = fetchCurrentTraefikConfigWithCustom(appId, readUrl, apiKey);
        logger.info("📄 Config for service extraction (custom):\n{}", config.replace("\\n", "\n"));
        java.util.List<com.traefikconfig.dto.ServiceInfo> services = parseServiceUrls(config);
        logger.info("✅ Extracted {} services with custom config", services.size());
        return services;
    }

    /**
     * Parse service URLs from Traefik configuration YAML
     */
    private java.util.List<com.traefikconfig.dto.ServiceInfo> parseServiceUrls(String config) {
        java.util.List<com.traefikconfig.dto.ServiceInfo> services = new java.util.ArrayList<>();

        try {
            logger.info("🔍 Raw config length: {} chars", config.length());
            logger.info("🔍 Config contains 'services:': {}", config.contains("services:"));
            logger.info("🔍 Config contains 'devcrm-crmbackend-service:': {}",
                    config.contains("devcrm-crmbackend-service:"));

            String[] lines = config.split("\n");
            boolean inServicesSection = false;
            String currentServiceName = null;
            String currentServiceUrl = null;
            String currentLoadBalancer = "roundrobin"; // Default load balancer

            logger.info("🔍 Starting to parse {} lines for services", lines.length);

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String trimmedLine = line.trim();

                if (i < 10 || trimmedLine.contains("services") || trimmedLine.contains("url") || inServicesSection) {
                    String indent = line.length() > line.trim().length()
                            ? line.substring(0, line.length() - line.trim().length()).replace(" ", "·")
                            : "";
                    logger.info("Line {}: '{}' (inServices: {}, currentService: {}, indent: '{}')",
                            i, trimmedLine, inServicesSection, currentServiceName, indent);
                }

                if (trimmedLine.equals("services:")) {
                    inServicesSection = true;
                    logger.info("📍 Found services section at line {}", i);
                    continue;
                }

                // If we're in services section, look for service names (any indentation ending
                // with :)
                if (inServicesSection) {
                    // Save previous service if complete
                    if (currentServiceName != null && currentServiceUrl != null && line.startsWith("    ")
                            && !line.startsWith("      ")) {
                        logger.info("✅ Adding service: {} -> {}", currentServiceName, currentServiceUrl);
                        services.add(new com.traefikconfig.dto.ServiceInfo(currentServiceName, currentServiceUrl,
                                currentLoadBalancer));
                        currentServiceName = null;
                        currentServiceUrl = null;
                        currentLoadBalancer = "roundrobin";
                    }

                    // Check if this is a new service name (4 spaces indentation, ends with :)
                    if (line.startsWith("    ") && !line.startsWith("      ") && trimmedLine.endsWith(":") &&
                            !trimmedLine.contains("loadBalancer") && !trimmedLine.contains("servers") &&
                            !trimmedLine.contains("middlewares") && !trimmedLine.contains("passHostHeader")) {
                        currentServiceName = trimmedLine.substring(0, trimmedLine.length() - 1);
                        currentServiceUrl = null;
                        currentLoadBalancer = "roundrobin";
                        logger.info("🔧 New service found: {}", currentServiceName);
                        continue;
                    }

                    // Check if we've left the services section (back to top level)
                    if (!line.startsWith("  ") && !line.trim().isEmpty()) {
                        // Save final service before leaving
                        if (currentServiceName != null && currentServiceUrl != null) {
                            logger.info("✅ Adding final service before leaving: {} -> {}", currentServiceName,
                                    currentServiceUrl);
                            services.add(new com.traefikconfig.dto.ServiceInfo(currentServiceName, currentServiceUrl,
                                    currentLoadBalancer));
                        }
                        inServicesSection = false;
                        currentServiceName = null;
                        logger.info("🚪 Left services section");
                        continue;
                    }
                }

                // Parse within service definition
                if (inServicesSection && currentServiceName != null) {
                    if (trimmedLine.startsWith("- url:")) {
                        String url = trimmedLine.substring(6).trim();
                        if (url.startsWith("\"") && url.endsWith("\"")) {
                            url = url.substring(1, url.length() - 1);
                        }
                        if (url.startsWith("'") && url.endsWith("'")) {
                            url = url.substring(1, url.length() - 1);
                        }
                        currentServiceUrl = url;
                        logger.info("🔗 Found URL for {}: {}", currentServiceName, currentServiceUrl);
                        continue;
                    }
                }
            }

            // Don't forget the last service
            if (currentServiceName != null && currentServiceUrl != null) {
                logger.info("✅ Adding final service: {} -> {}", currentServiceName, currentServiceUrl);
                services.add(new com.traefikconfig.dto.ServiceInfo(currentServiceName, currentServiceUrl,
                        currentLoadBalancer));
            }

            logger.info("🎯 Parsing complete. Found {} services total", services.size());

        } catch (Exception e) {
            logger.error("❌ Error parsing service URLs: {}", e.getMessage(), e);
        }

        return services;
    }
}
