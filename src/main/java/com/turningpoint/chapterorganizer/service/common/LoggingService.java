package com.turningpoint.chapterorganizer.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * Logging Service  
 * Fixes: Duplicated Code smell - Centralized logging functionality
 * Single Responsibility: Handle all application logging with consistent format
 */
@Service
public class LoggingService {

    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_ID_KEY = "userId";
    private static final String OPERATION_KEY = "operation";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log successful operations.
     * Fixes: Inconsistent success logging across services
     */
    public void logSuccess(Class<?> clazz, String operation, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        logger.info("SUCCESS - {}: {}", operation, message);
        clearContext();
    }

    /**
     * Log successful operations with data.
     */
    public void logSuccess(Class<?> clazz, String operation, String message, Object data) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        logger.info("SUCCESS - {}: {} | Data: {}", operation, message, data);
        clearContext();
    }

    /**
     * Log errors with exception details.
     * Fixes: Inconsistent error logging across services
     */
    public void logError(Class<?> clazz, String operation, String message, Exception ex) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        logger.error("ERROR - {}: {} | Exception: {}", operation, message, ex.getMessage(), ex);
        clearContext();
    }

    /**
     * Log warnings.
     */
    public void logWarning(Class<?> clazz, String operation, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        logger.warn("WARNING - {}: {}", operation, message);
        clearContext();
    }

    /**
     * Log debug information.
     */
    public void logDebug(Class<?> clazz, String operation, String message) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        logger.debug("DEBUG - {}: {}", operation, message);
        clearContext();
    }

    /**
     * Log API request start.
     * Fixes: Duplicated request logging across controllers
     */
    public String logRequestStart(Class<?> clazz, String endpoint, String method) {
        String requestId = UUID.randomUUID().toString();
        Logger logger = LoggerFactory.getLogger(clazz);
        
        setRequestContext(requestId);
        logger.info("REQUEST START - {} {} | RequestId: {} | Timestamp: {}", 
                method, endpoint, requestId, getCurrentTimestamp());
        
        return requestId;
    }

    /**
     * Log API request start with parameters.
     */
    public String logRequestStart(Class<?> clazz, String endpoint, String method, Map<String, Object> params) {
        String requestId = UUID.randomUUID().toString();
        Logger logger = LoggerFactory.getLogger(clazz);
        
        setRequestContext(requestId);
        logger.info("REQUEST START - {} {} | RequestId: {} | Params: {} | Timestamp: {}", 
                method, endpoint, requestId, params, getCurrentTimestamp());
        
        return requestId;
    }

    /**
     * Log API request completion.
     */
    public void logRequestEnd(Class<?> clazz, String requestId, String endpoint, long durationMs) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setRequestContext(requestId);
        logger.info("REQUEST END - {} | RequestId: {} | Duration: {}ms | Timestamp: {}", 
                endpoint, requestId, durationMs, getCurrentTimestamp());
        clearContext();
    }

    /**
     * Log business operation start.
     * Fixes: Inconsistent business logic logging
     */
    public void logBusinessOperationStart(Class<?> clazz, String operation, String entityType, Object entityId) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        logger.info("BUSINESS START - {} | Entity: {} | ID: {} | Timestamp: {}", 
                operation, entityType, entityId, getCurrentTimestamp());
    }

    /**
     * Log business operation completion.
     */
    public void logBusinessOperationEnd(Class<?> clazz, String operation, String entityType, Object entityId, boolean success) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setOperationContext(operation);
        String status = success ? "SUCCESS" : "FAILED";
        logger.info("BUSINESS END - {} | Status: {} | Entity: {} | ID: {} | Timestamp: {}", 
                operation, status, entityType, entityId, getCurrentTimestamp());
        clearContext();
    }

    /**
     * Log database operation.
     * Fixes: Duplicated database logging across repositories
     */
    public void logDatabaseOperation(Class<?> clazz, String operation, String table, Object id) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.debug("DATABASE - {} | Table: {} | ID: {}", operation, table, id);
    }

    /**
     * Log validation failure.
     * Fixes: Inconsistent validation logging
     */
    public void logValidationFailure(Class<?> clazz, String field, String value, String reason) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.warn("VALIDATION FAILED - Field: {} | Value: {} | Reason: {}", field, value, reason);
    }

    /**
     * Log security event.
     */
    public void logSecurityEvent(Class<?> clazz, String event, String userId, String details) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setUserContext(userId);
        logger.warn("SECURITY - {} | User: {} | Details: {}", event, userId, details);
        clearContext();
    }

    /**
     * Log performance metrics.
     * Fixes: Duplicated performance logging
     */
    public void logPerformance(Class<?> clazz, String operation, long durationMs) {
        Logger logger = LoggerFactory.getLogger(clazz);
        
        if (durationMs > 5000) { // Log slow operations as warnings
            logger.warn("PERFORMANCE - SLOW OPERATION: {} | Duration: {}ms", operation, durationMs);
        } else {
            logger.debug("PERFORMANCE - {}: {}ms", operation, durationMs);
        }
    }

    /**
     * Log performance metrics with threshold.
     */
    public void logPerformance(Class<?> clazz, String operation, long durationMs, long thresholdMs) {
        Logger logger = LoggerFactory.getLogger(clazz);
        
        if (durationMs > thresholdMs) {
            logger.warn("PERFORMANCE - THRESHOLD EXCEEDED: {} | Duration: {}ms | Threshold: {}ms", 
                    operation, durationMs, thresholdMs);
        } else {
            logger.debug("PERFORMANCE - {}: {}ms", operation, durationMs);
        }
    }

    /**
     * Log external API call.
     * Fixes: Inconsistent external service logging
     */
    public void logExternalApiCall(Class<?> clazz, String service, String endpoint, String method) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("EXTERNAL API - {} {} to {} | Timestamp: {}", 
                method, endpoint, service, getCurrentTimestamp());
    }

    /**
     * Log external API response.
     */
    public void logExternalApiResponse(Class<?> clazz, String service, int statusCode, long durationMs) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("EXTERNAL API RESPONSE - {} | Status: {} | Duration: {}ms", 
                service, statusCode, durationMs);
    }

    /**
     * Set request context in MDC.
     * Fixes: Inconsistent request tracking
     */
    private void setRequestContext(String requestId) {
        MDC.put(REQUEST_ID_KEY, requestId);
    }

    /**
     * Set user context in MDC.
     */
    private void setUserContext(String userId) {
        MDC.put(USER_ID_KEY, userId);
    }

    /**
     * Set operation context in MDC.
     */
    private void setOperationContext(String operation) {
        MDC.put(OPERATION_KEY, operation);
    }

    /**
     * Clear all MDC context.
     */
    private void clearContext() {
        MDC.clear();
    }

    /**
     * Get current timestamp formatted for logging.
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
    }

    /**
     * Create structured log entry for audit purposes.
     * Fixes: Inconsistent audit logging
     */
    public void logAuditEvent(Class<?> clazz, String action, String entityType, 
                             Object entityId, String userId, String details) {
        Logger logger = LoggerFactory.getLogger(clazz);
        setUserContext(userId);
        logger.info("AUDIT - Action: {} | Entity: {} | ID: {} | User: {} | Details: {} | Timestamp: {}", 
                action, entityType, entityId, userId, details, getCurrentTimestamp());
        clearContext();
    }

    /**
     * Log method entry for debugging.
     */
    public void logMethodEntry(Class<?> clazz, String methodName, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug("ENTER - {} | Args: {}", methodName, args);
        }
    }

    /**
     * Log method exit for debugging.
     */
    public void logMethodExit(Class<?> clazz, String methodName, Object result) {
        Logger logger = LoggerFactory.getLogger(clazz);
        if (logger.isDebugEnabled()) {
            logger.debug("EXIT - {} | Result: {}", methodName, result);
        }
    }

    /**
     * Log configuration values at startup.
     * Fixes: Missing configuration logging
     */
    public void logConfiguration(Class<?> clazz, Map<String, Object> config) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info("CONFIGURATION LOADED - {}", config);
    }
}