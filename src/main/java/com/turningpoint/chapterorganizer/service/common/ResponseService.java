package com.turningpoint.chapterorganizer.service.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response Service
 * Fixes: Duplicated Code smell - Eliminates repeated response creation logic
 * Single Responsibility: Create standardized HTTP responses
 */
@Service
public class ResponseService {

    /**
     * Create success response with data.
     * Fixes: Duplicated response creation code across controllers
     */
    public <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    /**
     * Create success response with data and message.
     */
    public <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * Create success response for creation (201 status).
     */
    public <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, "Resource created successfully"));
    }

    /**
     * Create success response for creation with custom message.
     */
    public <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message));
    }

    /**
     * Create success response for deletion (204 status).
     */
    public ResponseEntity<Void> deleted() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Create error response.
     * Fixes: Duplicated error handling code
     */
    public ResponseEntity<ApiResponse<Object>> error(String message) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(message));
    }

    /**
     * Create error response with custom status.
     */
    public ResponseEntity<ApiResponse<Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message));
    }

    /**
     * Create not found response.
     */
    public ResponseEntity<ApiResponse<Object>> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    /**
     * Create validation error response.
     */
    public ResponseEntity<ApiResponse<Object>> validationError(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Create unauthorized response.
     */
    public ResponseEntity<ApiResponse<Object>> unauthorized(String message) {
        return error(HttpStatus.UNAUTHORIZED, message);
    }

    /**
     * Create forbidden response.
     */
    public ResponseEntity<ApiResponse<Object>> forbidden(String message) {
        return error(HttpStatus.FORBIDDEN, message);
    }

    /**
     * Create paginated response.
     * Fixes: Duplicated pagination response code
     */
    public <T> ResponseEntity<PaginatedResponse<T>> paginated(
            List<T> content, 
            int page, 
            int size, 
            long totalElements) {
        
        PaginatedResponse<T> response = PaginatedResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / size))
                .build();
                
        return ResponseEntity.ok(response);
    }

    /**
     * Create empty list response.
     */
    public <T> ResponseEntity<ApiResponse<List<T>>> emptyList(String message) {
        return success(Collections.emptyList(), message);
    }

    /**
     * Create bulk operation response.
     * Fixes: Duplicated bulk operation response code
     */
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkOperation(
            int successCount, 
            int failureCount, 
            List<String> errors) {
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failureCount", failureCount);
        result.put("errors", errors);
        result.put("totalProcessed", successCount + failureCount);
        
        String message = String.format("Processed %d items: %d successful, %d failed", 
                successCount + failureCount, successCount, failureCount);
        
        return success(result, message);
    }

    /**
     * Standardized API Response wrapper.
     * Fixes: Inconsistent response formats
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private long timestamp;

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(true, "Success", data);
        }

        public static <T> ApiResponse<T> success(T data, String message) {
            return new ApiResponse<>(true, message, data);
        }

        public static ApiResponse<Object> error(String message) {
            return new ApiResponse<>(false, message, null);
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * Paginated Response wrapper.
     */
    public static class PaginatedResponse<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;

        public static <T> Builder<T> builder() {
            return new Builder<>();
        }

        public static class Builder<T> {
            private List<T> content;
            private int page;
            private int size;
            private long totalElements;
            private int totalPages;

            public Builder<T> content(List<T> content) {
                this.content = content;
                return this;
            }

            public Builder<T> page(int page) {
                this.page = page;
                return this;
            }

            public Builder<T> size(int size) {
                this.size = size;
                return this;
            }

            public Builder<T> totalElements(long totalElements) {
                this.totalElements = totalElements;
                return this;
            }

            public Builder<T> totalPages(int totalPages) {
                this.totalPages = totalPages;
                return this;
            }

            public PaginatedResponse<T> build() {
                PaginatedResponse<T> response = new PaginatedResponse<>();
                response.content = this.content;
                response.page = this.page;
                response.size = this.size;
                response.totalElements = this.totalElements;
                response.totalPages = this.totalPages;
                response.first = this.page == 0;
                response.last = this.page >= this.totalPages - 1;
                return response;
            }
        }

        // Getters and setters
        public List<T> getContent() { return content; }
        public void setContent(List<T> content) { this.content = content; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
        public boolean isFirst() { return first; }
        public void setFirst(boolean first) { this.first = first; }
        public boolean isLast() { return last; }
        public void setLast(boolean last) { this.last = last; }
    }
}