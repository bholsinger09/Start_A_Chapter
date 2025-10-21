package com.turningpoint.chapterorganizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private boolean initializeSampleData = false;
    private boolean qaMode = false;
    private String seedDataFile = "classpath:data/seed-data.sql";
    
    public boolean isInitializeSampleData() {
        return initializeSampleData;
    }
    
    public void setInitializeSampleData(boolean initializeSampleData) {
        this.initializeSampleData = initializeSampleData;
    }
    
    public boolean isQaMode() {
        return qaMode;
    }
    
    public void setQaMode(boolean qaMode) {
        this.qaMode = qaMode;
    }
    
    public String getSeedDataFile() {
        return seedDataFile;
    }
    
    public void setSeedDataFile(String seedDataFile) {
        this.seedDataFile = seedDataFile;
    }
}