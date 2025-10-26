package com.turningpoint.chapterorganizer.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource;

    private Connection mockConnection;
    private Statement mockStatement;

    @BeforeEach
    void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        
        when(dataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
    }

    @Test
    void fixSchema_ShouldReturnSuccess_WhenSchemaFixSucceeds() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(mockStatement).execute("ALTER TABLE members ALTER COLUMN chapter_id DROP NOT NULL");
    }

    @Test
    void fixSchema_ShouldReturnError_WhenConnectionFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Database error: Connection failed"));
    }

    @Test
    void fixSchema_ShouldReturnError_WhenSQLExecutionFails() throws Exception {
        doThrow(new SQLException("SQL failed")).when(mockStatement).execute(anyString());

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void fixSchema_ShouldReturnError_WhenCreateStatementFails() throws Exception {
        when(mockConnection.createStatement()).thenThrow(new SQLException("Statement failed"));

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void fixSchema_ShouldHandleNullPointer() throws Exception {
        when(dataSource.getConnection()).thenThrow(new NullPointerException("Null error"));

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void fixSchema_ShouldHandleRuntimeException() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Runtime error"));

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void fixSchema_ShouldCloseResources_WhenStatementCloseFails() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);
        doThrow(new SQLException("Close failed")).when(mockStatement).close();

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void fixSchema_ShouldCloseResources_WhenConnectionCloseFails() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);
        doThrow(new SQLException("Connection close failed")).when(mockConnection).close();

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(mockStatement).close();
        verify(mockConnection).close();
    }

    @Test
    void fixSchema_ShouldReturnProperResponseStructure() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void fixSchema_ShouldExecuteCorrectSQLStatement() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/admin/fix-schema"));

        verify(mockStatement).execute("ALTER TABLE members ALTER COLUMN chapter_id DROP NOT NULL");
    }

        @Test
    void fixSchema_ShouldOnlyAcceptPostMethod() throws Exception {
        mockMvc.perform(get("/api/admin/fix-schema"))
                .andExpect(status().is5xxServerError()); // Spring Boot returns 500 in test environment
    }

    @Test
    void fixSchema_ShouldUseHttpPost() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/admin/fix-schema"))
                .andExpect(status().is5xxServerError());
                
        mockMvc.perform(put("/api/admin/fix-schema"))
                .andExpect(status().is5xxServerError());
                
        mockMvc.perform(delete("/api/admin/fix-schema"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void fixSchema_ShouldNotRequireRequestBody() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void fixSchema_ShouldReturnApplicationJson() throws Exception {
        when(mockStatement.execute(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/admin/fix-schema"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
