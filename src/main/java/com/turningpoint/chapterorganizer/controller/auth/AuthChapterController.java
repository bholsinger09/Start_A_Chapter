package com.turningpoint.chapterorganizer.controller.auth;

import com.turningpoint.chapterorganizer.service.ChapterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Focused controller for authentication-related chapter operations.
 * Single Responsibility: Handle chapter lookups during auth flows.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.GET, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class AuthChapterController {

    private final ChapterService chapterService;

    public AuthChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    /**
     * Get all available chapters for registration/login forms.
     * Single purpose: Provide chapter options for auth UI.
     */
    @GetMapping("/chapters")
    public ResponseEntity<?> getChaptersForAuth() {
        try {
            List<?> chapters = chapterService.getAllChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to load chapters");
        }
    }
}