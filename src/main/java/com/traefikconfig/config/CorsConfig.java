package com.traefikconfig.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // allow all paths
                        .allowedOriginPatterns(
                            "https://trcon.devcrm.seabed2crest.com",
                            "http://*",
                            "https://*"
                        ) // use patterns instead of exact origins
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // allow all methods
                        .allowedHeaders("*") // allow all headers
                        .exposedHeaders("*") // expose all headers
                        .allowCredentials(true) // needed for cookies or Authorization headers
                        .maxAge(3600); // preflight cache duration
            }
        };
    }
}
