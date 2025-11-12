package com.turningpoint.chapterorganizer.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.function.Supplier;

/**
 * Utility class for common controller operations and error handling.
 * Reduces code duplication across REST controllers.
 */
public final class ControllerUtils {

    private ControllerUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Execute a supplier function and handle common exceptions.
     * 
     * @param <T> The response type
     * @param supplier The function to execute
     * @return ResponseEntity with appropriate status codes
     */
    public static <T> ResponseEntity<T> executeWithErrorHandling(Supplier<ResponseEntity<T>> supplier) {
        try {
            return supplier.get();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a successful response with data.
     * 
     * @param <T> The response type
     * @param data The response data
     * @return ResponseEntity with OK status and data
     */
    public static <T> ResponseEntity<T> ok(T data) {
        return ResponseEntity.ok(data);
    }

    /**
     * Create a created response with data.
     * 
     * @param <T> The response type
     * @param data The response data
     * @return ResponseEntity with CREATED status and data
     */
    public static <T> ResponseEntity<T> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(data);
    }

    /**
     * Create a not found response.
     * 
     * @param <T> The response type
     * @return ResponseEntity with NOT_FOUND status
     */
    public static <T> ResponseEntity<T> notFound() {
        return ResponseEntity.notFound().build();
    }

    /**
     * Create a bad request response.
     * 
     * @param <T> The response type
     * @return ResponseEntity with BAD_REQUEST status
     */
    public static <T> ResponseEntity<T> badRequest() {
        return ResponseEntity.badRequest().build();
    }
}