package com.turningpoint.chapterorganizer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turningpoint.chapterorganizer.entity.AuditAction;
import com.turningpoint.chapterorganizer.entity.AuditLog;
import com.turningpoint.chapterorganizer.entity.OperationalMetric;
import com.turningpoint.chapterorganizer.repository.AuditLogRepository;
import com.turningpoint.chapterorganizer.repository.OperationalMetricRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private OperationalMetricRepository operationalMetricRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuditService auditService;

    private AuditLog testAuditLog;
    private OperationalMetric testMetric;
    private Object testEntity;

    @BeforeEach
    void setUp() {
        testAuditLog = new AuditLog();
        testAuditLog.setId(1L);
        testAuditLog.setEntityType("Chapter");
        testAuditLog.setEntityId(1L);
        testAuditLog.setAction(AuditAction.CREATE);
        testAuditLog.setUserIdentifier("testuser");
        testAuditLog.setTimestamp(LocalDateTime.now());

        testMetric = new OperationalMetric();
        testMetric.setId(1L);
        testMetric.setMetricName("test_metric");
        testMetric.setMetricValue(100.0);

        testEntity = new Object();
    }

    @Test
    void logCreate_ShouldCreateAuditLog_WhenValidInput() throws Exception {
        // Given
        String jsonValue = "{\"id\":1,\"name\":\"Test\"}";
        when(objectMapper.writeValueAsString(testEntity)).thenReturn(jsonValue);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        auditService.logCreate("Chapter", 1L, testEntity, "testuser");

        // Then
        verify(objectMapper).writeValueAsString(testEntity);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void logCreate_ShouldHandleJsonException() throws Exception {
        // Given
        when(objectMapper.writeValueAsString(testEntity)).thenThrow(new RuntimeException("JSON error"));

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> auditService.logCreate("Chapter", 1L, testEntity, "testuser"));
        
        verify(objectMapper).writeValueAsString(testEntity);
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void logUpdate_ShouldCreateUpdateAuditLog_WhenValidInput() throws Exception {
        // Given
        Object oldEntity = new Object();
        Object newEntity = new Object();
        String oldJson = "{\"id\":1,\"name\":\"Old\"}";
        String newJson = "{\"id\":1,\"name\":\"New\"}";
        
        when(objectMapper.writeValueAsString(oldEntity)).thenReturn(oldJson);
        when(objectMapper.writeValueAsString(newEntity)).thenReturn(newJson);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        auditService.logUpdate("Chapter", 1L, oldEntity, newEntity, "testuser");

        // Then
        verify(objectMapper).writeValueAsString(oldEntity);
        verify(objectMapper).writeValueAsString(newEntity);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void logDelete_ShouldCreateDeleteAuditLog() throws Exception {
        // Given
        String jsonValue = "{\"id\":1,\"name\":\"Test\"}";
        when(objectMapper.writeValueAsString(testEntity)).thenReturn(jsonValue);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        auditService.logDelete("Chapter", 1L, testEntity, "testuser");

        // Then
        verify(objectMapper).writeValueAsString(testEntity);
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void getRecentActivity_ShouldReturnPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        Page<AuditLog> auditLogPage = new PageImpl<>(auditLogs);
        
        when(auditLogRepository.findAllByOrderByTimestampDesc(pageable)).thenReturn(auditLogPage);

        // When
        Page<AuditLog> result = auditService.getRecentActivity(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(auditLogRepository).findAllByOrderByTimestampDesc(pageable);
    }

    @Test
    void getUserActivity_ShouldReturnUserActivity() {
        // Given
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        when(auditLogRepository.findByUserIdentifierOrderByTimestampDesc("testuser")).thenReturn(auditLogs);

        // When
        List<AuditLog> result = auditService.getUserActivity("testuser");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findByUserIdentifierOrderByTimestampDesc("testuser");
    }

    @Test
    void getEntityHistory_ShouldReturnEntityHistory() {
        // Given
        List<AuditLog> auditLogs = Arrays.asList(testAuditLog);
        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc("Chapter", 1L))
                .thenReturn(auditLogs);

        // When
        List<AuditLog> result = auditService.getEntityHistory("Chapter", 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(auditLogRepository).findByEntityTypeAndEntityIdOrderByTimestampDesc("Chapter", 1L);
    }

    @Test
    void recordMetric_ShouldSaveOperationalMetric() {
        // Given
        when(operationalMetricRepository.save(any(OperationalMetric.class))).thenReturn(testMetric);

        // When
        auditService.recordMetric("test_metric", 100.0, "count", "performance");

        // Then
        verify(operationalMetricRepository).save(any(OperationalMetric.class));
    }

    @Test
    void recordMetric_ShouldHandleException() {
        // Given
        when(operationalMetricRepository.save(any(OperationalMetric.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> auditService.recordMetric("test_metric", 100.0, "count", "performance"));
        
        verify(operationalMetricRepository).save(any(OperationalMetric.class));
    }

    @Test
    void recordDailyActiveUsers_ShouldRecordMetric() {
        // Given
        when(operationalMetricRepository.save(any(OperationalMetric.class))).thenReturn(testMetric);

        // When
        auditService.recordDailyActiveUsers(25L);

        // Then
        verify(operationalMetricRepository).save(any(OperationalMetric.class));
    }

    @Test
    void recordApiResponseTime_ShouldRecordMetric() {
        // Given
        when(operationalMetricRepository.save(any(OperationalMetric.class))).thenReturn(testMetric);

        // When
        auditService.recordApiResponseTime(150.5);

        // Then
        verify(operationalMetricRepository).save(any(OperationalMetric.class));
    }

    @Test
    void logRSVP_ShouldCreateRSVPAuditLog() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        auditService.logRSVP(1L, 2L, "YES", "testuser", 1L);

        // Then
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void logAttendance_ShouldCreateAttendanceAuditLog() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        auditService.logAttendance(1L, 2L, true, "testuser", 1L);

        // Then
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void logLogin_ShouldCreateLoginAuditLog() {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(testAuditLog);

        // When
        auditService.logLogin("testuser", "192.168.1.1", "Mozilla/5.0", true);

        // Then
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void getSystemHealthMetrics_ShouldReturnMetrics() {
        // When
        Map<String, Object> result = auditService.getSystemHealthMetrics();

        // Then
        assertNotNull(result);
        assertTrue(result instanceof Map);
    }
}