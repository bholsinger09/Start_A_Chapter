package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Blog;
import com.turningpoint.chapterorganizer.entity.Comment;
import com.turningpoint.chapterorganizer.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for a specific blog ordered by creation date (oldest first)
    List<Comment> findByBlogOrderByCreatedAtAsc(Blog blog);
    
    // Find all comments for a specific blog with pagination
    Page<Comment> findByBlogOrderByCreatedAtAsc(Blog blog, Pageable pageable);
    
    // Find comments by blog id ordered by creation date (oldest first)
    List<Comment> findByBlogIdOrderByCreatedAtAsc(Long blogId);
    
    // Find comments by blog id with pagination
    Page<Comment> findByBlogIdOrderByCreatedAtAsc(Long blogId, Pageable pageable);
    
    // Find all comments by author
    List<Comment> findByAuthorOrderByCreatedAtDesc(Member author);
    
    // Find all comments by author with pagination
    Page<Comment> findByAuthorOrderByCreatedAtDesc(Member author, Pageable pageable);
    
    // Count comments for a specific blog
    long countByBlog(Blog blog);
    
    // Count comments for a blog by id
    long countByBlogId(Long blogId);
    
    // Count comments by author
    long countByAuthor(Member author);
    
    // Find recent comments for a blog (within specified time)
    @Query("SELECT c FROM Comment c WHERE c.blog = :blog AND c.createdAt >= :since ORDER BY c.createdAt ASC")
    List<Comment> findRecentCommentsByBlog(@Param("blog") Blog blog, @Param("since") LocalDateTime since);
    
    // Find recent comments by author (within specified time)
    @Query("SELECT c FROM Comment c WHERE c.author = :author AND c.createdAt >= :since ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByAuthor(@Param("author") Member author, @Param("since") LocalDateTime since);
    
    // Find all recent comments (within specified time)
    @Query("SELECT c FROM Comment c WHERE c.createdAt >= :since ORDER BY c.createdAt DESC")
    List<Comment> findRecentComments(@Param("since") LocalDateTime since);
    
    // Delete all comments for a specific blog
    void deleteByBlog(Blog blog);
    
    // Delete all comments by author
    void deleteByAuthor(Member author);
    
    // Check if comment exists for blog and author
    boolean existsByBlogAndAuthor(Blog blog, Member author);
}