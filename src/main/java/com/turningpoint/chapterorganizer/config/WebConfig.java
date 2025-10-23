package com.turningpoint.chapterorganizer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
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
     * Configure view controllers to handle SPA routing.
     * Any request that doesn't match static resources or API endpoints
     * will be forwarded to index.html for Vue.js to handle.
     */
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Forward SPA routes to index.html for Vue.js client-side routing
        // These patterns catch common SPA routes while excluding static resources
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/{path:[^.]*}").setViewName("forward:/index.html");
    }
}