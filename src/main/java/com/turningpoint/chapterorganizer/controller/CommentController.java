package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Comment;
import com.turningpoint.chapterorganizer.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final BlogService blogService;

    @Autowired
    public CommentController(BlogService blogService) {
        this.blogService = blogService;
    }

    // Get comments for a specific blog
    @GetMapping("/blog/{blogId}")
    public ResponseEntity<Map<String, Object>> getCommentsByBlog(
            @PathVariable Long blogId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> commentPage = blogService.getCommentsByBlog(blogId, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comments", commentPage.getContent());
            response.put("currentPage", commentPage.getNumber());
            response.put("totalPages", commentPage.getTotalPages());
            response.put("totalElements", commentPage.getTotalElements());
            response.put("hasNext", commentPage.hasNext());
            response.put("hasPrevious", commentPage.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve comments");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get comments for a specific blog (simple list)
    @GetMapping("/blog/{blogId}/list")
    public ResponseEntity<List<Comment>> getCommentsByBlogList(@PathVariable Long blogId) {
        try {
            List<Comment> comments = blogService.getCommentsByBlog(blogId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get comments by author
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Comment>> getCommentsByAuthor(@PathVariable Long authorId) {
        try {
            List<Comment> comments = blogService.getCommentsByAuthor(authorId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new comment
    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(@RequestBody Comment comment) {
        try {
            Comment createdComment = blogService.addComment(comment);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comment", createdComment);
            response.put("message", "Comment added successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create comment");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Create comment with explicit parameters
    @PostMapping("/blog/{blogId}")
    public ResponseEntity<Map<String, Object>> createCommentForBlog(
            @PathVariable Long blogId,
            @RequestBody Map<String, Object> requestBody) {
        try {
            String content = (String) requestBody.get("content");
            Long authorId = Long.valueOf(requestBody.get("authorId").toString());
            
            Comment createdComment = blogService.addComment(blogId, content, authorId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comment", createdComment);
            response.put("message", "Comment added successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create comment");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Update comment
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        try {
            String content = requestBody.get("content");
            Comment updatedComment = blogService.updateComment(id, content);
            
            Map<String, Object> response = new HashMap<>();
            response.put("comment", updatedComment);
            response.put("message", "Comment updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update comment");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Delete comment
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long id) {
        try {
            blogService.deleteComment(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete comment");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Get comment count for a blog
    @GetMapping("/blog/{blogId}/count")
    public ResponseEntity<Map<String, Long>> getCommentCount(@PathVariable Long blogId) {
        try {
            long count = blogService.getCommentCount(blogId);
            
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}