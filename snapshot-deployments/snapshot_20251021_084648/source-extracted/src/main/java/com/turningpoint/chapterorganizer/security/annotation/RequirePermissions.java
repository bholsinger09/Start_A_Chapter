package com.turningpoint.chapterorganizer.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require specific permissions for method access.
 * Can be used at class or method level.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermissions {
    
    /**
     * Required permissions (resource:action format)
     */
    String[] value();
    
    /**
     * Logical operation for multiple permissions
     */
    LogicalOperation operation() default LogicalOperation.AND;
    
    /**
     * Whether to check chapter-scoped permissions
     */
    boolean chapterScoped() default false;
    
    /**
     * Parameter name that contains the chapter ID (for chapter-scoped permissions)
     */
    String chapterIdParam() default "chapterId";
    
    /**
     * Custom error message when permission is denied
     */
    String message() default "Access denied: insufficient permissions";
    
    enum LogicalOperation {
        AND, // User must have ALL specified permissions
        OR   // User must have AT LEAST ONE of the specified permissions
    }
}