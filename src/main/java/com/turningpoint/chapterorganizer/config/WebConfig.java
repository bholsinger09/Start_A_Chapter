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
     * Note: The root path "/" is handled by RootController, so we only need to handle
     * other SPA routes that don't match API endpoints or static resources.
     */
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // Handle non-root SPA routes for Vue.js client-side routing
        // Exclude root "/" since RootController handles it, and exclude API paths
        registry.addViewController("/{path:(?!api|static|js|css|img|fonts).*}").setViewName("redirect:/index.html");
    }
}