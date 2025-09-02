#!/bin/bash

echo "🔍 Testing CORS Configuration for HTTP and HTTPS"
echo "================================================"

# Test HTTP CORS
echo ""
echo "🌐 Testing HTTP CORS..."
echo "Testing: GET /api/cors-test/simple via HTTP"
curl -v -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: Content-Type" \
     "http://localhost:8080/api/cors-test/simple" 2>&1 | grep -E "(Access-Control|HTTP|Origin)"

echo ""
echo "Testing: OPTIONS preflight via HTTP"
curl -v -X OPTIONS \
     -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     "http://localhost:8080/api/cors-test/preflight" 2>&1 | grep -E "(Access-Control|HTTP|Origin)"

echo ""
echo "🔒 Testing HTTPS CORS..."
echo "Testing: GET /api/cors-test/simple via HTTPS"
curl -v -H "Origin: https://localhost:3000" \
     -H "Access-Control-Request-Method: GET" \
     -H "Access-Control-Request-Headers: Content-Type" \
     "https://localhost:8080/api/cors-test/simple" 2>&1 | grep -E "(Access-Control|HTTP|Origin)" || echo "HTTPS request failed (expected if HTTPS not configured)"

echo ""
echo "Testing: OPTIONS preflight via HTTPS"
curl -v -X OPTIONS \
     -H "Origin: https://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     "https://localhost:8080/api/cors-test/preflight" 2>&1 | grep -E "(Access-Control|HTTP|Origin)" || echo "HTTPS request failed (expected if HTTPS not configured)"

echo ""
echo "📋 Testing with different origins..."
echo "Testing: Origin from external domain"
curl -v -H "Origin: https://example.com" \
     -H "Access-Control-Request-Method: GET" \
     "http://localhost:8080/api/cors-test/simple" 2>&1 | grep -E "(Access-Control|HTTP|Origin)"

echo ""
echo "✅ CORS Test Complete!"
echo ""
echo "💡 If you're still getting CORS errors with HTTPS:"
echo "   1. Make sure your application is deployed with these new CORS configurations"
echo "   2. Check if Traefik is properly forwarding CORS headers"
echo "   3. Verify that your HTTPS endpoint is accessible"
echo "   4. Check browser console for specific CORS error messages"
