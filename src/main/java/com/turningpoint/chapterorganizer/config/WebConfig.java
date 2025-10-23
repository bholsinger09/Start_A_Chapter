package com.turningpoint.chapterorganizer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for static resources and Single Page Application (SPA) routing.
 * This configuration handles both static file serving and ensures that all non-API routes 
 * are forwarded to index.html so that Vue.js can handle client-side routing.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure static resource handling.
     * Serves static files from the classpath:/static/ directory.
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0);
    }

    /**
     * Note: SPA routing is handled by RootController for root path and specific controllers for API paths.
     * No view controller configuration needed since we're using proper controller methods.
     */
}