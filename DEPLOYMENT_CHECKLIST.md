# CORS Fix Deployment Checklist

## ✅ Current Status
- [x] CORS configuration files updated and formatted
- [x] Application built successfully with enhanced CORS support
- [x] JAR file ready: `target/traefik-config-manager-1.0.0.jar`

## 🚀 Deployment Steps Required

### 1. **Upload New JAR to Server**
You need to upload the updated JAR file to your server:
```
target/traefik-config-manager-1.0.0.jar
```

### 2. **Stop Current Application**
On your server, stop the currently running application:
```bash
# Find the process
ps aux | grep traefik-config-manager

# Kill the process (replace PID with actual process ID)
kill <PID>
```

### 3. **Start Updated Application**
Start the new version with CORS fixes:
```bash
java -jar traefik-config-manager-1.0.0.jar
```

### 4. **Verify CORS Fix**
Test the CORS configuration:

#### Test 1: Simple CORS Test
```bash
curl -X 'GET' \
  'http://trcon.devcrm.seabed2crest.com/api/cors-test/simple' \
  -H 'accept: application/json' \
  -H 'Origin: http://localhost:3000' \
  -v
```

#### Test 2: Main API Endpoint
```bash
curl -X 'GET' \
  'http://trcon.devcrm.seabed2crest.com/api/traefik/config' \
  -H 'accept: application/json' \
  -H 'Origin: http://localhost:3000' \
  -v
```

#### Test 3: Preflight Request
```bash
curl -X 'OPTIONS' \
  'http://trcon.devcrm.seabed2crest.com/api/traefik/config' \
  -H 'Origin: http://localhost:3000' \
  -H 'Access-Control-Request-Method: GET' \
  -H 'Access-Control-Request-Headers: accept' \
  -v
```

## 🔍 Expected Results After Deployment

### Response Headers Should Include:
```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

### Swagger UI Should:
- Load without CORS errors
- Allow API calls to be executed
- Display proper responses

## 🛠️ Troubleshooting

### If CORS Still Doesn't Work:
1. **Check server logs** for any startup errors
2. **Verify the correct JAR** is running (check file timestamp)
3. **Test the CORS test endpoint** first: `/api/cors-test/simple`
4. **Clear browser cache** and try again

### Alternative Deployment Method:
If you're using Docker or a deployment platform:
1. Build a new Docker image with the updated JAR
2. Deploy the new image
3. Restart the container/service

## 📝 Notes
- The current JAR file size: ~50MB (includes all dependencies)
- Application starts on port 8080 by default
- Logs will show "✅ Traefik Config Manager Application is ready!" when started
- New CORS test endpoint available at `/api/cors-test/simple`

## ⚠️ Important
**The CORS errors you're seeing are because the server is still running the OLD version without CORS fixes. You MUST deploy the new JAR file for the fixes to take effect.**

Once deployed, your Swagger UI and all web-based API calls should work without CORS errors!