package com.turningpoint.chapterorganizer.security.abac;

/**
 * Interface for ABAC policy evaluation.
 */
public interface PolicyEvaluator {
    
    /**
     * Evaluate a policy against the given context
     */
    PolicyDecision evaluate(String policyId, PolicyEvaluationContext context);
    
    /**
     * Evaluate multiple policies and combine results
     */
    PolicyDecision evaluateMultiple(String[] policyIds, PolicyEvaluationContext context, CombiningAlgorithm algorithm);
    
    /**
     * Check if a policy exists
     */
    boolean policyExists(String policyId);
    
    /**
     * Get policy description
     */
    String getPolicyDescription(String policyId);
    
    enum CombiningAlgorithm {
        PERMIT_OVERRIDES,   // If any policy permits, result is permit
        DENY_OVERRIDES,     // If any policy denies, result is deny
        FIRST_APPLICABLE,   // Use result of first applicable policy
        UNANIMOUS           // All policies must permit
    }
}