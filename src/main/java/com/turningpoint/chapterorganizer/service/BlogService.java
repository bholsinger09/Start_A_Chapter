package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Blog;
import com.turningpoint.chapterorganizer.entity.Comment;
import com.turningpoint.chapterorganizer.repository.BlogRepository;
import com.turningpoint.chapterorganizer.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository, CommentRepository commentRepository) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Get all published blogs
     */
    @Transactional(readOnly = true)
    public List<Blog> getAllPublishedBlogs() {
        return blogRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    /**
     * Get all blogs (including drafts)
     */
    @Transactional(readOnly = true)
    public List<Blog> getAllBlogs() {
        return blogRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get blog by ID
     */
    @Transactional(readOnly = true)
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    /**
     * Get blogs by author
     */
    @Transactional(readOnly = true)
    public List<Blog> getBlogsByAuthor(Long authorId) {
        return blogRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    /**
     * Get published blogs by author
     */
    @Transactional(readOnly = true)
    public List<Blog> getPublishedBlogsByAuthor(Long authorId) {
        return blogRepository.findByAuthorIdAndPublishedTrueOrderByCreatedAtDesc(authorId);
    }

    /**
     * Create new blog
     */
    public Blog createBlog(Blog blog) {
        if (blog.getAuthor() == null) {
            throw new IllegalArgumentException("Blog must have an author");
        }
        if (blog.getTitle() == null || blog.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Blog title is required");
        }
        if (blog.getContent() == null || blog.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Blog content is required");
        }
        
        // Set default published status if not provided
        if (blog.getPublished() == null) {
            blog.setPublished(false);
        }
        
        return blogRepository.save(blog);
    }

    /**
     * Update blog
     */
    public Blog updateBlog(Long id, Blog blogUpdate) {
        Blog existingBlog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + id));

        // Update fields
        if (blogUpdate.getTitle() != null && !blogUpdate.getTitle().trim().isEmpty()) {
            existingBlog.setTitle(blogUpdate.getTitle());
        }
        if (blogUpdate.getContent() != null && !blogUpdate.getContent().trim().isEmpty()) {
            existingBlog.setContent(blogUpdate.getContent());
        }
        if (blogUpdate.getPublished() != null) {
            existingBlog.setPublished(blogUpdate.getPublished());
        }

        return blogRepository.save(existingBlog);
    }

    /**
     * Publish blog
     */
    public Blog publishBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + id));
        
        blog.setPublished(true);
        return blogRepository.save(blog);
    }

    /**
     * Unpublish blog (make it a draft)
     */
    public Blog unpublishBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + id));
        
        blog.setPublished(false);
        return blogRepository.save(blog);
    }

    /**
     * Delete blog
     */
    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new IllegalArgumentException("Blog not found with id: " + id);
        }
        blogRepository.deleteById(id);
    }

    /**
     * Add comment to blog
     */
    public Comment addComment(Long blogId, Comment comment) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found with id: " + blogId));

        if (comment.getAuthor() == null) {
            throw new IllegalArgumentException("Comment must have an author");
        }
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        comment.setBlog(blog);
        Comment savedComment = commentRepository.save(comment);
        
        // Add to blog's comments collection
        blog.addComment(savedComment);
        
        return savedComment;
    }

    /**
     * Get comments by blog
     */
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBlog(Long blogId) {
        if (!blogRepository.existsById(blogId)) {
            throw new IllegalArgumentException("Blog not found with id: " + blogId);
        }
        return commentRepository.findByBlogIdOrderByCreatedAtAsc(blogId);
    }

    /**
     * Delete comment
     */
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));
        
        // Remove from blog's comments collection
        Blog blog = comment.getBlog();
        if (blog != null) {
            blog.removeComment(comment);
        }
        
        commentRepository.delete(comment);
    }

    /**
     * Search blogs by title or content
     */
    @Transactional(readOnly = true)
    public List<Blog> searchBlogs(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPublishedBlogs();
        }
        return blogRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndPublishedTrue(
                searchTerm);
    }

    /**
     * Count total blogs
     */
    @Transactional(readOnly = true)
    public Long countAllBlogs() {
        return blogRepository.count();
    }

    /**
     * Count published blogs
     */
    @Transactional(readOnly = true)
    public Long countPublishedBlogs() {
        return blogRepository.countByPublishedTrue();
    }

    /**
     * Count blogs by author
     */
    @Transactional(readOnly = true)
    public Long countBlogsByAuthor(Long authorId) {
        return blogRepository.countByAuthorId(authorId);
    }
}
