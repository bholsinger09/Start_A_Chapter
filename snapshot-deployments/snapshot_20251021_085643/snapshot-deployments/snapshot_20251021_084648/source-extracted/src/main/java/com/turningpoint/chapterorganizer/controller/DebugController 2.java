package com.turningpoint.chapterorganizer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {
    
    @GetMapping("/debug")
    public String debug() {
        return "Debug endpoint working";
    }
    
    @GetMapping("/api/debug")
    public String apiDebug() {
        return "API Debug endpoint working";
    }
}