package com.turningpoint.chapterorganizer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for RootController.
 * Tests root endpoints, API info endpoint, HTML response formatting, and CORS support.
 */
@WebMvcTest(RootController.class)
@AutoConfigureMockMvc(addFilters = false)
class RootControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void index_ShouldReturnRedirectHtml() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<!DOCTYPE html>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Campus Chapter Organizer</title>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<h1>Loading...</h1>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("window.location.href='/index.html'")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("</html>")));
    }

    @Test
    void index_ShouldSupportGetMethod() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Campus Chapter Organizer")));
    }

    @Test
    void index_ShouldHandleUnsupportedHttpMethods() throws Exception {
        // Test POST method - should return method not allowed or internal server error (due to global exception handler)
        mockMvc.perform(post("/"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test PUT method
        mockMvc.perform(put("/"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test DELETE method
        mockMvc.perform(delete("/"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler
    }

    @Test
    void apiInfo_ShouldReturnServiceInformation() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.service").value("Campus Chapter Organizer API"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.status").value("running"))
                .andExpect(jsonPath("$.endpoints").isMap())
                .andExpect(jsonPath("$.endpoints.chapters").value("/api/chapters"))
                .andExpect(jsonPath("$.endpoints.members").value("/api/members"))
                .andExpect(jsonPath("$.endpoints.events").value("/api/events"))
                .andExpect(jsonPath("$.endpoints.health").value("/actuator/health"));
    }

    @Test
    void apiInfo_ShouldSupportGetMethod() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.service").exists())
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.endpoints").exists());
    }

    @Test
    void apiInfo_ShouldHandleUnsupportedHttpMethods() throws Exception {
        // Test POST method - should return method not allowed or internal server error (due to global exception handler)
        mockMvc.perform(post("/api/info"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test PUT method
        mockMvc.perform(put("/api/info"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test DELETE method
        mockMvc.perform(delete("/api/info"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler
    }

    @Test
    void apiInfo_ShouldIncludeAllRequiredFields() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.service").isString())
                .andExpect(jsonPath("$.service").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(jsonPath("$.version").isString())
                .andExpect(jsonPath("$.version").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.status").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(jsonPath("$.endpoints").isMap());
    }

    @Test
    void apiInfo_ShouldHaveCorrectEndpointMappings() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.endpoints.chapters").value(org.hamcrest.Matchers.startsWith("/api/")))
                .andExpect(jsonPath("$.endpoints.members").value(org.hamcrest.Matchers.startsWith("/api/")))
                .andExpect(jsonPath("$.endpoints.events").value(org.hamcrest.Matchers.startsWith("/api/")))
                .andExpect(jsonPath("$.endpoints.health").value(org.hamcrest.Matchers.startsWith("/actuator/")));
    }

    @Test
    void rootController_ShouldSupportCrossOrigin() throws Exception {
        // Test CORS support for root endpoint
        mockMvc.perform(options("/")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));

        // Test CORS support for API info endpoint
        mockMvc.perform(options("/api/info")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    @Test
    void rootController_ShouldNotRequireAuthentication() throws Exception {
        // Both endpoints should work without authentication
        mockMvc.perform(get("/")
                // Explicitly no authentication headers
                )
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Campus Chapter Organizer")));

        mockMvc.perform(get("/api/info")
                // Explicitly no authentication headers
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.service").value("Campus Chapter Organizer API"));
    }

    @Test
    void apiInfo_EndpointsShouldBeValidPaths() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.endpoints.chapters").value(org.hamcrest.Matchers.matchesRegex("^/api/[a-zA-Z0-9]+$")))
                .andExpect(jsonPath("$.endpoints.members").value(org.hamcrest.Matchers.matchesRegex("^/api/[a-zA-Z0-9]+$")))
                .andExpect(jsonPath("$.endpoints.events").value(org.hamcrest.Matchers.matchesRegex("^/api/[a-zA-Z0-9]+$")))
                .andExpect(jsonPath("$.endpoints.health").value(org.hamcrest.Matchers.matchesRegex("^/[a-zA-Z0-9/]+$")));
    }

    @Test
    void index_ShouldContainValidJavaScript() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<script>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("window.location.href")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("'/index.html'")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("</script>")));
    }
}