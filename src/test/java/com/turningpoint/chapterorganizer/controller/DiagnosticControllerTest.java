package com.turningpoint.chapterorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for DiagnosticController
 * Tests configuration and database diagnostic endpoints using standalone MockMvc setup
 */
@ExtendWith(MockitoExtension.class)
class DiagnosticControllerTest {

    private MockMvc mockMvc;

    @Mock
    private Environment environment;

    @Mock
    private DataSource dataSource;

    @InjectMocks
    private DiagnosticController diagnosticController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(diagnosticController).build();
    }

    // =========================================
    // GET /api/diagnostics/config endpoint tests
    // =========================================

    @Test
    void getConfiguration_ShouldReturnConfigInfo_WhenValidRequest() throws Exception {
        // Mock environment properties
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{"default"});
        when(environment.getProperty("spring.jpa.hibernate.ddl-auto")).thenReturn("create-drop");
        when(environment.getProperty("spring.datasource.url")).thenReturn("jdbc:h2:mem:testdb");
        when(environment.getProperty("spring.flyway.enabled")).thenReturn("false");

        // Mock database connection and metadata
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("H2");
        when(metaData.getDatabaseProductVersion()).thenReturn("1.4.200");
        when(metaData.getURL()).thenReturn("jdbc:h2:mem:testdb");

        mockMvc.perform(get("/api/diagnostics/config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.activeProfiles").isArray())
                .andExpect(jsonPath("$.activeProfiles[0]").value("test"))
                .andExpect(jsonPath("$.defaultProfiles").isArray())
                .andExpect(jsonPath("$.defaultProfiles[0]").value("default"))
                .andExpect(jsonPath("$.ddlAuto").value("create-drop"))
                .andExpect(jsonPath("$.datasourceUrl").value("jdbc:h2:mem:testdb"))
                .andExpect(jsonPath("$.flywayEnabled").value("false"))
                .andExpect(jsonPath("$.databaseProductName").value("H2"))
                .andExpect(jsonPath("$.databaseProductVersion").value("1.4.200"))
                .andExpect(jsonPath("$.databaseUrl").value("jdbc:h2:mem:testdb"));

        verify(connection).close();
    }

    @Test
    void getConfiguration_ShouldHandleDatabaseError_WhenConnectionFails() throws Exception {
        // Mock environment properties
        when(environment.getActiveProfiles()).thenReturn(new String[]{"test"});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{"default"});
        when(environment.getProperty(anyString())).thenReturn("test-value");

        // Mock database connection failure
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/diagnostics/config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.activeProfiles").isArray())
                .andExpect(jsonPath("$.databaseError").value("Database connection failed"));
    }

    @Test
    void getConfiguration_ShouldHandleEnvironmentVariables() throws Exception {
        // Mock environment properties and system environment
        when(environment.getActiveProfiles()).thenReturn(new String[]{"prod"});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{"default"});
        when(environment.getProperty(anyString())).thenReturn(null);

        // Mock database connection
        Connection connection = mock(Connection.class);
        DatabaseMetaData metaData = mock(DatabaseMetaData.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("PostgreSQL");
        when(metaData.getDatabaseProductVersion()).thenReturn("13.0");
        when(metaData.getURL()).thenReturn("jdbc:postgresql://localhost/testdb");

        mockMvc.perform(get("/api/diagnostics/config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.activeProfiles[0]").value("prod"))
                .andExpect(jsonPath("$.databaseProductName").value("PostgreSQL"));

        verify(connection).close();
    }

    // =========================================
    // GET /api/diagnostics/tables endpoint tests
    // =========================================

    @Test
    void getTableInfo_ShouldReturnTableCounts_WhenValidRequest() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong(1)).thenReturn(5L, 10L, 3L, 8L, 25L, 15L);

        mockMvc.perform(get("/api/diagnostics/tables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chapters_count").value(5))
                .andExpect(jsonPath("$.institutions_count").value(10))
                .andExpect(jsonPath("$.universities_count").value(3))
                .andExpect(jsonPath("$.churches_count").value(8))
                .andExpect(jsonPath("$.members_count").value(25))
                .andExpect(jsonPath("$.events_count").value(15));

        verify(connection, times(6)).prepareStatement(anyString());
        verify(stmt, times(6)).executeQuery();
        verify(rs, times(6)).close();
        verify(stmt, times(6)).close();
        verify(connection).close();
    }

    @Test
    void getTableInfo_ShouldHandleTableQueryError_WhenTableNotFound() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(argThat(sql -> sql != null && sql.contains("chapters")))).thenThrow(new RuntimeException("Table not found"));
        when(connection.prepareStatement(argThat(sql -> sql != null && !sql.contains("chapters")))).thenReturn(stmt);
        
        ResultSet rs = mock(ResultSet.class);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getLong(1)).thenReturn(10L);

        mockMvc.perform(get("/api/diagnostics/tables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.chapters_error").value("Table not found"))
                .andExpect(jsonPath("$.institutions_count").value(10));

        verify(connection).close();
    }

    @Test
    void getTableInfo_ShouldHandleConnectionError_WhenDatabaseUnavailable() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Database unavailable"));

        mockMvc.perform(get("/api/diagnostics/tables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.connection_error").value("Database unavailable"));
    }

    // =========================================
    // HTTP method validation tests
    // =========================================

    @Test
    void diagnosticEndpoints_ShouldHandleUnsupportedHttpMethods() throws Exception {
        // POST should not be allowed
        mockMvc.perform(post("/api/diagnostics/config"))
                .andExpect(status().isMethodNotAllowed());

        mockMvc.perform(post("/api/diagnostics/tables"))
                .andExpect(status().isMethodNotAllowed());

        // PUT should not be allowed
        mockMvc.perform(put("/api/diagnostics/config"))
                .andExpect(status().isMethodNotAllowed());

        // DELETE should not be allowed
        mockMvc.perform(delete("/api/diagnostics/tables"))
                .andExpect(status().isMethodNotAllowed());
    }

    // =========================================
    // Response validation tests
    // =========================================

    @Test
    void diagnosticEndpoints_ShouldReturnConsistentJsonStructure() throws Exception {
        // Setup basic mocks
        when(environment.getActiveProfiles()).thenReturn(new String[]{});
        when(environment.getDefaultProfiles()).thenReturn(new String[]{});
        
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getMetaData()).thenThrow(new RuntimeException("Test error"));

        // Test config endpoint returns JSON
        mockMvc.perform(get("/api/diagnostics/config"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap());

        // Test tables endpoint returns JSON
        mockMvc.perform(get("/api/diagnostics/tables"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isMap());
    }
}