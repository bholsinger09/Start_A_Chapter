package com.turningpoint.chapterorganizer.security.annotation;

import com.turningpoint.chapterorganizer.security.abac.PolicyEvaluator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for attribute-based access control using ABAC policies.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePolicies {
    
    /**
     * Policy IDs to evaluate
     */
    String[] value();
    
    /**
     * Combining algorithm for multiple policies
     */
    PolicyEvaluator.CombiningAlgorithm algorithm() default PolicyEvaluator.CombiningAlgorithm.DENY_OVERRIDES;
    
    /**
     * User attribute mappings (parameter name -> attribute key)
     */
    String[] userAttributes() default {};
    
    /**
     * Resource attribute mappings (parameter name -> attribute key)
     */
    String[] resourceAttributes() default {};
    
    /**
     * Environment attribute mappings (parameter name -> attribute key)
     */
    String[] environmentAttributes() default {};
    
    /**
     * Action attribute mappings (parameter name -> attribute key)
     */
    String[] actionAttributes() default {};
    
    /**
     * Custom error message when policy evaluation fails
     */
    String message() default "Access denied by policy evaluation";
    
    /**
     * Whether to require all policies to be applicable (vs allowing not applicable)
     */
    boolean requireApplicable() default false;
}