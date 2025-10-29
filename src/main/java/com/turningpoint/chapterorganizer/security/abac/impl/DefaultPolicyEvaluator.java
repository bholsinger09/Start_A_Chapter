package com.turningpoint.chapterorganizer.security.abac.impl;

import com.turningpoint.chapterorganizer.security.abac.PolicyDecision;
import com.turningpoint.chapterorganizer.security.abac.PolicyEvaluationContext;
import com.turningpoint.chapterorganizer.security.abac.PolicyEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Default implementation of ABAC policy evaluator with built-in policies.
 */
@Service
public class DefaultPolicyEvaluator implements PolicyEvaluator {
    
    private final Map<String, PolicyRule> policies = new HashMap<>();
    
    public DefaultPolicyEvaluator() {
        initializeDefaultPolicies();
    }
    
    @Override
    public PolicyDecision evaluate(String policyId, PolicyEvaluationContext context) {
        PolicyRule policy = policies.get(policyId);
        if (policy == null) {
            return PolicyDecision.notApplicable("Policy not found: " + policyId);
        }
        
        try {
            return policy.evaluate(context);
        } catch (Exception e) {
            return PolicyDecision.indeterminate("Error evaluating policy: " + e.getMessage());
        }
    }
    
    @Override
    public PolicyDecision evaluateMultiple(String[] policyIds, PolicyEvaluationContext context, CombiningAlgorithm algorithm) {
        List<PolicyDecision> decisions = new ArrayList<>();
        
        for (String policyId : policyIds) {
            PolicyDecision decision = evaluate(policyId, context);
            decisions.add(decision);
        }
        
        return combineDecisions(decisions, algorithm);
    }
    
    @Override
    public boolean policyExists(String policyId) {
        return policies.containsKey(policyId);
    }
    
    @Override
    public String getPolicyDescription(String policyId) {
        PolicyRule policy = policies.get(policyId);
        return policy != null ? policy.getDescription() : "Policy not found";
    }
    
    /**
     * Add a custom policy
     */
    public void addPolicy(String policyId, PolicyRule policy) {
        policies.put(policyId, policy);
    }
    
    /**
     * Remove a policy
     */
    public void removePolicy(String policyId) {
        policies.remove(policyId);
    }
    
    private PolicyDecision combineDecisions(List<PolicyDecision> decisions, CombiningAlgorithm algorithm) {
        switch (algorithm) {
            case PERMIT_OVERRIDES:
                return permitOverrides(decisions);
            case DENY_OVERRIDES:
                return denyOverrides(decisions);
            case FIRST_APPLICABLE:
                return firstApplicable(decisions);
            case UNANIMOUS:
                return unanimous(decisions);
            default:
                return PolicyDecision.indeterminate("Unknown combining algorithm");
        }
    }
    
    private PolicyDecision permitOverrides(List<PolicyDecision> decisions) {
        for (PolicyDecision decision : decisions) {
            if (decision.isPermit()) {
                return decision;
            }
        }
        
        for (PolicyDecision decision : decisions) {
            if (decision.isDeny()) {
                return decision;
            }
        }
        
        return PolicyDecision.notApplicable("No applicable policies");
    }
    
    private PolicyDecision denyOverrides(List<PolicyDecision> decisions) {
        for (PolicyDecision decision : decisions) {
            if (decision.isDeny()) {
                return decision;
            }
        }
        
        for (PolicyDecision decision : decisions) {
            if (decision.isPermit()) {
                return decision;
            }
        }
        
        return PolicyDecision.notApplicable("No applicable policies");
    }
    
    private PolicyDecision firstApplicable(List<PolicyDecision> decisions) {
        for (PolicyDecision decision : decisions) {
            if (!decision.isNotApplicable()) {
                return decision;
            }
        }
        
        return PolicyDecision.notApplicable("No applicable policies");
    }
    
    private PolicyDecision unanimous(List<PolicyDecision> decisions) {
        boolean hasApplicable = false;
        
        for (PolicyDecision decision : decisions) {
            if (!decision.isNotApplicable()) {
                hasApplicable = true;
                if (!decision.isPermit()) {
                    return decision.isDeny() ? decision : PolicyDecision.deny("Not unanimous");
                }
            }
        }
        
        return hasApplicable ? PolicyDecision.permit("Unanimous permit") 
                             : PolicyDecision.notApplicable("No applicable policies");
    }
    
    private void initializeDefaultPolicies() {
        // Ownership-based access policy
        policies.put("ownership-access", new PolicyRule(
            "Ownership Access Policy",
            "Users can access resources they own"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                Long userId = context.getUserAttribute("id", Long.class);
                Long ownerId = context.getResourceAttribute("ownerId", Long.class);
                
                if (userId == null || ownerId == null) {
                    return PolicyDecision.notApplicable("Missing user or owner information");
                }
                
                if (userId.equals(ownerId)) {
                    return PolicyDecision.permit("User owns the resource");
                } else {
                    return PolicyDecision.deny("User does not own the resource");
                }
            }
        });
        
        // Chapter membership access policy
        policies.put("chapter-membership", new PolicyRule(
            "Chapter Membership Policy",
            "Users can access resources in their chapter"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                @SuppressWarnings("unchecked")
                Set<Long> userChapters = context.getUserAttribute("chapterIds", Set.class);
                Long resourceChapter = context.getResourceAttribute("chapterId", Long.class);
                
                if (userChapters == null || resourceChapter == null) {
                    return PolicyDecision.notApplicable("Missing chapter information");
                }
                
                if (userChapters.contains(resourceChapter)) {
                    return PolicyDecision.permit("User is member of resource chapter");
                } else {
                    return PolicyDecision.deny("User is not member of resource chapter");
                }
            }
        });
        
        // Business hours access policy
        policies.put("business-hours", new PolicyRule(
            "Business Hours Policy",
            "Certain operations only allowed during business hours"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                LocalDateTime now = LocalDateTime.now();
                LocalTime currentTime = now.toLocalTime();
                
                LocalTime startTime = LocalTime.of(8, 0);  // 8:00 AM
                LocalTime endTime = LocalTime.of(18, 0);   // 6:00 PM
                
                if (currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
                    return PolicyDecision.permit("Within business hours");
                } else {
                    return PolicyDecision.deny("Outside business hours");
                }
            }
        });
        
        // Event capacity policy
        policies.put("event-capacity", new PolicyRule(
            "Event Capacity Policy",
            "RSVP allowed only if event has capacity"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                Integer currentAttendees = context.getResourceAttribute("currentAttendees", Integer.class);
                Integer maxCapacity = context.getResourceAttribute("maxCapacity", Integer.class);
                
                if (currentAttendees == null || maxCapacity == null) {
                    return PolicyDecision.permit("No capacity restrictions");
                }
                
                if (currentAttendees < maxCapacity) {
                    return PolicyDecision.permit("Event has available capacity");
                } else {
                    return PolicyDecision.deny("Event is at full capacity");
                }
            }
        });
        
        // Role hierarchy policy
        policies.put("role-hierarchy", new PolicyRule(
            "Role Hierarchy Policy",
            "Users can manage resources of users with lower hierarchy"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                Integer userHierarchy = context.getUserAttribute("hierarchyLevel", Integer.class);
                Integer targetHierarchy = context.getResourceAttribute("targetUserHierarchy", Integer.class);
                
                if (userHierarchy == null || targetHierarchy == null) {
                    return PolicyDecision.notApplicable("Missing hierarchy information");
                }
                
                if (userHierarchy > targetHierarchy) {
                    return PolicyDecision.permit("User has higher hierarchy level");
                } else {
                    return PolicyDecision.deny("User has equal or lower hierarchy level");
                }
            }
        });
        
        // Event time policy
        policies.put("event-timing", new PolicyRule(
            "Event Timing Policy",
            "Event operations based on event timing"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime eventStart = context.getResourceAttribute("eventStartTime", LocalDateTime.class);
                String action = context.getActionAttribute("type", String.class);
                
                if (eventStart == null || action == null) {
                    return PolicyDecision.notApplicable("Missing event timing information");
                }
                
                switch (action) {
                    case "rsvp":
                        // Allow RSVP up to 1 hour before event
                        if (now.isBefore(eventStart.minusHours(1))) {
                            return PolicyDecision.permit("RSVP window is open");
                        } else {
                            return PolicyDecision.deny("RSVP window has closed");
                        }
                    case "cancel":
                        // Allow cancellation up to 2 hours before event
                        if (now.isBefore(eventStart.minusHours(2))) {
                            return PolicyDecision.permit("Cancellation window is open");
                        } else {
                            return PolicyDecision.deny("Cancellation window has closed");
                        }
                    case "modify":
                        // Allow modification up to 24 hours before event
                        if (now.isBefore(eventStart.minusDays(1))) {
                            return PolicyDecision.permit("Modification window is open");
                        } else {
                            return PolicyDecision.deny("Modification window has closed");
                        }
                    default:
                        return PolicyDecision.notApplicable("Unknown action type");
                }
            }
        });
        
        // Resource visibility policy
        policies.put("resource-visibility", new PolicyRule(
            "Resource Visibility Policy",
            "Control resource visibility based on privacy settings"
        ) {
            @Override
            public PolicyDecision evaluate(PolicyEvaluationContext context) {
                String visibility = context.getResourceAttribute("visibility", String.class);
                Boolean isOwner = context.getUserAttribute("isOwner", Boolean.class);
                Boolean isMember = context.getUserAttribute("isMember", Boolean.class);
                
                if (visibility == null) {
                    return PolicyDecision.permit("No visibility restrictions");
                }
                
                switch (visibility.toLowerCase()) {
                    case "public":
                        return PolicyDecision.permit("Resource is public");
                    case "members":
                        if (Boolean.TRUE.equals(isMember)) {
                            return PolicyDecision.permit("User is a member");
                        } else {
                            return PolicyDecision.deny("Resource is members-only");
                        }
                    case "private":
                        if (Boolean.TRUE.equals(isOwner)) {
                            return PolicyDecision.permit("User is the owner");
                        } else {
                            return PolicyDecision.deny("Resource is private");
                        }
                    default:
                        return PolicyDecision.notApplicable("Unknown visibility setting");
                }
            }
        });
    }
    
    /**
     * Abstract base class for policy rules
     */
    public abstract static class PolicyRule {
        private final String name;
        private final String description;
        
        public PolicyRule(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public abstract PolicyDecision evaluate(PolicyEvaluationContext context);
    }
}