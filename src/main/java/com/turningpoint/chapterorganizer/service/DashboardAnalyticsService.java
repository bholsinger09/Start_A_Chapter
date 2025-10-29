package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import com.turningpoint.chapterorganizer.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardAnalyticsService {

    private final ChapterRepository chapterRepository;
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final BlogRepository blogRepository;

    @Autowired
    public DashboardAnalyticsService(
            ChapterRepository chapterRepository,
            MemberRepository memberRepository,
            EventRepository eventRepository,
            BlogRepository blogRepository) {
        this.chapterRepository = chapterRepository;
        this.memberRepository = memberRepository;
        this.eventRepository = eventRepository;
        this.blogRepository = blogRepository;
    }

    /**
     * Get comprehensive dashboard statistics
     */
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Overall statistics
        stats.put("totalChapters", chapterRepository.count());
        stats.put("totalMembers", memberRepository.count());
        stats.put("activeMembers", memberRepository.countByActiveTrue());
        stats.put("totalEvents", eventRepository.count());
        stats.put("totalBlogs", blogRepository.count());
        
        // Recent activity (last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        stats.put("newMembersThisMonth", memberRepository.countByCreatedAtAfter(thirtyDaysAgo));
        stats.put("eventsThisMonth", eventRepository.countByCreatedAtAfter(thirtyDaysAgo));
        stats.put("blogsThisMonth", blogRepository.countByCreatedAtAfter(thirtyDaysAgo));
        
        // Growth metrics
        LocalDateTime lastMonth = LocalDateTime.now().minus(60, ChronoUnit.DAYS);
        long membersLastMonth = memberRepository.countByCreatedAtAfter(lastMonth);
        long membersThisMonth = memberRepository.countByCreatedAtAfter(thirtyDaysAgo);
        double memberGrowthRate = membersLastMonth > 0 ? 
            ((double)(membersThisMonth - membersLastMonth) / membersLastMonth * 100) : 0;
        stats.put("memberGrowthRate", Math.round(memberGrowthRate * 100.0) / 100.0);
        
        return stats;
    }

    /**
     * Get chapter statistics breakdown
     */
    public List<Map<String, Object>> getChapterStatistics() {
        List<Chapter> chapters = chapterRepository.findAll();
        
        return chapters.stream().map(chapter -> {
            Map<String, Object> chapterStats = new HashMap<>();
            chapterStats.put("id", chapter.getId());
            chapterStats.put("name", chapter.getName());
            chapterStats.put("university", chapter.getUniversityName() != null ? chapter.getUniversityName() : (chapter.getInstitution() != null ? chapter.getInstitution().getName() : "Unknown"));
            chapterStats.put("memberCount", memberRepository.countByChapterAndActiveTrue(chapter));
            chapterStats.put("totalMembers", memberRepository.countByChapter(chapter));
            chapterStats.put("eventCount", eventRepository.countByChapter(chapter));
            
            // Recent activity for this chapter
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
            chapterStats.put("recentMembers", memberRepository.countByChapterAndCreatedAtAfter(chapter, thirtyDaysAgo));
            chapterStats.put("recentEvents", eventRepository.countByChapterAndCreatedAtAfter(chapter, thirtyDaysAgo));
            
            return chapterStats;
        }).collect(Collectors.toList());
    }

    /**
     * Get recent activity timeline
     */
    public List<Map<String, Object>> getRecentActivity(int limit) {
        List<Map<String, Object>> activities = new java.util.ArrayList<>();
        
        // Recent members
        List<Member> recentMembers = memberRepository.findTop10ByOrderByCreatedAtDesc();
        for (Member member : recentMembers) {
            if (activities.size() >= limit) break;
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "member_joined");
            activity.put("title", member.getFirstName() + " " + member.getLastName() + " joined");
            activity.put("description", "New member at " + (member.getChapter() != null ? member.getChapter().getName() : "Unknown Chapter"));
            activity.put("timestamp", member.getCreatedAt());
            activity.put("icon", "bi-person-plus");
            activity.put("iconClass", "text-success");
            activities.add(activity);
        }
        
        // Recent events
        List<Event> recentEvents = eventRepository.findTop10ByOrderByCreatedAtDesc();
        for (Event event : recentEvents) {
            if (activities.size() >= limit) break;
            Map<String, Object> activity = new HashMap<>();
            activity.put("type", "event_created");
            activity.put("title", "Event: " + event.getTitle());
            activity.put("description", "Scheduled for " + event.getEventDateTime().toString());
            activity.put("timestamp", event.getCreatedAt());
            activity.put("icon", "bi-calendar-plus");
            activity.put("iconClass", "text-primary");
            activities.add(activity);
        }
        
        // Sort by timestamp (most recent first)
        activities.sort((a, b) -> {
            LocalDateTime timestampA = (LocalDateTime) a.get("timestamp");
            LocalDateTime timestampB = (LocalDateTime) b.get("timestamp");
            return timestampB.compareTo(timestampA);
        });
        
        // Return only the requested number of activities
        return activities.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Get upcoming events for dashboard
     */
    public List<Map<String, Object>> getUpcomingEvents(int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = now.plus(30, ChronoUnit.DAYS);
        
        List<Event> upcomingEvents = eventRepository.findByEventDateTimeBetweenOrderByEventDateTimeAsc(now, nextMonth);
        
        return upcomingEvents.stream()
            .limit(limit)
            .map(event -> {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("id", event.getId());
                eventData.put("name", event.getTitle());
                eventData.put("description", event.getDescription());
                eventData.put("eventDate", event.getEventDateTime());
                eventData.put("eventType", event.getType());
                eventData.put("chapterName", event.getChapter() != null ? event.getChapter().getName() : "All Chapters");
                
                // Calculate days until event
                long daysUntil = ChronoUnit.DAYS.between(now.toLocalDate(), event.getEventDateTime().toLocalDate());
                eventData.put("daysUntil", daysUntil);
                
                return eventData;
            })
            .collect(Collectors.toList());
    }

    /**
     * Get user-specific dashboard data based on member ID
     */
    public Map<String, Object> getUserDashboardData(Long memberId) {
        Map<String, Object> userData = new HashMap<>();
        
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return userData;
        }
        
        userData.put("memberInfo", Map.of(
            "name", member.getFirstName() + " " + member.getLastName(),
            "role", member.getRole(),
            "chapterName", member.getChapter() != null ? member.getChapter().getName() : "No Chapter"
        ));
        
        // User's chapter statistics (if they belong to a chapter)
        if (member.getChapter() != null) {
            Chapter userChapter = member.getChapter();
            userData.put("chapterStats", Map.of(
                "memberCount", memberRepository.countByChapterAndActiveTrue(userChapter),
                "upcomingEvents", eventRepository.countByChapterAndEventDateTimeAfter(userChapter, LocalDateTime.now()),
                "recentBlogs", blogRepository.countByCreatedAtAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))
            ));
        }
        
        return userData;
    }

    /**
     * Get monthly growth data for charts
     */
    public Map<String, Object> getMonthlyGrowthData() {
        Map<String, Object> growthData = new HashMap<>();
        
        // Get data for last 12 months
        LocalDateTime startDate = LocalDateTime.now().minus(12, ChronoUnit.MONTHS);
        
        List<Map<String, Object>> monthlyData = new java.util.ArrayList<>();
        
        for (int i = 11; i >= 0; i--) {
            LocalDateTime monthStart = LocalDateTime.now().minus(i, ChronoUnit.MONTHS).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plus(1, ChronoUnit.MONTHS);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", monthStart.getMonth().toString());
            monthData.put("year", monthStart.getYear());
            monthData.put("newMembers", memberRepository.countByCreatedAtBetween(monthStart, monthEnd));
            monthData.put("newEvents", eventRepository.countByCreatedAtBetween(monthStart, monthEnd));
            monthData.put("newBlogs", blogRepository.countByCreatedAtBetween(monthStart, monthEnd));
            
            monthlyData.add(monthData);
        }
        
        growthData.put("monthlyData", monthlyData);
        return growthData;
    }
}