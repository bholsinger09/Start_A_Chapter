package com.turningpoint.chapterorganizer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * Diagnostic controller to help troubleshoot configuration issues
 */
@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticController {

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    /**
     * Get current configuration for troubleshooting
     */
    @GetMapping("/config")
    public Map<String, Object> getConfiguration() {
        Map<String, Object> config = new HashMap<>();
        
        // Profile information
        config.put("activeProfiles", environment.getActiveProfiles());
        config.put("defaultProfiles", environment.getDefaultProfiles());
        
        // Database configuration
        config.put("ddlAuto", environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        config.put("datasourceUrl", environment.getProperty("spring.datasource.url"));
        config.put("flywayEnabled", environment.getProperty("spring.flyway.enabled"));
        
        // Environment variables
        config.put("envSpringProfilesActive", System.getenv("SPRING_PROFILES_ACTIVE"));
        config.put("envDdlAuto", System.getenv("SPRING_JPA_HIBERNATE_DDL_AUTO"));
        
        try {
            // Database metadata
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            config.put("databaseProductName", metaData.getDatabaseProductName());
            config.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            config.put("databaseUrl", metaData.getURL());
            connection.close();
        } catch (Exception e) {
            config.put("databaseError", e.getMessage());
        }
        
        return config;
    }
}