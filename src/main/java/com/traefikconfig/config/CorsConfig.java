package com.traefikconfig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Enhanced CORS configuration with pattern-based origins
     * Compatible with Spring Boot 3.3.x and modern browsers
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
                            // Local development
                            "http://localhost:*",
                            "https://localhost:*",
                            "http://127.0.0.1:*",
                            "https://127.0.0.1:*",
                            
                            // Traefik domains (your specific use case)
                            "http://*.traefik.me",
                            "https://*.traefik.me",
                            "http://*.seabed2crest.com",
                            "https://*.seabed2crest.com",
                            
                            // Generic patterns for flexibility
                            "http://*",
                            "https://*"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowedHeaders("*")
                        .exposedHeaders(
                            "Access-Control-Allow-Origin",
                            "Access-Control-Allow-Credentials", 
                            "Access-Control-Allow-Methods",
                            "Access-Control-Allow-Headers",
                            "Access-Control-Max-Age",
                            "Access-Control-Expose-Headers",
                            "X-Requested-With",
                            "X-Total-Count"
                        )
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    /**
     * Filter-level CORS configuration for comprehensive coverage
     * This ensures CORS headers are set even for non-controller requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Define allowed origin patterns
        List<String> allowedOriginPatterns = Arrays.asList(
            // Local development
            "http://localhost:*",
            "https://localhost:*",
            "http://127.0.0.1:*",
            "https://127.0.0.1:*",
            
            // Traefik domains
            "http://*.traefik.me",
            "https://*.traefik.me",
            "http://*.seabed2crest.com",
            "https://*.seabed2crest.com",
            
            // Generic patterns
            "http://*",
            "https://*"
        );
        
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Expose specific CORS headers
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials", 
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Headers",
            "Access-Control-Max-Age",
            "Access-Control-Expose-Headers",
            "X-Requested-With",
            "X-Total-Count"
        ));
        
        // Set preflight cache duration
        configuration.setMaxAge(3600L);
        
        // Don't allow credentials with wildcard origins (security best practice)
        configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}