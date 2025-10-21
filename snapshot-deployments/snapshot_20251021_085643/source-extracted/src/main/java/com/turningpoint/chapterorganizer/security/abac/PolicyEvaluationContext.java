package com.turningpoint.chapterorganizer.security.abac;

import java.util.Map;

/**
 * Context for ABAC policy evaluation containing all necessary attributes.
 */
public class PolicyEvaluationContext {
    
    private final Map<String, Object> userAttributes;
    private final Map<String, Object> resourceAttributes;
    private final Map<String, Object> environmentAttributes;
    private final Map<String, Object> actionAttributes;
    
    public PolicyEvaluationContext(Map<String, Object> userAttributes,
                                 Map<String, Object> resourceAttributes,
                                 Map<String, Object> environmentAttributes,
                                 Map<String, Object> actionAttributes) {
        this.userAttributes = userAttributes;
        this.resourceAttributes = resourceAttributes;
        this.environmentAttributes = environmentAttributes;
        this.actionAttributes = actionAttributes;
    }
    
    public Map<String, Object> getUserAttributes() {
        return userAttributes;
    }
    
    public Map<String, Object> getResourceAttributes() {
        return resourceAttributes;
    }
    
    public Map<String, Object> getEnvironmentAttributes() {
        return environmentAttributes;
    }
    
    public Map<String, Object> getActionAttributes() {
        return actionAttributes;
    }
    
    // Convenience methods for common attribute access
    public Object getUserAttribute(String key) {
        return userAttributes != null ? userAttributes.get(key) : null;
    }
    
    public Object getResourceAttribute(String key) {
        return resourceAttributes != null ? resourceAttributes.get(key) : null;
    }
    
    public Object getEnvironmentAttribute(String key) {
        return environmentAttributes != null ? environmentAttributes.get(key) : null;
    }
    
    public Object getActionAttribute(String key) {
        return actionAttributes != null ? actionAttributes.get(key) : null;
    }
    
    // Type-safe getters
    @SuppressWarnings("unchecked")
    public <T> T getUserAttribute(String key, Class<T> type) {
        Object value = getUserAttribute(key);
        return type.isInstance(value) ? (T) value : null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getResourceAttribute(String key, Class<T> type) {
        Object value = getResourceAttribute(key);
        return type.isInstance(value) ? (T) value : null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getEnvironmentAttribute(String key, Class<T> type) {
        Object value = getEnvironmentAttribute(key);
        return type.isInstance(value) ? (T) value : null;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getActionAttribute(String key, Class<T> type) {
        Object value = getActionAttribute(key);
        return type.isInstance(value) ? (T) value : null;
    }
    
    // Attribute existence checks
    public boolean hasUserAttribute(String key) {
        return userAttributes != null && userAttributes.containsKey(key);
    }
    
    public boolean hasResourceAttribute(String key) {
        return resourceAttributes != null && resourceAttributes.containsKey(key);
    }
    
    public boolean hasEnvironmentAttribute(String key) {
        return environmentAttributes != null && environmentAttributes.containsKey(key);
    }
    
    public boolean hasActionAttribute(String key) {
        return actionAttributes != null && actionAttributes.containsKey(key);
    }
}