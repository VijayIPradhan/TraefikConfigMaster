package com.traefikconfig.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    // Pre-compiled patterns for better performance
    private static final Pattern TRAEFIK_DOMAIN_PATTERN = Pattern.compile("https?://.*\\.traefik\\.me");
    private static final Pattern SEABED2CREST_DOMAIN_PATTERN = Pattern.compile("https?://.*\\.seabed2crest\\.com");
    private static final Pattern LOCALHOST_PATTERN = Pattern.compile("https?://localhost:\\d+");
    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("https?://127\\.0\\.0\\.1:\\d+");
    private static final Pattern GENERIC_HTTPS_PATTERN = Pattern.compile("https?://.*");

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
        
        // Set comprehensive CORS headers
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Expose-Headers", 
            "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, " +
            "Access-Control-Allow-Methods, Access-Control-Allow-Headers, " +
            "Access-Control-Max-Age, Access-Control-Expose-Headers, " +
            "X-Requested-With, X-Total-Count");
        
        // Handle credentials properly
        response.setHeader("Access-Control-Allow-Credentials", "false");

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }

    /**
     * Check if the origin matches our allowed patterns
     * Uses pre-compiled patterns for better performance
     */
    private boolean isAllowedOrigin(String origin) {
        return TRAEFIK_DOMAIN_PATTERN.matcher(origin).matches() ||
               SEABED2CREST_DOMAIN_PATTERN.matcher(origin).matches() ||
               LOCALHOST_PATTERN.matcher(origin).matches() ||
               LOCAL_IP_PATTERN.matcher(origin).matches() ||
               GENERIC_HTTPS_PATTERN.matcher(origin).matches();
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
