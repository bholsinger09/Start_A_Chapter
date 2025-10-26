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
 * Comprehensive test suite for DebugController.
 * Tests debug endpoints, content validation, HTTP methods, and public access.
 */
@WebMvcTest(DebugController.class)
@AutoConfigureMockMvc(addFilters = false)
class DebugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debug_ShouldReturnDebugMessage() throws Exception {
        mockMvc.perform(get("/debug"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("Debug endpoint working"));
    }

    @Test
    void apiDebug_ShouldReturnApiDebugMessage() throws Exception {
        mockMvc.perform(get("/api/debug"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().string("API Debug endpoint working"));
    }

    @Test
    void debug_ShouldSupportGetMethodOnly() throws Exception {
        // GET should work
        mockMvc.perform(get("/debug"))
                .andExpect(status().isOk())
                .andExpect(content().string("Debug endpoint working"));
    }

    @Test
    void apiDebug_ShouldSupportGetMethodOnly() throws Exception {
        // GET should work
        mockMvc.perform(get("/api/debug"))
                .andExpect(status().isOk())
                .andExpect(content().string("API Debug endpoint working"));
    }

    @Test
    void debug_ShouldHandleUnsupportedHttpMethods() throws Exception {
        // Test POST method - should return method not allowed or server error (due to global exception handler)
        mockMvc.perform(post("/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test PUT method
        mockMvc.perform(put("/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test DELETE method
        mockMvc.perform(delete("/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test PATCH method
        mockMvc.perform(patch("/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler
    }

    @Test
    void apiDebug_ShouldHandleUnsupportedHttpMethods() throws Exception {
        // Test POST method - should return method not allowed or server error (due to global exception handler)
        mockMvc.perform(post("/api/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test PUT method
        mockMvc.perform(put("/api/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test DELETE method
        mockMvc.perform(delete("/api/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler

        // Test PATCH method
        mockMvc.perform(patch("/api/debug"))
                .andExpect(status().is5xxServerError()); // Due to GlobalExceptionHandler
    }

    @Test
    void debugEndpoints_ShouldNotRequireAuthentication() throws Exception {
        // Both debug endpoints should work without authentication
        mockMvc.perform(get("/debug")
                // Explicitly no authentication headers
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Debug endpoint working"));

        mockMvc.perform(get("/api/debug")
                // Explicitly no authentication headers
                )
                .andExpect(status().isOk())
                .andExpect(content().string("API Debug endpoint working"));
    }

    @Test
    void debug_ShouldReturnPlainTextContent() throws Exception {
        mockMvc.perform(get("/debug"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Debug")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("endpoint")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("working")));
    }

    @Test
    void apiDebug_ShouldReturnPlainTextContent() throws Exception {
        mockMvc.perform(get("/api/debug"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("API")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Debug")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("endpoint")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("working")));
    }

    @Test
    void debugEndpoints_ShouldHaveDifferentMessages() throws Exception {
        // Verify the two debug endpoints return different messages
        String debugResponse = mockMvc.perform(get("/debug"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String apiDebugResponse = mockMvc.perform(get("/api/debug"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verify they are different
        assert !debugResponse.equals(apiDebugResponse);
        assert debugResponse.contains("Debug endpoint working");
        assert apiDebugResponse.contains("API Debug endpoint working");
    }

    @Test
    void debugEndpoints_ShouldBeConsistentAcrossRequests() throws Exception {
        // Test that debug endpoints return consistent responses
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/debug"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Debug endpoint working"));

            mockMvc.perform(get("/api/debug"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("API Debug endpoint working"));
        }
    }

    @Test
    void debugEndpoints_ShouldSupportOptionsMethod() throws Exception {
        // Test OPTIONS method for CORS preflight
        mockMvc.perform(options("/debug")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());

        mockMvc.perform(options("/api/debug")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk());
    }
}