package com.turningpoint.chapterorganizer.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Controller to serve static files for Vue.js SPA
 */
@RestController
public class StaticController {

    /**
     * Serves the index.html file for SPA routing
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> index() throws IOException {
        Resource resource = new ClassPathResource("static/index.html");
        if (resource.exists()) {
            String content = Files.readString(resource.getFile().toPath());
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(content);
        }
        return ResponseEntity.notFound().build();
    }
}