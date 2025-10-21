package com.turningpoint.chapterorganizer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://chapter-organizer-backend.onrender.com").description("Production Server"),
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))
                .info(new Info()
                        .title("Campus Chapter Organizer API")
                        .version("1.0.0")
                        .description("A comprehensive REST API for managing college campus chapters, members, and events. " +
                                   "This API supports full CRUD operations, advanced search and filtering, pagination, " +
                                   "and provides comprehensive chapter management functionality.")
                        .contact(new Contact()
                                .name("Campus Chapter Organizer Support")
                                .email("support@chapterorganizer.com")
                                .url("https://github.com/bholsinger09/Start_A_Chapter"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                );
    }
}