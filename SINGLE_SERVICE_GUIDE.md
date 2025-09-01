# Single Service Configuration Guide

This application now supports both traditional dual-service configurations (separate backend and frontend services) and simplified single-service configurations.

## Configuration Types

### 1. Traditional Dual Service (Default)
Creates 4 routers: 2 for backend API routes, 2 for frontend routes
```yaml
traefik:
  config:
    backend-service: "devcrm-crmbackend-service"
    frontend-service: "devcrm-crmfrontend-service"
```

### 2. Single Service Configuration
Creates 2 routers: 1 for HTTP, 1 for HTTPS (both pointing to the same service)
```yaml
traefik:
  config:
    single-service: "your-single-service-name"
    single-service-port: 8080
    skip-middlewares: true
```

### 3. Skip Middlewares Option
For configurations that don't have a middlewares section, you can use `skip-middlewares: true` to:
- Generate routers without middleware references
- Handle configurations that lack a middlewares section
- Create cleaner configurations for simple setups

## API Usage

### Using Custom Config Endpoints

#### Single Service Mode
```json
{
  "hostname": "example.com",
  "serviceName": "traefikconfigmaster-1dl4tc-service-29",
  "servicePort": 8080,
  "skipMiddlewares": true,
  "applicationId": "your-app-id",
  "dokployApiKey": "your-api-key",
  "apiDomain": "https://your-domain.com"
}
```

#### Dual Service Mode (Traditional)
```json
{
  "hostname": "example.com",
  "backendService": "backend-service-name",
  "frontendService": "frontend-service-name",
  "applicationId": "your-app-id",
  "dokployApiKey": "your-api-key",
  "apiDomain": "https://your-domain.com"
}
```

## Generated Configuration Examples

### Single Service Output
```yaml
http:
  routers:
    # Routes for example.com
    example-router:
      rule: Host(`example.com`)
      service: example-service
      entryPoints:
        - web

    example-router-websecure:
      rule: Host(`example.com`)
      service: example-service
      entryPoints:
        - websecure
      tls:
        certResolver: letsencrypt

  services:
    example-service:
      loadBalancer:
        servers:
        - url: http://traefikconfigmaster-1dl4tc-service-29:8080
        passHostHeader: true
```

### Dual Service Output (Traditional)
```yaml
http:
  routers:
    # Backend API routes for example.com
    example-devcrm-crmbackend-router:
      rule: Host(`example.com`) && PathPrefix(`/api`)
      service: devcrm-crmbackend-service
      middlewares:
        - redirect-to-https

    example-devcrm-crmbackend-router-websecure:
      rule: Host(`example.com`) && PathPrefix(`/api`)
      service: devcrm-crmbackend-service
      middlewares: []
      tls:
        certResolver: letsencrypt

    # Frontend routes for example.com
    example-devcrm-crmfrontend-router:
      rule: Host(`example.com`) && !PathPrefix(`/api`)
      service: devcrm-crmfrontend-service
      middlewares:
        - redirect-to-https

    example-devcrm-crmfrontend-router-websecure:
      rule: Host(`example.com`) && !PathPrefix(`/api`)
      service: devcrm-crmfrontend-service
      middlewares: []
      tls:
        certResolver: letsencrypt
```

## API Endpoints

All existing endpoints support both modes:

- `POST /api/traefik/add-host-custom` - Add host with custom configuration
- `DELETE /api/traefik/delete-host-custom` - Delete host with custom configuration
- `POST /api/traefik/config-custom` - Get current configuration with custom settings

The system automatically detects which mode to use based on the provided parameters:
- If `serviceName` is provided → Single service mode
- If `backendService` and/or `frontendService` are provided → Dual service mode
- If `single-service` is configured in application.yml → Single service mode (default)