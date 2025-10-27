package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Blog;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin(origins = "*")
public class BlogController {

    private final BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    // Get all published blogs
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPublishedBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Blog> blogPage = blogService.getAllPublishedBlogs(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("blogs", blogPage.getContent());
            response.put("currentPage", blogPage.getNumber());
            response.put("totalPages", blogPage.getTotalPages());
            response.put("totalElements", blogPage.getTotalElements());
            response.put("hasNext", blogPage.hasNext());
            response.put("hasPrevious", blogPage.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve blogs");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Get all published blogs (simple list)
    @GetMapping("/list")
    public ResponseEntity<List<Blog>> getAllPublishedBlogsList() {
        try {
            List<Blog> blogs = blogService.getAllPublishedBlogs();
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get blog by ID
    @GetMapping("/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        try {
            Optional<Blog> blog = blogService.getPublishedBlogById(id);
            return blog.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new blog
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBlog(@RequestBody Blog blog) {
        System.out.println("DEBUG: BlogController.createBlog called");
        System.out.println("DEBUG: Blog title: " + (blog != null ? blog.getTitle() : "null"));
        System.out.println("DEBUG: Blog author: " + (blog != null && blog.getAuthor() != null ? blog.getAuthor().getId() : "null"));
        
        try {
            Blog createdBlog = blogService.createBlog(blog);
            
            Map<String, Object> response = new HashMap<>();
            response.put("blog", createdBlog);
            response.put("message", "Blog created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in BlogController: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create blog");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Update blog
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBlog(@PathVariable Long id, @RequestBody Blog blog) {
        try {
            blog.setId(id);
            Blog updatedBlog = blogService.updateBlog(blog);
            
            Map<String, Object> response = new HashMap<>();
            response.put("blog", updatedBlog);
            response.put("message", "Blog updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update blog");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Delete blog
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBlog(@PathVariable Long id) {
        try {
            blogService.deleteBlog(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Blog deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete blog");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Publish blog
    @PutMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishBlog(@PathVariable Long id) {
        try {
            Blog publishedBlog = blogService.publishBlog(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("blog", publishedBlog);
            response.put("message", "Blog published successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to publish blog");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Unpublish blog
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<Map<String, Object>> unpublishBlog(@PathVariable Long id) {
        try {
            Blog unpublishedBlog = blogService.unpublishBlog(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("blog", unpublishedBlog);
            response.put("message", "Blog unpublished successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to unpublish blog");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // Get blogs by author
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Map<String, Object>> getBlogsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean includeUnpublished) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Blog> blogPage;
            
            if (includeUnpublished) {
                blogPage = blogService.getBlogsByAuthor(authorId, pageable);
            } else {
                // For published only, we need to create a custom implementation
                // For now, return all blogs by author
                blogPage = blogService.getBlogsByAuthor(authorId, pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("blogs", blogPage.getContent());
            response.put("currentPage", blogPage.getNumber());
            response.put("totalPages", blogPage.getTotalPages());
            response.put("totalElements", blogPage.getTotalElements());
            response.put("hasNext", blogPage.hasNext());
            response.put("hasPrevious", blogPage.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve blogs by author");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Search blogs
    @GetMapping("/search")
    public ResponseEntity<List<Blog>> searchBlogs(@RequestParam String keyword) {
        try {
            List<Blog> blogs = blogService.searchBlogs(keyword);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get recent blogs
    @GetMapping("/recent")
    public ResponseEntity<List<Blog>> getRecentBlogs(@RequestParam(defaultValue = "7") int days) {
        try {
            List<Blog> blogs = blogService.getRecentBlogs(days);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get popular blogs
    @GetMapping("/popular")
    public ResponseEntity<List<Blog>> getPopularBlogs(@RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<Blog> blogs = blogService.getPopularBlogs(pageable);
            return ResponseEntity.ok(blogs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get blog statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBlogStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPublishedBlogs", blogService.getPublishedBlogCount());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to retrieve blog statistics");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}