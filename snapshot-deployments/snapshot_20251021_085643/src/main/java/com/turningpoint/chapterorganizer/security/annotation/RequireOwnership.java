package com.turningpoint.chapterorganizer.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for ownership-based access control.
 * Checks if the current user owns the specified resource.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireOwnership {
    
    /**
     * Resource type (e.g., "event", "chapter", "member")
     */
    String resource();
    
    /**
     * Parameter name that contains the resource ID
     */
    String resourceIdParam() default "id";
    
    /**
     * Field name in the resource entity that contains the owner ID
     */
    String ownerField() default "ownerId";
    
    /**
     * Whether to allow chapter administrators to bypass ownership check
     */
    boolean allowChapterAdmin() default true;
    
    /**
     * Whether to allow system administrators to bypass ownership check
     */
    boolean allowSystemAdmin() default true;
    
    /**
     * Custom error message when ownership check fails
     */
    String message() default "Access denied: you do not own this resource";
}