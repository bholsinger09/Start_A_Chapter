package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Blog;
import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Find all published blogs ordered by creation date (newest first)
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();
    
    // Find all published blogs with pagination
    Page<Blog> findByPublishedTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Find all blogs by author
    List<Blog> findByAuthorOrderByCreatedAtDesc(Member author);
    
    // Find published blogs by author
    List<Blog> findByAuthorAndPublishedTrueOrderByCreatedAtDesc(Member author);
    
    // Find blogs by author with pagination
    Page<Blog> findByAuthorOrderByCreatedAtDesc(Member author, Pageable pageable);
    
    // Find published blogs by author with pagination
    Page<Blog> findByAuthorAndPublishedTrueOrderByCreatedAtDesc(Member author, Pageable pageable);
    
    // Find blogs by title containing keyword (case insensitive)
    List<Blog> findByTitleContainingIgnoreCaseAndPublishedTrueOrderByCreatedAtDesc(String keyword);
    
    // Find blogs by content containing keyword (case insensitive)
    @Query("SELECT b FROM Blog b WHERE LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) AND b.published = true ORDER BY b.createdAt DESC")
    List<Blog> findByContentContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Find blogs by title or content containing keyword (case insensitive)
    @Query("SELECT b FROM Blog b WHERE (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND b.published = true ORDER BY b.createdAt DESC")
    List<Blog> findByTitleOrContentContainingIgnoreCase(@Param("keyword") String keyword);
    
    // Count published blogs
    long countByPublishedTrue();
    
    // Count blogs by author
    long countByAuthor(Member author);
    
    // Count published blogs by author
    long countByAuthorAndPublishedTrue(Member author);
    
    // Find recent blogs (published within specified time)
    @Query("SELECT b FROM Blog b WHERE b.published = true AND b.createdAt >= :since ORDER BY b.createdAt DESC")
    List<Blog> findRecentPublishedBlogs(@Param("since") LocalDateTime since);
    
    // Find popular blogs (with most comments)
    @Query("SELECT b FROM Blog b WHERE b.published = true ORDER BY SIZE(b.comments) DESC, b.createdAt DESC")
    List<Blog> findPopularPublishedBlogs(Pageable pageable);
    
    // Check if blog exists by id and is published
    boolean existsByIdAndPublishedTrue(Long id);
    
    // Find published blog by id
    Optional<Blog> findByIdAndPublishedTrue(Long id);
    
    // Analytics methods for dashboard
    long countByCreatedAtAfter(LocalDateTime date);
    
    List<Blog> findTop5ByOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(b) FROM Blog b WHERE b.author.chapter = :chapter AND b.createdAt > :date")
    long countByAuthorChapterAndCreatedAtAfter(@Param("chapter") Chapter chapter, @Param("date") LocalDateTime date);
    
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}