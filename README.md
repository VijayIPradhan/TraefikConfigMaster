# Traefik Config Manager

A Spring Boot application for managing Traefik configurations via REST API with proper layered architecture.

## Architecture

- **Controller Layer**: REST endpoints for API communication
- **Service Layer**: Business logic interface and implementation
- **DTO Layer**: Data transfer objects for request/response handling
- **Validation**: Input validation using Bean Validation

## Project Structure

```
src/main/java/com/traefikconfig/
├── TraefikConfigApplication.java          # Main Spring Boot application
├── controller/
│   └── TraefikConfigController.java       # REST API endpoints
├── service/
│   ├── TraefikConfigService.java          # Service interface
│   └── impl/
│       └── TraefikConfigServiceImpl.java  # Service implementation
└── dto/
    ├── ApiResponse.java                   # Generic API response wrapper
    ├── ConfigResponse.java                # Configuration response DTO
    └── HostRequest.java                   # Host request DTO with validation
```

## Features

- Add new host configurations to Traefik
- Delete existing host configurations
- Retrieve current Traefik configuration
- **Dynamic configuration management** - Update API keys, URLs, and service names at runtime
- RESTful API endpoints with proper DTOs
- Input validation
- Structured error handling
- Comprehensive logging with emojis
- Swagger/OpenAPI documentation
- Interactive API testing interface

## API Endpoints

All endpoints return responses in the following format:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

### Add Host
```
POST /api/traefik/add-host
Content-Type: application/json

{
  "hostname": "example.com"
}
```

### Delete Host
```
DELETE /api/traefik/delete-host
Content-Type: application/json

{
  "hostname": "example.com"
}
```

### Get Current Config
```
GET /api/traefik/config
```

## Running the Application

### Prerequisites
- Java 17+ installed
- JAVA_HOME environment variable set

### Option 1: Using Maven Wrapper (Recommended)
```cmd
mvnw.cmd spring-boot:run
```

### Option 2: Using Maven (if installed)
```cmd
mvn spring-boot:run
```

### Option 3: Using IDE
- Import the project into your IDE (IntelliJ IDEA, Eclipse, VS Code)
- Run the `TraefikConfigApplication.java` main method

### Option 4: Build and Run JAR
```cmd
mvnw.cmd clean package
java -jar target/traefik-config-manager-1.0.0.jar
```

The application will start on port 8080

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

The Swagger UI provides an interactive interface where you can:
- View all available endpoints
- See request/response schemas
- Test API endpoints directly from the browser
- View example requests and responses

## Testing the API

You can test the endpoints using curl:

```bash
# Add a host
curl -X POST http://localhost:8080/api/traefik/add-host \
  -H "Content-Type: application/json" \
  -d '{"hostname": "test.example.com"}'

# Get current config
curl http://localhost:8080/api/traefik/config

# Delete a host
curl -X DELETE http://localhost:8080/api/traefik/delete-host \
  -H "Content-Type: application/json" \
  -d '{"hostname": "test.example.com"}'

# Get current configuration properties
curl http://localhost:8080/api/config/current

# Update configuration properties
curl -X PUT http://localhost:8080/api/config/update \
  -H "Content-Type: application/json" \
  -d '{"applicationId": "new-app-id", "backendService": "new-backend-service"}'

# Reset configuration to defaults
curl -X POST http://localhost:8080/api/config/reset

# Add host with custom configuration
curl -X POST http://localhost:8080/api/traefik/add-host-custom \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "custom.example.com",
    "dokployApiKey": "custom-api-key",
    "apiDomain": "https://custom.imvj.in",
    "applicationId": "custom-app-id",
    "backendService": "custom-backend-service",
    "frontendService": "custom-frontend-service"
  }'

# Delete host with custom configuration
curl -X DELETE http://localhost:8080/api/traefik/delete-host-custom \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "custom.example.com",
    "dokployApiKey": "custom-api-key",
    "apiDomain": "https://custom.imvj.in",
    "applicationId": "custom-app-id"
  }'

# Get configuration with custom settings
curl -X POST http://localhost:8080/api/traefik/config-custom \
  -H "Content-Type: application/json" \
  -d '{
    "dokployApiKey": "custom-api-key",
    "apiDomain": "https://custom.imvj.in",
    "applicationId": "custom-app-id"
  }'

# Get service URLs from configuration
curl http://localhost:8080/api/traefik/services

# Get service URLs with custom configuration
curl -X POST http://localhost:8080/api/traefik/services-custom \
  -H "Content-Type: application/json" \
  -d '{
    "apiDomain": "https://custom.imvj.in",
    "applicationId": "custom-app-id"
  }'
```

## API Documentation Features

### Swagger UI Highlights
- **Interactive Testing**: Test all endpoints directly from the browser
- **Request Examples**: Pre-filled example requests for easy testing
- **Response Schemas**: Detailed response structure documentation
- **Validation Info**: See all validation rules and constraints
- **Error Examples**: Sample error responses for different scenarios

### Available Endpoints in Swagger
- `GET /api/health` - Application health check
- `GET /api/traefik/config` - Retrieve current Traefik configuration
- `POST /api/traefik/add-host` - Add new hostname to configuration
- `DELETE /api/traefik/delete-host` - Remove hostname from configuration
- `POST /api/traefik/add-host-custom` - Add hostname with custom configuration
- `DELETE /api/traefik/delete-host-custom` - Remove hostname with custom configuration
- `POST /api/traefik/config-custom` - Get configuration with custom settings
- `GET /api/traefik/services` - Extract service URLs from configuration
- `POST /api/traefik/services-custom` - Extract service URLs with custom config
- `GET /api/config/current` - Get current configuration properties
- `PUT /api/config/update` - Update configuration properties
- `POST /api/config/reset` - Reset configuration to defaults

## Response Examples

### Success Response
```json
{
  "success": true,
  "message": "Traefik configuration updated successfully!",
  "data": null
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error: Hostname is required",
  "data": null
}
```

### Config Response
```json
{
  "success": true,
  "message": "Configuration retrieved successfully",
  "data": {
    "config": "http:\n  routers:\n    ..."
  }
}
```

## Configuration Management

The application now supports dynamic configuration management, allowing you to update API keys, URLs, and service names without restarting the application.

### Configuration Properties

All configuration is stored in `application.yml` under the `traefik.config` section:

```yaml
traefik:
  config:
    dokploy-api-key: "your-api-key"
    api-domain: "https://your-domain.com"
    application-id: "your-application-id"
    backend-service: "your-backend-service"
    frontend-service: "your-frontend-service"
```

### Runtime Configuration Updates

You can update these properties at runtime using the configuration API:

```bash
# Update specific properties
curl -X PUT http://localhost:8080/api/config/update \
  -H "Content-Type: application/json" \
  -d '{
    "dokployApiKey": "new-api-key",
    "apiDomain": "https://new-domain.com",
    "applicationId": "new-app-id",
    "backendService": "new-backend-service"
  }'

# Get current configuration
curl http://localhost:8080/api/config/current

# Reset to defaults
curl -X POST http://localhost:8080/api/config/reset
```

### Benefits

- **No Restart Required**: Update configuration without stopping the application
- **Partial Updates**: Only update the properties you need to change
- **Reset Capability**: Easily revert to default configuration
- **Full Logging**: All configuration changes are logged
- **Swagger Documentation**: Interactive testing of configuration endpoints

## Service URL Extraction

The application can extract and parse service URLs from the Traefik configuration, helping you discover where services like `http://devcrm-crmbackend-4qxr51:8070` are defined.

### Service Information Extracted

- **Service Name**: The name of the service in Traefik configuration
- **Service URL**: The actual URL where the service is running
- **Load Balancer**: The load balancing method (e.g., roundrobin)

### Example Service Response

```json
{
  "success": true,
  "message": "Service URLs retrieved successfully",
  "data": [
    {
      "serviceName": "devcrm-crmbackend-service",
      "serviceUrl": "http://devcrm-crmbackend-4qxr51:8070",
      "loadBalancer": "roundrobin"
    },
    {
      "serviceName": "devcrm-crmfrontend-service", 
      "serviceUrl": "http://devcrm-crmfrontend-abc123:3000",
      "loadBalancer": "roundrobin"
    }
  ]
}
```

## Custom Configuration Endpoints

The application also provides endpoints that allow you to use different configuration settings for individual operations without changing the global configuration.

### Custom Configuration Features

- **Per-Request Configuration**: Override API keys, URLs, and service names for individual requests
- **Fallback to Defaults**: Any parameter not provided will use the default configuration
- **Flexible Operations**: Add, delete, or retrieve configurations with custom settings
- **Security**: API keys are masked in logs for security

### Custom Configuration Examples

```bash
# Add host with different API key and application ID
curl -X POST http://localhost:8080/api/traefik/add-host-custom \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "staging.example.com",
    "dokployApiKey": "staging-api-key",
    "apiDomain": "https://staging.imvj.in",
    "applicationId": "staging-app-id"
  }'

# Use different service names for a specific host
curl -X POST http://localhost:8080/api/traefik/add-host-custom \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "special.example.com",
    "backendService": "special-backend-service",
    "frontendService": "special-frontend-service"
  }'

# Get configuration from different environment
curl -X POST http://localhost:8080/api/traefik/config-custom \
  -H "Content-Type: application/json" \
  -d '{
    "dokployApiKey": "production-api-key",
    "apiDomain": "https://prod.imvj.in",
    "applicationId": "prod-app-id"
  }'
```#   T r a e f i k C o n f i g M a s t e r  
 "# TraefikConfigMaster" 
