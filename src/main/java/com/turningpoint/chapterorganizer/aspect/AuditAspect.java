package com.turningpoint.chapterorganizer.aspect;

import com.turningpoint.chapterorganizer.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;

    @Autowired
    public AuditAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @AfterReturning(value = "execution(* com.turningpoint.chapterorganizer.service.ChapterService.createChapter(..))", returning = "result")
    public void auditChapterCreation(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                // Extract chapter ID from result
                Method getIdMethod = result.getClass().getMethod("getId");
                Long chapterId = (Long) getIdMethod.invoke(result);
                
                auditService.logCreate("Chapter", chapterId, result, "system");
            }
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to audit chapter creation: " + e.getMessage());
        }
    }

    @AfterReturning(value = "execution(* com.turningpoint.chapterorganizer.service.ChapterService.updateChapter(..))", returning = "result")
    public void auditChapterUpdate(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                Method getIdMethod = result.getClass().getMethod("getId");
                Long chapterId = (Long) getIdMethod.invoke(result);
                
                // Note: For full before/after comparison, we'd need to capture the old state
                auditService.logUpdate("Chapter", chapterId, null, result, "system");
            }
        } catch (Exception e) {
            System.err.println("Failed to audit chapter update: " + e.getMessage());
        }
    }

    @AfterReturning(value = "execution(* com.turningpoint.chapterorganizer.service.MemberService.createMember(..))", returning = "result")
    public void auditMemberCreation(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                Method getIdMethod = result.getClass().getMethod("getId");
                Long memberId = (Long) getIdMethod.invoke(result);
                
                Method getChapterMethod = result.getClass().getMethod("getChapter");
                Object chapter = getChapterMethod.invoke(result);
                Long chapterId = null;
                if (chapter != null) {
                    Method getChapterIdMethod = chapter.getClass().getMethod("getId");
                    chapterId = (Long) getChapterIdMethod.invoke(chapter);
                }
                
                auditService.logCreate("Member", memberId, result, "system");
                
                // Record metric for new member
                auditService.recordMetric("new_members_daily", 1.0, "count", "growth", chapterId);
            }
        } catch (Exception e) {
            System.err.println("Failed to audit member creation: " + e.getMessage());
        }
    }

    @AfterReturning(value = "execution(* com.turningpoint.chapterorganizer.service.EventService.createEvent(..))", returning = "result")
    public void auditEventCreation(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                Method getIdMethod = result.getClass().getMethod("getId");
                Long eventId = (Long) getIdMethod.invoke(result);
                
                Method getChapterMethod = result.getClass().getMethod("getChapter");
                Object chapter = getChapterMethod.invoke(result);
                Long chapterId = null;
                if (chapter != null) {
                    Method getChapterIdMethod = chapter.getClass().getMethod("getId");
                    chapterId = (Long) getChapterIdMethod.invoke(chapter);
                }
                
                auditService.logCreate("Event", eventId, result, "system");
                
                // Record metric for new event
                auditService.recordMetric("new_events_daily", 1.0, "count", "activity", chapterId);
            }
        } catch (Exception e) {
            System.err.println("Failed to audit event creation: " + e.getMessage());
        }
    }

    @AfterReturning(value = "execution(* com.turningpoint.chapterorganizer.service.EventService.createOrUpdateRSVP(..))", returning = "result")
    public void auditRSVPAction(JoinPoint joinPoint, Object result) {
        try {
            if (result != null && joinPoint.getArgs().length >= 4) {
                Long eventId = (Long) joinPoint.getArgs()[0];
                Long memberId = (Long) joinPoint.getArgs()[1];
                String status = joinPoint.getArgs()[2].toString();
                
                Method getEventMethod = result.getClass().getMethod("getEvent");
                Object event = getEventMethod.invoke(result);
                Long chapterId = null;
                if (event != null) {
                    Method getChapterMethod = event.getClass().getMethod("getChapter");
                    Object chapter = getChapterMethod.invoke(event);
                    if (chapter != null) {
                        Method getChapterIdMethod = chapter.getClass().getMethod("getId");
                        chapterId = (Long) getChapterIdMethod.invoke(chapter);
                    }
                }
                
                auditService.logRSVP(eventId, memberId, status, "system", chapterId);
                
                // Record metric for RSVP activity
                auditService.recordMetric("rsvp_activity_daily", 1.0, "count", "engagement", chapterId);
            }
        } catch (Exception e) {
            System.err.println("Failed to audit RSVP action: " + e.getMessage());
        }
    }

    @AfterThrowing(value = "execution(* com.turningpoint.chapterorganizer.service.*.*(..))", throwing = "exception")
    public void auditServiceException(JoinPoint joinPoint, Exception exception) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            
            auditService.logAction(
                com.turningpoint.chapterorganizer.entity.AuditAction.SYSTEM,
                "Service",
                null,
                "system",
                String.format("Exception in %s.%s: %s", className, methodName, exception.getMessage()),
                null
            );
            
            // Record error metric
            auditService.recordMetric("service_errors_daily", 1.0, "count", "errors");
        } catch (Exception e) {
            System.err.println("Failed to audit service exception: " + e.getMessage());
        }
    }

    // Performance monitoring
    @AfterReturning(value = "execution(* com.turningpoint.chapterorganizer.controller.*.*(..))")
    public void auditControllerPerformance(JoinPoint joinPoint) {
        try {
            // This is a simple example - in real implementation you'd measure execution time
            long executionTime = System.currentTimeMillis() % 1000; // Simplified for demo
            
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            
            // Record API response time metric
            auditService.recordApiResponseTime(executionTime);
        } catch (Exception e) {
            System.err.println("Failed to audit controller performance: " + e.getMessage());
        }
    }
}