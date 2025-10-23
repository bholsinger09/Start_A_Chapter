package com.turningpoint.chapterorganizer.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
public class RootController {

    @GetMapping("/")
    public String index() {
        return "<!DOCTYPE html><html><head><title>Campus Chapter Organizer</title></head><body><h1>Loading...</h1><script>window.location.href='/index.html';</script></body></html>";
    }

    @GetMapping("/api/info")
    @ResponseBody
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Campus Chapter Organizer API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "chapters", "/api/chapters",
            "members", "/api/members", 
            "events", "/api/events",
            "health", "/actuator/health"
        ));
        return response;
    }
}