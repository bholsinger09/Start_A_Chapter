package com.turningpoint.chapterorganizer.security.context;

import org.springframework.stereotype.Component;

/**
 * Thread-local security context holder for managing current user security information.
 */
@Component
public class SecurityContextHolder {
    
    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();
    
    /**
     * Set the security context for the current thread
     */
    public static void setContext(SecurityContext context) {
        contextHolder.set(context);
    }
    
    /**
     * Get the security context for the current thread
     */
    public static SecurityContext getContext() {
        return contextHolder.get();
    }
    
    /**
     * Clear the security context for the current thread
     */
    public static void clearContext() {
        contextHolder.remove();
    }
    
    /**
     * Check if there is a security context set
     */
    public static boolean hasContext() {
        return contextHolder.get() != null;
    }
    
    /**
     * Get the current user from the security context
     */
    public static com.turningpoint.chapterorganizer.entity.Member getCurrentUser() {
        SecurityContext context = getContext();
        return context != null ? context.getCurrentUser() : null;
    }
    
    /**
     * Get the current user ID from the security context
     */
    public static Long getCurrentUserId() {
        com.turningpoint.chapterorganizer.entity.Member user = getCurrentUser();
        return user != null ? user.getId() : null;
    }
}