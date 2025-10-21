package com.turningpoint.chapterorganizer.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to require specific roles for method access.
 * Can be used at class or method level.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRoles {
    
    /**
     * Required role names
     */
    String[] value();
    
    /**
     * Logical operation for multiple roles
     */
    LogicalOperation operation() default LogicalOperation.OR;
    
    /**
     * Whether to check chapter-scoped roles
     */
    boolean chapterScoped() default false;
    
    /**
     * Parameter name that contains the chapter ID (for chapter-scoped roles)
     */
    String chapterIdParam() default "chapterId";
    
    /**
     * Minimum hierarchy level required
     */
    int minHierarchyLevel() default 0;
    
    /**
     * Custom error message when role is not found
     */
    String message() default "Access denied: insufficient role permissions";
    
    enum LogicalOperation {
        AND, // User must have ALL specified roles
        OR   // User must have AT LEAST ONE of the specified roles
    }
}