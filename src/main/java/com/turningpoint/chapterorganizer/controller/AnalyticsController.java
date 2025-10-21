package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import com.turningpoint.chapterorganizer.repository.EventRSVPRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics controller for dashboard metrics and Chart.js data
 * Provides comprehensive analytics data for the AnalyticsDashboard component
 */
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final ChapterService chapterService;
    private final EventRepository eventRepository;
    private final EventRSVPRepository eventRsvpRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public AnalyticsController(ChapterService chapterService, EventRepository eventRepository,
                               EventRSVPRepository eventRsvpRepository, MemberRepository memberRepository) {
        this.chapterService = chapterService;
        this.eventRepository = eventRepository;
        this.eventRsvpRepository = eventRsvpRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Get comprehensive dashboard analytics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // Real-time metrics
        analytics.put("realTimeMetrics", getRealTimeMetrics());
        
        // Membership growth data for line chart
        analytics.put("membershipGrowth", getMembershipGrowthData());
        
        // Event attendance data for pie chart
        analytics.put("eventAttendance", getEventAttendanceData());
        
        // Chapter performance data for radar chart
        analytics.put("chapterPerformance", getChapterPerformanceData());
        
        // Recent activity
        analytics.put("recentActivity", getRecentActivity());
        
        // Trends and insights
        analytics.put("trends", getTrendsAndInsights());

        return ResponseEntity.ok(analytics);
    }

    /**
     * Get real-time metrics for live display
     */
    @GetMapping("/metrics/realtime")
    public ResponseEntity<Map<String, Object>> getRealTimeMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Current counts
        metrics.put("totalMembers", memberRepository.count());
        metrics.put("activeMembers", memberRepository.countByActiveTrue());
        metrics.put("totalChapters", chapterService.getAllActiveChapters().size());
        metrics.put("totalEvents", eventRepository.count());
        metrics.put("totalRsvps", eventRsvpRepository.count());
        
        // Today's activity
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        metrics.put("todayEvents", eventRepository.countByEventDateTimeBetween(startOfDay, endOfDay));
        metrics.put("todayRsvps", eventRsvpRepository.countByRsvpDateBetween(startOfDay, endOfDay));
        
        // Growth rates (percentage change from last month)
        metrics.put("memberGrowthRate", calculateMemberGrowthRate());
        metrics.put("eventGrowthRate", calculateEventGrowthRate());
        
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get membership growth data for line chart
     */
    @GetMapping("/membership-growth")
    public ResponseEntity<Map<String, Object>> getMembershipGrowthData() {
        Map<String, Object> data = new HashMap<>();
        
        // Get last 12 months of data
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 11; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
            
            long memberCount = memberRepository.countByCreatedAtBetween(monthStart, monthEnd);
            long cumulativeCount = memberRepository.countByCreatedAtBefore(monthEnd);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month.toString());
            monthData.put("newMembers", memberCount);
            monthData.put("totalMembers", cumulativeCount);
            monthData.put("activeMembers", memberRepository.countByActiveTrueAndCreatedAtBefore(monthEnd));
            
            monthlyData.add(monthData);
        }
        
        data.put("monthlyData", monthlyData);
        return ResponseEntity.ok(data);
    }

    /**
     * Get event attendance data for pie chart
     */
    @GetMapping("/event-attendance")
    public ResponseEntity<Map<String, Object>> getEventAttendanceData() {
        Map<String, Object> data = new HashMap<>();
        
        // Get events from last 6 months
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        List<Event> recentEvents = eventRepository.findByEventDateTimeAfter(sixMonthsAgo);
        
        // Group by event type and calculate attendance
        Map<String, Long> attendanceByType = recentEvents.stream()
            .collect(Collectors.groupingBy(
                event -> event.getType() != null ? event.getType().toString() : "UNKNOWN",
                Collectors.summingLong(event -> eventRsvpRepository.countByEventId(event.getId()))
            ));
        
        // Calculate average attendance per event type
        Map<String, Double> avgAttendanceByType = recentEvents.stream()
            .collect(Collectors.groupingBy(
                event -> event.getType() != null ? event.getType().toString() : "UNKNOWN",
                Collectors.averagingLong(event -> eventRsvpRepository.countByEventId(event.getId()))
            ));
        
        data.put("attendanceByType", attendanceByType);
        data.put("averageAttendanceByType", avgAttendanceByType);
        data.put("totalEvents", recentEvents.size());
        
        return ResponseEntity.ok(data);
    }

    /**
     * Get chapter performance data for radar chart
     */
    @GetMapping("/chapter-performance")
    public ResponseEntity<Map<String, Object>> getChapterPerformanceData() {
        Map<String, Object> data = new HashMap<>();
        
            List<Map<String, Object>> chapterMetrics = chapterService.getAllActiveChapters().stream()
            .map(chapter -> {
                Map<String, Object> metrics = new HashMap<>();
                metrics.put("chapterName", chapter.getName());
                metrics.put("memberCount", memberRepository.countByChapter_Id(chapter.getId()));
                metrics.put("activeMembers", memberRepository.countByChapter_IdAndActiveTrue(chapter.getId()));
                metrics.put("eventCount", eventRepository.countByChapterId(chapter.getId()));
                metrics.put("avgAttendance", calculateAverageAttendance(chapter.getId()));
                metrics.put("engagement", calculateEngagementScore(chapter.getId()));
                return metrics;
            })
            .collect(Collectors.toList());        data.put("chapterMetrics", chapterMetrics);
        return ResponseEntity.ok(data);
    }

    /**
     * Get recent activity for activity feed
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<List<Map<String, Object>>> getRecentActivity() {
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // Get recent members (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Member> recentMembers = memberRepository.findByCreatedAtAfterOrderByCreatedAtDesc(thirtyDaysAgo);
        
        recentMembers.stream().limit(10).forEach(member -> {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "member_joined");
            activity.put("message", member.getFirstName() + " " + member.getLastName() + " joined");
            activity.put("timestamp", member.getCreatedAt());
            activity.put("icon", "user-plus");
            activities.add(activity);
        });
        
        // Get recent events
        List<Event> recentEvents = eventRepository.findByEventDateTimeAfterOrderByEventDateTimeDesc(thirtyDaysAgo);
        
        recentEvents.stream().limit(10).forEach(event -> {
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "event_created");
            activity.put("message", "Event '" + event.getTitle() + "' was created");
            activity.put("timestamp", event.getEventDateTime());
            activity.put("icon", "calendar-plus");
            activities.add(activity);
        });
        
        // Sort by timestamp descending
        activities.sort((a, b) -> 
            ((LocalDateTime) b.get("timestamp")).compareTo((LocalDateTime) a.get("timestamp"))
        );
        
        return ResponseEntity.ok(activities.stream().limit(20).collect(Collectors.toList()));
    }

    /**
     * Get trends and insights
     */
    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrendsAndInsights() {
        Map<String, Object> trends = new HashMap<>();
        
        // Calculate various trend metrics
        trends.put("membershipTrend", calculateMembershipTrend());
        trends.put("eventParticipationTrend", calculateEventParticipationTrend());
        trends.put("chapterGrowthTrend", calculateChapterGrowthTrend());
        trends.put("seasonalTrends", calculateSeasonalTrends());
        
        // Insights and recommendations
        List<String> insights = generateInsights();
        trends.put("insights", insights);
        
        return ResponseEntity.ok(trends);
    }

    // Helper methods for calculations

    private double calculateMemberGrowthRate() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        
        long currentMonthMembers = memberRepository.countByCreatedAtAfter(lastMonth);
        long previousMonthMembers = memberRepository.countByCreatedAtBetween(twoMonthsAgo, lastMonth);
        
        if (previousMonthMembers == 0) return currentMonthMembers > 0 ? 100.0 : 0.0;
        
        return ((double) (currentMonthMembers - previousMonthMembers) / previousMonthMembers) * 100.0;
    }

    private double calculateEventGrowthRate() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(2);
        
        long currentMonthEvents = eventRepository.countByEventDateTimeAfter(lastMonth);
        long previousMonthEvents = eventRepository.countByEventDateTimeBetween(twoMonthsAgo, lastMonth);
        
        if (previousMonthEvents == 0) return currentMonthEvents > 0 ? 100.0 : 0.0;
        
        return ((double) (currentMonthEvents - previousMonthEvents) / previousMonthEvents) * 100.0;
    }

    private double calculateAverageAttendance(Long chapterId) {
        List<Event> chapterEvents = eventRepository.findByChapterId(chapterId);
        if (chapterEvents.isEmpty()) return 0.0;
        
        double totalAttendance = chapterEvents.stream()
            .mapToLong(event -> eventRsvpRepository.countByEventId(event.getId()))
            .sum();
        
        return totalAttendance / chapterEvents.size();
    }

    private double calculateEngagementScore(Long chapterId) {
        long memberCount = memberRepository.countByChapter_Id(chapterId);
        long activeMembers = memberRepository.countByChapter_IdAndActiveTrue(chapterId);
        long eventCount = eventRepository.countByChapterId(chapterId);
        
        if (memberCount == 0) return 0.0;
        
        double activeMemberRatio = (double) activeMembers / memberCount;
        double eventsPerMember = memberCount > 0 ? (double) eventCount / memberCount : 0.0;
        
        // Simple engagement score calculation (can be refined)
        return (activeMemberRatio * 0.7 + Math.min(eventsPerMember / 10.0, 1.0) * 0.3) * 100.0;
    }

    private String calculateMembershipTrend() {
        double growthRate = calculateMemberGrowthRate();
        if (growthRate > 10) return "Growing rapidly";
        if (growthRate > 5) return "Steady growth";
        if (growthRate > 0) return "Slow growth";
        if (growthRate == 0) return "Stable";
        return "Declining";
    }

    private String calculateEventParticipationTrend() {
        double eventGrowthRate = calculateEventGrowthRate();
        if (eventGrowthRate > 15) return "Very active";
        if (eventGrowthRate > 5) return "Active";
        if (eventGrowthRate > 0) return "Moderately active";
        return "Low activity";
    }

    private String calculateChapterGrowthTrend() {
        long totalChapters = chapterService.getAllActiveChapters().size();
        if (totalChapters > 10) return "Well established";
        if (totalChapters > 5) return "Growing network";
        if (totalChapters > 2) return "Expanding";
        return "Getting started";
    }

    private Map<String, Object> calculateSeasonalTrends() {
        Map<String, Object> seasonal = new HashMap<>();
        
        // This is a simplified version - in a real app you'd analyze historical data
        seasonal.put("peakSeason", "Fall");
        seasonal.put("lowSeason", "Summer");
        seasonal.put("recommendation", "Focus recruitment efforts in late summer");
        
        return seasonal;
    }

    private List<String> generateInsights() {
        List<String> insights = new ArrayList<>();
        
        double memberGrowth = calculateMemberGrowthRate();
        if (memberGrowth > 20) {
            insights.add("Membership is growing exceptionally well! Consider expanding chapter capacity.");
        } else if (memberGrowth < 0) {
            insights.add("Membership growth has slowed. Consider new recruitment strategies.");
        }
        
        long totalEvents = eventRepository.count();
        long totalMembers = memberRepository.count();
        if (totalMembers > 0 && totalEvents / (double) totalMembers < 0.1) {
            insights.add("Consider organizing more events to increase member engagement.");
        }
        
        if (insights.isEmpty()) {
            insights.add("Overall performance looks good! Keep up the great work.");
        }
        
        return insights;
    }
}