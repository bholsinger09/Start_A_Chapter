package com.turningpoint.chapterorganizer.security.aspect;

import com.turningpoint.chapterorganizer.security.abac.PolicyEvaluationContext;
import com.turningpoint.chapterorganizer.security.abac.PolicyEvaluator;
import com.turningpoint.chapterorganizer.security.abac.PolicyDecision;
import com.turningpoint.chapterorganizer.security.annotation.RequirePermissions;
import com.turningpoint.chapterorganizer.security.annotation.RequireRoles;
import com.turningpoint.chapterorganizer.security.annotation.RequirePolicies;
import com.turningpoint.chapterorganizer.security.annotation.RequireOwnership;
import com.turningpoint.chapterorganizer.security.service.SecurityService;
import com.turningpoint.chapterorganizer.security.context.SecurityContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import com.turningpoint.chapterorganizer.security.exception.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Security aspect for handling RBAC and ABAC annotations.
 */
@Aspect
@Component
public class SecurityAspect {
    
    private final SecurityService securityService;
    private final PolicyEvaluator policyEvaluator;
    
    @Autowired
    public SecurityAspect(SecurityService securityService, PolicyEvaluator policyEvaluator) {
        this.securityService = securityService;
        this.policyEvaluator = policyEvaluator;
    }
    
    @Around("@annotation(requirePermissions)")
    public Object checkPermissions(ProceedingJoinPoint joinPoint, RequirePermissions requirePermissions) throws Throwable {
        if (!SecurityContextHolder.hasContext()) {
            throw new AccessDeniedException("No security context available");
        }
        
        String[] permissions = requirePermissions.value();
        boolean requireAll = requirePermissions.operation() == RequirePermissions.LogicalOperation.AND;
        boolean chapterScoped = requirePermissions.chapterScoped();
        Long chapterId = null;
        
        if (chapterScoped) {
            chapterId = extractChapterId(joinPoint, requirePermissions.chapterIdParam());
        }
        
        boolean hasAccess = securityService.validatePermission(permissions, requireAll, chapterScoped, chapterId);
        
        if (!hasAccess) {
            throw new AccessDeniedException(requirePermissions.message());
        }
        
        return joinPoint.proceed();
    }
    
    @Around("@annotation(requireRoles)")
    public Object checkRoles(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        if (!SecurityContextHolder.hasContext()) {
            throw new AccessDeniedException("No security context available");
        }
        
        String[] roles = requireRoles.value();
        boolean requireAll = requireRoles.operation() == RequireRoles.LogicalOperation.AND;
        boolean chapterScoped = requireRoles.chapterScoped();
        Long chapterId = null;
        
        if (chapterScoped) {
            chapterId = extractChapterId(joinPoint, requireRoles.chapterIdParam());
        }
        
        boolean hasAccess = securityService.validateRole(roles, requireAll, chapterScoped, chapterId, requireRoles.minHierarchyLevel());
        
        if (!hasAccess) {
            throw new AccessDeniedException(requireRoles.message());
        }
        
        return joinPoint.proceed();
    }
    
    @Around("@annotation(requirePolicies)")
    public Object checkPolicies(ProceedingJoinPoint joinPoint, RequirePolicies requirePolicies) throws Throwable {
        if (!SecurityContextHolder.hasContext()) {
            throw new AccessDeniedException("No security context available");
        }
        
        String[] policyIds = requirePolicies.value();
        PolicyEvaluator.CombiningAlgorithm algorithm = requirePolicies.algorithm();
        
        PolicyEvaluationContext context = buildPolicyContext(joinPoint, requirePolicies);
        PolicyDecision decision = policyEvaluator.evaluateMultiple(policyIds, context, algorithm);
        
        if (decision.isDeny()) {
            throw new AccessDeniedException(requirePolicies.message() + ": " + decision.getReason());
        }
        
        if (requirePolicies.requireApplicable() && decision.isNotApplicable()) {
            throw new AccessDeniedException("No applicable policies found");
        }
        
        if (decision.isIndeterminate()) {
            throw new AccessDeniedException("Policy evaluation error: " + decision.getReason());
        }
        
        return joinPoint.proceed();
    }
    
    @Around("@annotation(requireOwnership)")
    public Object checkOwnership(ProceedingJoinPoint joinPoint, RequireOwnership requireOwnership) throws Throwable {
        if (!SecurityContextHolder.hasContext()) {
            throw new AccessDeniedException("No security context available");
        }
        
        // Allow system admins to bypass ownership check
        if (requireOwnership.allowSystemAdmin() && securityService.isSystemAdmin()) {
            return joinPoint.proceed();
        }
        
        // Extract resource ID and check ownership
        Long resourceId = extractParameter(joinPoint, requireOwnership.resourceIdParam(), Long.class);
        if (resourceId == null) {
            throw new AccessDeniedException("Resource ID not found");
        }
        
        // For chapter admin bypass, we need chapter context
        if (requireOwnership.allowChapterAdmin()) {
            // This would require additional logic to determine chapter from resource
            // Implementation depends on specific resource types and relationships
        }
        
        // For now, check direct ownership (this would need to be enhanced based on actual resource entities)
        Long currentUserId = SecurityContextHolder.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(resourceId)) {
            throw new AccessDeniedException(requireOwnership.message());
        }
        
        return joinPoint.proceed();
    }
    
    private Long extractChapterId(ProceedingJoinPoint joinPoint, String paramName) {
        return extractParameter(joinPoint, paramName, Long.class);
    }
    
    @SuppressWarnings("unchecked")
    private <T> T extractParameter(ProceedingJoinPoint joinPoint, String paramName, Class<T> type) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                Object value = args[i];
                if (type.isInstance(value)) {
                    return (T) value;
                }
            }
        }
        
        return null;
    }
    
    private PolicyEvaluationContext buildPolicyContext(ProceedingJoinPoint joinPoint, RequirePolicies requirePolicies) {
        Map<String, Object> userAttributes = extractAttributes(joinPoint, requirePolicies.userAttributes());
        Map<String, Object> resourceAttributes = extractAttributes(joinPoint, requirePolicies.resourceAttributes());
        Map<String, Object> environmentAttributes = extractAttributes(joinPoint, requirePolicies.environmentAttributes());
        Map<String, Object> actionAttributes = extractAttributes(joinPoint, requirePolicies.actionAttributes());
        
        // Add default user attributes from security context
        if (SecurityContextHolder.hasContext()) {
            var currentUser = SecurityContextHolder.getCurrentUser();
            if (currentUser != null) {
                userAttributes.put("id", currentUser.getId());
                userAttributes.put("email", currentUser.getEmail());
                // Add other user attributes as needed
            }
        }
        
        return new PolicyEvaluationContext(userAttributes, resourceAttributes, environmentAttributes, actionAttributes);
    }
    
    private Map<String, Object> extractAttributes(ProceedingJoinPoint joinPoint, String[] attributeMappings) {
        Map<String, Object> attributes = new HashMap<>();
        
        for (String mapping : attributeMappings) {
            String[] parts = mapping.split("->");
            if (parts.length == 2) {
                String paramName = parts[0].trim();
                String attributeKey = parts[1].trim();
                Object value = extractParameter(joinPoint, paramName, Object.class);
                if (value != null) {
                    attributes.put(attributeKey, value);
                }
            }
        }
        
        return attributes;
    }
}