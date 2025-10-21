package com.turningpoint.chapterorganizer.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket controller for real-time features
 * Handles live notifications, updates, and bidirectional communication
 */
@Controller
@CrossOrigin(origins = "*")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle ping messages from clients for connection testing
     */
    @MessageMapping("/ping")
    @SendTo("/topic/pong")
    public Map<String, Object> handlePing(Map<String, Object> message) {
        return Map.of(
            "type", "pong",
            "timestamp", LocalDateTime.now(),
            "message", "Connection is active",
            "originalMessage", message
        );
    }

    /**
     * Handle user activity updates
     */
    @MessageMapping("/activity")
    @SendTo("/topic/activity")
    public Map<String, Object> handleActivity(Map<String, Object> activity) {
        return Map.of(
            "type", "activity_update",
            "timestamp", LocalDateTime.now(),
            "activity", activity
        );
    }

    /**
     * Send notification to all connected clients
     */
    public void sendGlobalNotification(String message, String type) {
        Map<String, Object> notification = Map.of(
            "type", "notification",
            "notificationType", type,
            "message", message,
            "timestamp", LocalDateTime.now()
        );
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    /**
     * Send notification to specific user
     */
    public void sendUserNotification(String userId, String message, String type) {
        Map<String, Object> notification = Map.of(
            "type", "notification",
            "notificationType", type,
            "message", message,
            "timestamp", LocalDateTime.now()
        );
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
    }

    /**
     * Broadcast live statistics update
     */
    public void broadcastStatsUpdate(Map<String, Object> stats) {
        Map<String, Object> update = Map.of(
            "type", "stats_update",
            "timestamp", LocalDateTime.now(),
            "stats", stats
        );
        messagingTemplate.convertAndSend("/topic/stats", update);
    }

    /**
     * Broadcast member update (new member, member edited, etc.)
     */
    public void broadcastMemberUpdate(String action, Object memberData) {
        Map<String, Object> update = Map.of(
            "type", "member_update",
            "action", action, // "created", "updated", "deleted"
            "timestamp", LocalDateTime.now(),
            "data", memberData
        );
        messagingTemplate.convertAndSend("/topic/members", update);
    }

    /**
     * Broadcast event update (new event, event edited, etc.)
     */
    public void broadcastEventUpdate(String action, Object eventData) {
        Map<String, Object> update = Map.of(
            "type", "event_update",
            "action", action, // "created", "updated", "deleted", "rsvp_changed"
            "timestamp", LocalDateTime.now(),
            "data", eventData
        );
        messagingTemplate.convertAndSend("/topic/events", update);
    }
}