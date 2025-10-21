package com.turningpoint.chapterorganizer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time features
 * Enables real-time notifications, live updates, and bidirectional communication
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry messages back to the client
        // on destinations prefixed with "/topic" and "/queue"
        config.enableSimpleBroker("/topic", "/queue");
        
        // Define the prefix for messages that are bound for methods annotated with @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // Enable user-specific messaging
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint that clients will use to connect to the server
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all origins for development
                .withSockJS(); // Enable SockJS fallback options
                
        // Additional endpoint without SockJS for modern browsers
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
    }
}