# No Middlewares Configuration Feature

This feature allows the Traefik Config Manager to handle configurations that don't have a middlewares section, which is common in simpler Traefik setups.

## Key Features

### 1. **Smart Section Detection**
- The merge logic now automatically detects available sections in the configuration
- No longer requires a middlewares section to exist
- Gracefully handles configurations with different section structures

### 2. **Skip Middlewares Option**
- New `skipMiddlewares` parameter in API requests
- New `skip-middlewares` configuration property
- When enabled, generates routers without middleware references

### 3. **Automatic Services Section Creation**
- If a services section doesn't exist, it will be created automatically
- Ensures single service configurations work even in minimal setups

## Configuration Options

### In application.yml
```yaml
traefik:
  config:
    skip-middlewares: true
    single-service: "your-service-name"
    single-service-port: 8080
```

### In API Requests
```json
{
  "hostname": "example.com",
  "serviceName": "your-service-name",
  "servicePort": 8080,
  "skipMiddlewares": true
}
```

## Generated Output Comparison

### With Middlewares (Traditional)
```yaml
http:
  routers:
    example-router:
      rule: Host(`example.com`)
      service: example-service
      middlewares:
        - redirect-to-https
      entryPoints:
        - web
```

### Without Middlewares (New Feature)
```yaml
http:
  routers:
    example-router:
      rule: Host(`example.com`)
      service: example-service
      entryPoints:
        - web
```

## Technical Implementation

### 1. **Enhanced Merge Logic**
- `findNextSectionAfterRouters()` - Dynamically finds the next section
- `createServicesSection()` - Creates services section if missing
- Handles configurations with or without middlewares section

### 2. **Flexible Router Generation**
- Conditional middleware inclusion based on `skipMiddlewares` flag
- Supports both dual service and single service modes
- Maintains backward compatibility

### 3. **Error Handling**
- Graceful fallback when expected sections are missing
- Clear logging for debugging configuration issues
- Maintains existing functionality for traditional setups

## Use Cases

1. **Simple Docker Compose setups** without complex middleware chains
2. **Development environments** with minimal Traefik configuration
3. **Legacy configurations** that don't use middlewares
4. **Microservice deployments** with service-level routing only

This feature ensures the Traefik Config Manager works with a wider variety of Traefik configurations while maintaining full backward compatibility.