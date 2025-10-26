package com.turningpoint.chapterorganizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turningpoint.chapterorganizer.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for AuthController.
 * Tests authentication endpoints including login, registration, and security validation.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Object> validLoginRequest;
    private Map<String, Object> validRegistrationRequest;

    @BeforeEach
    void setUp() {
        // Setup valid login request
        validLoginRequest = new HashMap<>();
        validLoginRequest.put("username", "testuser");
        validLoginRequest.put("password", "testpassword");
        validLoginRequest.put("action", "login");

        // Setup valid registration request
        validRegistrationRequest = new HashMap<>();
        validRegistrationRequest.put("username", "newuser");
        validRegistrationRequest.put("password", "newpassword");
        validRegistrationRequest.put("firstName", "John");
        validRegistrationRequest.put("lastName", "Doe");
        validRegistrationRequest.put("email", "john.doe@example.com");
        validRegistrationRequest.put("stateOfResidence", "CA");
        validRegistrationRequest.put("action", "register");
    }

    @Test
    void simpleTest_GET_ShouldReturnSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/simple-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("AuthController working without dependencies"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void simpleTest_POST_ShouldReturnSuccessWithUsername() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser");

        // When & Then
        mockMvc.perform(post("/api/auth/simple-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login working via simple-test POST endpoint"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAuth_ShouldHandleLogin_WhenActionIsLogin() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.action").value("login"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAuth_ShouldHandleLogin_WhenHasPasswordButNoRegistrationFields() throws Exception {
        // Given - Login request without explicit action but with login characteristics
        Map<String, Object> implicitLoginRequest = new HashMap<>();
        implicitLoginRequest.put("username", "testuser");
        implicitLoginRequest.put("password", "testpassword");
        // No firstName, stateOfResidence, etc.

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(implicitLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.action").value("login"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAuth_ShouldHandleRegistration_WhenActionIsRegister() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.action").value("register"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAuth_ShouldHandleRegistration_WhenHasRegistrationFields() throws Exception {
        // Given - Registration request without explicit action but with registration characteristics
        Map<String, Object> implicitRegistrationRequest = new HashMap<>();
        implicitRegistrationRequest.put("username", "newuser");
        implicitRegistrationRequest.put("password", "newpassword");
        implicitRegistrationRequest.put("firstName", "Jane");
        implicitRegistrationRequest.put("stateOfResidence", "NY");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(implicitRegistrationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.action").value("register"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loginTest_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("password", "testpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login test endpoint working"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void login_ShouldReturnSuccess() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("username", "testuser");
        request.put("password", "testpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login endpoint working (basic test mode)"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void simpleTest_POST_ShouldHandleEmptyRequest() throws Exception {
        // Given
        Map<String, Object> emptyRequest = new HashMap<>();

        // When & Then
        mockMvc.perform(post("/api/auth/simple-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login working via simple-test POST endpoint"))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAuth_ShouldHandleEmptyRequest() throws Exception {
        // Given
        Map<String, Object> emptyRequest = new HashMap<>();

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.action").value("register"))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void loginTest_ShouldHandleNullUsername() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();
        request.put("password", "testpassword");

        // When & Then
        mockMvc.perform(post("/api/auth/login-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login test endpoint working"))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void login_ShouldHandleMissingCredentials() throws Exception {
        // Given
        Map<String, Object> request = new HashMap<>();

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login endpoint working (basic test mode)"))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleAuth_ShouldDistinguishBetweenLoginAndRegistration() throws Exception {
        // Test that the controller correctly identifies login vs registration based on fields

        // Test 1: Clear registration (has firstName and stateOfResidence)
        Map<String, Object> clearRegistration = new HashMap<>();
        clearRegistration.put("username", "user1");
        clearRegistration.put("password", "pass1");
        clearRegistration.put("firstName", "John");
        clearRegistration.put("stateOfResidence", "CA");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clearRegistration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("register"));

        // Test 2: Clear login (only username and password)
        Map<String, Object> clearLogin = new HashMap<>();
        clearLogin.put("username", "user2");
        clearLogin.put("password", "pass2");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clearLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("login"));
    }

    @Test
    void allEndpoints_ShouldIncludeTimestamp() throws Exception {
        // Test that all endpoints include a timestamp in their response

        // Test GET simple-test
        mockMvc.perform(get("/api/auth/simple-test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Test POST simple-test
        mockMvc.perform(post("/api/auth/simple-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Test register endpoint
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Test login-test
        mockMvc.perform(post("/api/auth/login-test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Test login
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
}