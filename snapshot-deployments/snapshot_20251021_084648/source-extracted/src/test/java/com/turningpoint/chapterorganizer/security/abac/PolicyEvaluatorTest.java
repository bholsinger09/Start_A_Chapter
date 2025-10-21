package com.turningpoint.chapterorganizer.security.abac;

import com.turningpoint.chapterorganizer.security.abac.impl.DefaultPolicyEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ABAC policy evaluation.
 */
class PolicyEvaluatorTest {
    
    private DefaultPolicyEvaluator policyEvaluator;
    
    @BeforeEach
    void setUp() {
        policyEvaluator = new DefaultPolicyEvaluator();
    }
    
    @Test
    void testOwnershipPolicy() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("id", 1L);
        Map<String, Object> resourceAttributes = Map.of("ownerId", 1L);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("ownership-access", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("User owns the resource", decision.getReason());
    }
    
    @Test
    void testOwnershipPolicyDenied() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("id", 1L);
        Map<String, Object> resourceAttributes = Map.of("ownerId", 2L);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("ownership-access", context);
        
        // Assert
        assertTrue(decision.isDeny());
        assertEquals("User does not own the resource", decision.getReason());
    }
    
    @Test
    void testChapterMembershipPolicy() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("chapterIds", Set.of(1L, 2L));
        Map<String, Object> resourceAttributes = Map.of("chapterId", 1L);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("chapter-membership", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("User is member of resource chapter", decision.getReason());
    }
    
    @Test
    void testChapterMembershipPolicyDenied() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("chapterIds", Set.of(2L, 3L));
        Map<String, Object> resourceAttributes = Map.of("chapterId", 1L);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("chapter-membership", context);
        
        // Assert
        assertTrue(decision.isDeny());
        assertEquals("User is not member of resource chapter", decision.getReason());
    }
    
    @Test
    void testEventCapacityPolicy() {
        // Arrange
        Map<String, Object> resourceAttributes = Map.of(
                "currentAttendees", 25,
                "maxCapacity", 30);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                null, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("event-capacity", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("Event has available capacity", decision.getReason());
    }
    
    @Test
    void testEventCapacityPolicyFull() {
        // Arrange
        Map<String, Object> resourceAttributes = Map.of(
                "currentAttendees", 30,
                "maxCapacity", 30);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                null, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("event-capacity", context);
        
        // Assert
        assertTrue(decision.isDeny());
        assertEquals("Event is at full capacity", decision.getReason());
    }
    
    @Test
    void testRoleHierarchyPolicy() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("hierarchyLevel", 70);
        Map<String, Object> resourceAttributes = Map.of("targetUserHierarchy", 50);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("role-hierarchy", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("User has higher hierarchy level", decision.getReason());
    }
    
    @Test
    void testRoleHierarchyPolicyDenied() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("hierarchyLevel", 50);
        Map<String, Object> resourceAttributes = Map.of("targetUserHierarchy", 70);
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("role-hierarchy", context);
        
        // Assert
        assertTrue(decision.isDeny());
        assertEquals("User has equal or lower hierarchy level", decision.getReason());
    }
    
    @Test
    void testEventTimingPolicyRSVP() {
        // Arrange
        LocalDateTime eventStart = LocalDateTime.now().plusHours(2);
        Map<String, Object> resourceAttributes = Map.of("eventStartTime", eventStart);
        Map<String, Object> actionAttributes = Map.of("type", "rsvp");
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                null, resourceAttributes, null, actionAttributes);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("event-timing", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("RSVP window is open", decision.getReason());
    }
    
    @Test
    void testEventTimingPolicyRSVPClosed() {
        // Arrange
        LocalDateTime eventStart = LocalDateTime.now().plusMinutes(30);
        Map<String, Object> resourceAttributes = Map.of("eventStartTime", eventStart);
        Map<String, Object> actionAttributes = Map.of("type", "rsvp");
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                null, resourceAttributes, null, actionAttributes);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("event-timing", context);
        
        // Assert
        assertTrue(decision.isDeny());
        assertEquals("RSVP window has closed", decision.getReason());
    }
    
    @Test
    void testResourceVisibilityPublic() {
        // Arrange
        Map<String, Object> resourceAttributes = Map.of("visibility", "public");
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                null, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("resource-visibility", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("Resource is public", decision.getReason());
    }
    
    @Test
    void testResourceVisibilityMembersOnly() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("isMember", true);
        Map<String, Object> resourceAttributes = Map.of("visibility", "members");
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("resource-visibility", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("User is a member", decision.getReason());
    }
    
    @Test
    void testResourceVisibilityPrivate() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("isOwner", true);
        Map<String, Object> resourceAttributes = Map.of("visibility", "private");
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("resource-visibility", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("User is the owner", decision.getReason());
    }
    
    @Test
    void testMultiplePoliciesPermitOverrides() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("id", 1L);
        Map<String, Object> resourceAttributes = Map.of(
                "ownerId", 2L, // Ownership will deny
                "visibility", "public"); // Visibility will permit
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluateMultiple(
                new String[]{"ownership-access", "resource-visibility"},
                context,
                PolicyEvaluator.CombiningAlgorithm.PERMIT_OVERRIDES);
        
        // Assert
        assertTrue(decision.isPermit());
    }
    
    @Test
    void testMultiplePoliciesDenyOverrides() {
        // Arrange
        Map<String, Object> userAttributes = Map.of("id", 1L);
        Map<String, Object> resourceAttributes = Map.of(
                "ownerId", 2L, // Ownership will deny
                "visibility", "public"); // Visibility will permit
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluateMultiple(
                new String[]{"ownership-access", "resource-visibility"},
                context,
                PolicyEvaluator.CombiningAlgorithm.DENY_OVERRIDES);
        
        // Assert
        assertTrue(decision.isDeny());
    }
    
    @Test
    void testNonExistentPolicy() {
        // Arrange
        PolicyEvaluationContext context = new PolicyEvaluationContext(null, null, null, null);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("non-existent-policy", context);
        
        // Assert
        assertTrue(decision.isNotApplicable());
        assertTrue(decision.getReason().contains("Policy not found"));
    }
    
    @Test
    void testPolicyExists() {
        // Act & Assert
        assertTrue(policyEvaluator.policyExists("ownership-access"));
        assertTrue(policyEvaluator.policyExists("chapter-membership"));
        assertFalse(policyEvaluator.policyExists("non-existent"));
    }
    
    @Test
    void testCustomPolicy() {
        // Arrange
        DefaultPolicyEvaluator.PolicyRule customPolicy = new DefaultPolicyEvaluator.PolicyRule(
                "Custom Policy", "Test custom policy") {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                String action = context.getActionAttribute("action", String.class);
                return "test".equals(action) ? 
                        PolicyDecision.permit("Test action allowed") :
                        PolicyDecision.deny("Test action not allowed");
            }
        };
        
        policyEvaluator.addPolicy("custom-test", customPolicy);
        
        Map<String, Object> actionAttributes = Map.of("action", "test");
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                null, null, null, actionAttributes);
        
        // Act
        PolicyDecision decision = policyEvaluator.evaluate("custom-test", context);
        
        // Assert
        assertTrue(decision.isPermit());
        assertEquals("Test action allowed", decision.getReason());
    }
    
    @Test
    void testPolicyContextAttributeAccess() {
        // Arrange
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("name", "John Doe");
        userAttributes.put("age", 25);
        
        Map<String, Object> resourceAttributes = new HashMap<>();
        resourceAttributes.put("type", "document");
        resourceAttributes.put("restricted", false);
        
        PolicyEvaluationContext context = new PolicyEvaluationContext(
                userAttributes, resourceAttributes, null, null);
        
        // Act & Assert
        assertEquals("John Doe", context.getUserAttribute("name"));
        assertEquals(25, context.getUserAttribute("age", Integer.class));
        assertEquals("document", context.getResourceAttribute("type"));
        assertEquals(false, context.getResourceAttribute("restricted", Boolean.class));
        
        assertTrue(context.hasUserAttribute("name"));
        assertTrue(context.hasResourceAttribute("type"));
        assertFalse(context.hasEnvironmentAttribute("time"));
        assertFalse(context.hasActionAttribute("method"));
    }
}