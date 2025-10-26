package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.AuditLog;
import com.turningpoint.chapterorganizer.entity.OperationalMetric;
import com.turningpoint.chapterorganizer.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for MonitoringController
 * Tests all 16 monitoring endpoints with proper mocking and validation
 */
@ExtendWith(MockitoExtension.class)
class MonitoringControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private MonitoringController monitoringController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(monitoringController).build();
    }

    // =========================================
    // System Health Tests
    // =========================================

    @Test
    void getSystemHealth_ShouldReturnHealthMetrics_WhenValidRequest() throws Exception {
        // Given
        Map<String, Object> healthMetrics = Map.of("status", "healthy", "uptime", 12345L);
        when(auditService.getSystemHealthMetrics()).thenReturn(healthMetrics);

        // When & Then
        mockMvc.perform(get("/api/monitoring/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("healthy"));
    }

    @Test
    void getDashboardStats_ShouldReturnDashboardData_WhenValidRequest() throws Exception {
        // Given
        setupDashboardMocks();

        // When & Then
        mockMvc.perform(get("/api/monitoring/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getDashboardStats_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Given
        when(auditService.getRecentActivity(any(Pageable.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/monitoring/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // =========================================
    // Audit Log Tests
    // =========================================

    @Test
    void getAuditLogs_ShouldReturnPagedLogs_WhenValidRequest() throws Exception {
        // Given
        Page<AuditLog> auditLogPage = new PageImpl<>(createMockAuditLogs(), PageRequest.of(0, 20), 2);
        when(auditService.getRecentActivity(any(Pageable.class))).thenReturn(auditLogPage);

        // When & Then
        mockMvc.perform(get("/api/monitoring/audit")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserAuditLogs_ShouldReturnUserLogs_WhenValidUserIdentifier() throws Exception {
        // Given
        List<AuditLog> userLogs = createMockAuditLogs();
        when(auditService.getUserActivity("user123")).thenReturn(userLogs);

        // When & Then
        mockMvc.perform(get("/api/monitoring/audit/user/user123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getEntityAuditLogs_ShouldReturnEntityLogs_WhenValidEntityTypeAndId() throws Exception {
        // Given
        List<AuditLog> entityLogs = createMockAuditLogs();
        when(auditService.getEntityHistory("Chapter", 1L)).thenReturn(entityLogs);

        // When & Then
        mockMvc.perform(get("/api/monitoring/audit/entity/Chapter/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getChapterAuditLogs_ShouldReturnChapterLogs_WhenValidChapterId() throws Exception {
        // Given
        List<AuditLog> chapterLogs = createMockAuditLogs();
        when(auditService.getChapterActivity(eq(1L), any(LocalDateTime.class))).thenReturn(chapterLogs);

        // When & Then
        mockMvc.perform(get("/api/monitoring/audit/chapter/1")
                        .param("days", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getFailedOperations_ShouldReturnFailedLogs_WhenValidRequest() throws Exception {
        // Given
        List<AuditLog> failedLogs = createMockAuditLogs();
        when(auditService.getFailedOperations()).thenReturn(failedLogs);

        // When & Then
        mockMvc.perform(get("/api/monitoring/audit/failures")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // =========================================
    // Metrics Tests
    // =========================================

    @Test
    void getMetricsByCategory_ShouldReturnMetrics_WhenValidCategory() throws Exception {
        // Given
        List<OperationalMetric> metrics = createMockOperationalMetrics();
        when(auditService.getMetricsByCategory("performance")).thenReturn(metrics);

        // When & Then
        mockMvc.perform(get("/api/monitoring/metrics/category/performance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getChapterMetrics_ShouldReturnChapterMetrics_WhenValidChapterId() throws Exception {
        // Given
        List<OperationalMetric> metrics = createMockOperationalMetrics();
        when(auditService.getChapterMetrics(1L)).thenReturn(metrics);

        // When & Then
        mockMvc.perform(get("/api/monitoring/metrics/chapter/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAverageMetric_ShouldReturnAverage_WhenValidMetricNameAndDates() throws Exception {
        // Given
        when(auditService.getAverageMetric(eq("response_time"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(150.5);

        // When & Then
        mockMvc.perform(get("/api/monitoring/metrics/response_time/average")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAverageMetric_ShouldReturnZero_WhenNullAverage() throws Exception {
        // Given
        when(auditService.getAverageMetric(eq("response_time"), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/monitoring/metrics/response_time/average")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("0.0"));
    }

    // =========================================
    // Security Monitoring Tests
    // =========================================

    @Test
    void getSuspiciousActivities_ShouldReturnSuspiciousData_WhenValidRequest() throws Exception {
        // Given
        List<Object[]> suspiciousData = Arrays.asList(
                new Object[]{"user1", 15L},
                new Object[]{"user2", 10L}
        );
        when(auditService.getSuspiciousActivities(24, 5L)).thenReturn(suspiciousData);

        // When & Then
        mockMvc.perform(get("/api/monitoring/security/suspicious")
                        .param("hours", "24")
                        .param("threshold", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserActivityCount_ShouldReturnCount_WhenValidUserIdentifier() throws Exception {
        // Given
        when(auditService.getUserActivityCount("user123", 24)).thenReturn(42L);

        // When & Then
        mockMvc.perform(get("/api/monitoring/security/user-activity/user123")
                        .param("hours", "24")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }

    // =========================================
    // Analytics Tests
    // =========================================

    @Test
    void getMostActiveUsers_ShouldReturnActiveUsers_WhenValidRequest() throws Exception {
        // Given
        List<Object[]> activeUsers = Arrays.asList(
                new Object[]{"user1", 25L},
                new Object[]{"user2", 20L}
        );
        when(auditService.getMostActiveUsers(any(LocalDateTime.class))).thenReturn(activeUsers);

        // When & Then
        mockMvc.perform(get("/api/monitoring/analytics/active-users")
                        .param("days", "7")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // =========================================
    // Manual Metric Recording Tests
    // =========================================

    @Test
    void recordMetric_ShouldReturnSuccess_WhenValidRequest() throws Exception {
        // Given
        String requestBody = """
            {
                "metricName": "response_time",
                "value": 150.5,
                "unit": "ms",
                "category": "performance",
                "chapterId": 1
            }
            """;

        doNothing().when(auditService).recordMetric(anyString(), anyDouble(), anyString(), anyString(), anyLong());

        // When & Then
        mockMvc.perform(post("/api/monitoring/metrics/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Metric recorded successfully"));
    }

    @Test
    void recordMetric_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        // Given
        String requestBody = """
            {
                "metricName": "response_time",
                "value": 150.5,
                "unit": "ms",
                "category": "performance",
                "chapterId": 1
            }
            """;

        doThrow(new RuntimeException("Database error"))
                .when(auditService).recordMetric(anyString(), anyDouble(), anyString(), anyString(), anyLong());

        // When & Then
        mockMvc.perform(post("/api/monitoring/metrics/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to record metric")));
    }

    // =========================================
    // System Metrics Tests
    // =========================================

    @Test
    void getMetrics_ShouldReturnSystemMetrics_WhenValidRequest() throws Exception {
        // Given
        setupSystemMetricsMocks();

        // When & Then
        mockMvc.perform(get("/api/monitoring/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jvm").exists())
                .andExpect(jsonPath("$.application").exists());
    }

    @Test
    void getMetrics_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(auditService.getFailedOperations()).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/monitoring/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getOperationalData_ShouldReturnOperationalMetrics_WhenValidRequest() throws Exception {
        // Given
        when(auditService.getFailedOperations()).thenReturn(createMockAuditLogs());

        // When & Then
        mockMvc.perform(get("/api/monitoring/operational")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.database").exists())
                .andExpect(jsonPath("$.services").exists())
                .andExpect(jsonPath("$.system").exists());
    }

    @Test
    void getOperationalData_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(auditService.getFailedOperations()).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/api/monitoring/operational")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }

    // =========================================
    // Helper methods for test data setup
    // =========================================

    private void setupDashboardMocks() {
        Page<AuditLog> auditLogPage = new PageImpl<>(createMockAuditLogs(), PageRequest.of(0, 100), 2);
        List<Object[]> activeUsers = Arrays.<Object[]>asList(new Object[]{"user1", 25L});
        List<AuditLog> failedOps = Arrays.asList();
        List<Object[]> suspicious = Arrays.<Object[]>asList(new Object[]{"user2", 10L});

        when(auditService.getRecentActivity(any(Pageable.class))).thenReturn(auditLogPage);
        when(auditService.getMostActiveUsers(any(LocalDateTime.class))).thenReturn(activeUsers);
        when(auditService.getFailedOperations()).thenReturn(failedOps);
        when(auditService.getSuspiciousActivities(24, 5L)).thenReturn(suspicious);
    }

    private void setupSystemMetricsMocks() {
        List<AuditLog> failedOps = Arrays.asList();
        List<Object[]> activeUsers = Arrays.<Object[]>asList(new Object[]{"user1", 25L});

        when(auditService.getFailedOperations()).thenReturn(failedOps);
        when(auditService.getMostActiveUsers(any(LocalDateTime.class))).thenReturn(activeUsers);
    }

    private List<AuditLog> createMockAuditLogs() {
        // Create mock audit logs - simplified version to avoid entity complexity
        return Arrays.asList();
    }

    private List<OperationalMetric> createMockOperationalMetrics() {
        // Create mock operational metrics - simplified version
        return Arrays.asList();
    }
}