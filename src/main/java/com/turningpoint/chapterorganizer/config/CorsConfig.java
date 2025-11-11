package com.turningpoint.chapterorganizer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials
        config.setAllowCredentials(false);
        
        // Set allowed origins - be very permissive for debugging
        if ("*".equals(allowedOrigins)) {
            config.addAllowedOriginPattern("*");
        } else {
            // Add both the configured origins and wildcard patterns
            List<String> origins = Arrays.asList(allowedOrigins.split(","));
            config.setAllowedOrigins(origins);
            // Also add origin patterns for more flexibility
            config.addAllowedOriginPattern("https://startachapter.duckdns.org");
            config.addAllowedOriginPattern("https://startachapter.duckdns.org:*");
            config.addAllowedOriginPattern("http://startachapter.duckdns.org");
            config.addAllowedOriginPattern("http://startachapter.duckdns.org:*");
        }
        
        // Set allowed headers - be very permissive
        config.addAllowedHeader("*");
        config.setAllowedHeaders(Arrays.asList("*", "Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        
        // Set allowed methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        
        // Set exposed headers
        config.setExposedHeaders(Arrays.asList("*", "Authorization", "Content-Type", "X-Total-Count"));
        
        // Set max age
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
