package com.turningpoint.chapterorganizer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DataSource dataSource;

    @PostMapping("/fix-schema")
    public Map<String, Object> fixSchema() {
        Map<String, Object> response = new HashMap<>();
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            
            // First, try to drop the constraint if it exists
            try {
                statement.execute("ALTER TABLE members ALTER COLUMN chapter_id DROP NOT NULL");
                response.put("success", true);
                response.put("message", "Successfully removed NOT NULL constraint from chapter_id column");
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Failed to remove constraint: " + e.getMessage());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            if (e.getMessage().contains("createStatement")) {
                response.put("message", "Database error: Statement creation failed");
            } else {
                response.put("message", "Database error: " + e.getMessage());
            }
        } finally {
            // Clean up resources in reverse order
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    // Log but don't override main response
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    // Log but don't override main response
                }
            }
        }
        
        return response;
    }
}