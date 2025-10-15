package com.turningpoint.chapterorganizer.security.abac;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of a policy evaluation decision.
 */
public class PolicyDecision {
    
    private final Decision decision;
    private final String reason;
    private final List<String> obligations;
    private final List<String> advice;
    
    public PolicyDecision(Decision decision, String reason) {
        this.decision = decision;
        this.reason = reason;
        this.obligations = new ArrayList<>();
        this.advice = new ArrayList<>();
    }
    
    public PolicyDecision(Decision decision, String reason, List<String> obligations, List<String> advice) {
        this.decision = decision;
        this.reason = reason;
        this.obligations = obligations != null ? new ArrayList<>(obligations) : new ArrayList<>();
        this.advice = advice != null ? new ArrayList<>(advice) : new ArrayList<>();
    }
    
    public Decision getDecision() {
        return decision;
    }
    
    public String getReason() {
        return reason;
    }
    
    public List<String> getObligations() {
        return obligations;
    }
    
    public List<String> getAdvice() {
        return advice;
    }
    
    public boolean isPermit() {
        return decision == Decision.PERMIT;
    }
    
    public boolean isDeny() {
        return decision == Decision.DENY;
    }
    
    public boolean isNotApplicable() {
        return decision == Decision.NOT_APPLICABLE;
    }
    
    public boolean isIndeterminate() {
        return decision == Decision.INDETERMINATE;
    }
    
    // Factory methods for common decisions
    public static PolicyDecision permit(String reason) {
        return new PolicyDecision(Decision.PERMIT, reason);
    }
    
    public static PolicyDecision deny(String reason) {
        return new PolicyDecision(Decision.DENY, reason);
    }
    
    public static PolicyDecision notApplicable(String reason) {
        return new PolicyDecision(Decision.NOT_APPLICABLE, reason);
    }
    
    public static PolicyDecision indeterminate(String reason) {
        return new PolicyDecision(Decision.INDETERMINATE, reason);
    }
    
    public static PolicyDecision permit(String reason, List<String> obligations, List<String> advice) {
        return new PolicyDecision(Decision.PERMIT, reason, obligations, advice);
    }
    
    public static PolicyDecision deny(String reason, List<String> obligations, List<String> advice) {
        return new PolicyDecision(Decision.DENY, reason, obligations, advice);
    }
    
    public enum Decision {
        PERMIT,           // Access is granted
        DENY,            // Access is denied
        NOT_APPLICABLE,  // Policy doesn't apply to this request
        INDETERMINATE    // Cannot determine (error in evaluation)
    }
    
    @Override
    public String toString() {
        return "PolicyDecision{" +
                "decision=" + decision +
                ", reason='" + reason + '\'' +
                ", obligations=" + obligations +
                ", advice=" + advice +
                '}';
    }
}