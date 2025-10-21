package com.turningpoint.chapterorganizer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for broadcasting real-time notifications to connected frontend clients
 * via WebSocket. Provides methods to notify about member activities, events,
 * and live metric updates for the analytics dashboard.
 */
@Service
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcasts a new member join notification to all connected clients
     * 
     * @param memberName The name of the member who joined
     * @param chapterId The ID of the chapter they joined
     */
    public void broadcastMemberJoined(String memberName, Long chapterId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "member_joined");
        notification.put("message", memberName + " has joined the chapter");
        notification.put("memberName", memberName);
        notification.put("chapterId", chapterId);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("icon", "user-plus");

        // Send to all subscribers of the notifications topic
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        
        // Send chapter-specific notification
        messagingTemplate.convertAndSend("/topic/chapter/" + chapterId + "/notifications", notification);
    }

    /**
     * Broadcasts a new event creation notification
     * 
     * @param eventTitle The title of the created event
     * @param chapterId The ID of the chapter hosting the event
     * @param eventId The ID of the created event
     */
    public void broadcastEventCreated(String eventTitle, Long chapterId, Long eventId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "event_created");
        notification.put("message", "New event: " + eventTitle);
        notification.put("eventTitle", eventTitle);
        notification.put("chapterId", chapterId);
        notification.put("eventId", eventId);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("icon", "calendar-plus");

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        messagingTemplate.convertAndSend("/topic/chapter/" + chapterId + "/notifications", notification);
    }

    /**
     * Broadcasts an event RSVP notification
     * 
     * @param memberName The name of the member who RSVP'd
     * @param eventTitle The title of the event
     * @param chapterId The chapter ID
     * @param eventId The event ID
     */
    public void broadcastEventRSVP(String memberName, String eventTitle, Long chapterId, Long eventId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "event_rsvp");
        notification.put("message", memberName + " RSVP'd to " + eventTitle);
        notification.put("memberName", memberName);
        notification.put("eventTitle", eventTitle);
        notification.put("chapterId", chapterId);
        notification.put("eventId", eventId);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("icon", "check-circle");

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        messagingTemplate.convertAndSend("/topic/chapter/" + chapterId + "/notifications", notification);
        messagingTemplate.convertAndSend("/topic/event/" + eventId + "/rsvps", notification);
    }

    /**
     * Broadcasts updated real-time metrics to dashboard subscribers
     * 
     * @param totalMembers Current total member count
     * @param totalEvents Current total event count
     * @param totalChapters Current total chapter count
     * @param todayRsvps Today's RSVP count
     */
    public void broadcastMetricsUpdate(long totalMembers, long totalEvents, long totalChapters, long todayRsvps) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalMembers", totalMembers);
        metrics.put("totalEvents", totalEvents);
        metrics.put("totalChapters", totalChapters);
        metrics.put("todayRsvps", todayRsvps);
        metrics.put("timestamp", LocalDateTime.now());
        
        // Calculate growth rates (simplified - could be enhanced with actual calculation)
        metrics.put("memberGrowthRate", totalMembers > 0 ? 5.2 : 0.0); // Mock growth rate
        metrics.put("eventGrowthRate", totalEvents > 0 ? 3.8 : 0.0); // Mock growth rate

        messagingTemplate.convertAndSend("/topic/dashboard/metrics", metrics);
    }

    /**
     * Broadcasts a member status change (activation/deactivation)
     * 
     * @param memberName The name of the member
     * @param isActive Whether the member is now active
     * @param chapterId The chapter ID
     */
    public void broadcastMemberStatusChange(String memberName, boolean isActive, Long chapterId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", isActive ? "member_activated" : "member_deactivated");
        notification.put("message", memberName + " has been " + (isActive ? "activated" : "deactivated"));
        notification.put("memberName", memberName);
        notification.put("isActive", isActive);
        notification.put("chapterId", chapterId);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("icon", isActive ? "user-check" : "user-x");

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        messagingTemplate.convertAndSend("/topic/chapter/" + chapterId + "/notifications", notification);
    }

    /**
     * Broadcasts a chapter creation notification
     * 
     * @param chapterName The name of the new chapter
     * @param institutionName The institution name
     * @param chapterId The new chapter ID
     */
    public void broadcastChapterCreated(String chapterName, String institutionName, Long chapterId) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "chapter_created");
        notification.put("message", "New chapter created: " + chapterName + " at " + institutionName);
        notification.put("chapterName", chapterName);
        notification.put("institutionName", institutionName);
        notification.put("chapterId", chapterId);
        notification.put("timestamp", LocalDateTime.now());
        notification.put("icon", "building");

        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Broadcasts an analytics update notification for the dashboard
     * 
     * @param updateType The type of analytics update
     * @param data The updated data
     */
    public void broadcastAnalyticsUpdate(String updateType, Object data) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", updateType);
        update.put("data", data);
        update.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSend("/topic/dashboard/analytics", update);
    }

    /**
     * Sends a direct message to a specific user
     * 
     * @param userId The target user ID
     * @param message The message to send
     * @param messageType The type of message
     */
    public void sendDirectMessage(Long userId, String message, String messageType) {
        Map<String, Object> directMessage = new HashMap<>();
        directMessage.put("type", messageType);
        directMessage.put("message", message);
        directMessage.put("timestamp", LocalDateTime.now());

        messagingTemplate.convertAndSendToUser(
            userId.toString(), 
            "/queue/messages", 
            directMessage
        );
    }

    /**
     * Broadcasts a system-wide announcement
     * 
     * @param title The announcement title
     * @param message The announcement message
     * @param priority The priority level (low, normal, high, urgent)
     */
    public void broadcastAnnouncement(String title, String message, String priority) {
        Map<String, Object> announcement = new HashMap<>();
        announcement.put("type", "announcement");
        announcement.put("title", title);
        announcement.put("message", message);
        announcement.put("priority", priority);
        announcement.put("timestamp", LocalDateTime.now());
        announcement.put("icon", getAnnouncementIcon(priority));

        messagingTemplate.convertAndSend("/topic/announcements", announcement);
    }

    private String getAnnouncementIcon(String priority) {
        return switch (priority.toLowerCase()) {
            case "urgent" -> "alert-triangle";
            case "high" -> "alert-circle";
            case "normal" -> "info";
            case "low" -> "message-circle";
            default -> "bell";
        };
    }
}