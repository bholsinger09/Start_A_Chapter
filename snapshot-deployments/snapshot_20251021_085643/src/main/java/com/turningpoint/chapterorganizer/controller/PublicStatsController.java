package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.service.ChapterService;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import com.turningpoint.chapterorganizer.repository.EventRSVPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats/public")
@CrossOrigin(origins = "*")
public class PublicStatsController {

    private final ChapterService chapterService;
    private final MemberService memberService;
    private final EventRepository eventRepository;
    private final EventRSVPRepository eventRsvpRepository;

    @Autowired
    public PublicStatsController(ChapterService chapterService, 
                                MemberService memberService,
                                EventRepository eventRepository,
                                EventRSVPRepository eventRsvpRepository) {
        this.chapterService = chapterService;
        this.memberService = memberService;
        this.eventRepository = eventRepository;
        this.eventRsvpRepository = eventRsvpRepository;
    }

    /**
     * Get public statistics for the home screen
     * This endpoint provides basic statistics without requiring authentication
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getPublicOverview() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get basic counts - safe to expose publicly
            stats.put("totalChapters", chapterService.getAllActiveChapters().size());
            stats.put("totalMembers", memberService.getAllMembers().size());
            stats.put("totalEvents", eventRepository.count());
            stats.put("totalRsvps", eventRsvpRepository.count());
            
            // Add some additional public-friendly information
            stats.put("description", "Campus Chapter Organizer - Connecting students across universities");
            stats.put("version", "1.0.0");
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Return default values if there's an error
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalChapters", 0);
            defaultStats.put("totalMembers", 0);
            defaultStats.put("totalEvents", 0);
            defaultStats.put("totalRsvps", 0);
            defaultStats.put("description", "Campus Chapter Organizer");
            defaultStats.put("version", "1.0.0");
            defaultStats.put("error", "Unable to fetch current statistics");
            
            return ResponseEntity.ok(defaultStats);
        }
    }

    /**
     * Get basic chapter information for public display
     */
    @GetMapping("/chapters-summary")
    public ResponseEntity<Map<String, Object>> getChaptersSummary() {
        try {
            Map<String, Object> summary = new HashMap<>();
            
            // Get chapters by state for public display
            var chapters = chapterService.getAllActiveChapters();
            Map<String, Integer> chaptersByState = new HashMap<>();
            
            chapters.forEach(chapter -> {
                String state = chapter.getState() != null ? chapter.getState() : "Unknown";
                chaptersByState.put(state, chaptersByState.getOrDefault(state, 0) + 1);
            });
            
            summary.put("chaptersByState", chaptersByState);
            summary.put("totalStates", chaptersByState.size());
            summary.put("averageChaptersPerState", 
                chaptersByState.isEmpty() ? 0 : chapters.size() / (double) chaptersByState.size());
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unable to fetch chapter summary");
            return ResponseEntity.ok(errorResponse);
        }
    }
}