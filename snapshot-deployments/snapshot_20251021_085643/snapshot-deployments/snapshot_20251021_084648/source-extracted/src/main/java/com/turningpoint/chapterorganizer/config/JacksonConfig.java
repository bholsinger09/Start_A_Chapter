package com.turningpoint.chapterorganizer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson configuration to handle Hibernate lazy loading proxies and LocalDateTime
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule to handle LocalDateTime and other time types
        mapper.registerModule(new JavaTimeModule());
        
        // Register Hibernate6 module to handle lazy loading proxies
        Hibernate6Module hibernateModule = new Hibernate6Module();
        
        // Simple configuration to handle proxy objects
        hibernateModule.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        
        mapper.registerModule(hibernateModule);
        
        return mapper;
    }
}