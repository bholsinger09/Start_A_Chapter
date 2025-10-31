package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/chapters")
    public ResponseEntity<List<Chapter>> getAllChapters() {
        try {
            List<Chapter> chapters = chapterService.getAllActiveChapters();
            return ResponseEntity.ok(chapters);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/chapters/{id}")
    public ResponseEntity<Chapter> getChapterById(@PathVariable Long id) {
        try {
            Optional<Chapter> chapter = chapterService.getChapterById(id);
            return chapter.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/chapters")
    public ResponseEntity<Chapter> createChapter(@RequestBody Chapter chapter) {
        try {
            Chapter createdChapter = chapterService.createChapter(chapter);
            return ResponseEntity.ok(createdChapter);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chapter> updateChapter(@PathVariable Long id, @RequestBody Chapter chapter) {
        try {
            Chapter updatedChapter = chapterService.updateChapter(id, chapter);
            return ResponseEntity.ok(updatedChapter);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
