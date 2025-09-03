package com.traefikconfig.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        // Get the origin from the request
        String origin = request.getHeader("Origin");
        
        // Set CORS headers for all requests
        if (origin != null) {
            // Check if origin matches our allowed patterns
            if (isAllowedOrigin(origin)) {
                response.setHeader("Access-Control-Allow-Origin", origin);
            } else {
                // Fallback to wildcard for requests without origin header
                response.setHeader("Access-Control-Allow-Origin", "*");
            }
        } else {
            // Fallback to wildcard for requests without origin header
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Expose-Headers", 
            "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, " +
            "Access-Control-Allow-Methods, Access-Control-Allow-Headers, " +
            "Access-Control-Max-Age, Access-Control-Expose-Headers");
        
        // Handle credentials properly
        response.setHeader("Access-Control-Allow-Credentials", "false");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    private boolean isAllowedOrigin(String origin) {
        // Check if origin matches our allowed patterns
        return origin.matches("https?://.*\\.traefik\\.me") ||
               origin.matches("https?://.*\\.seabed2crest\\.com") ||
               origin.matches("https?://localhost:\\d+") ||
               origin.matches("https?://127\\.0\\.0\\.1:\\d+") ||
               origin.matches("https?://.*"); // Allow all other origins
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
