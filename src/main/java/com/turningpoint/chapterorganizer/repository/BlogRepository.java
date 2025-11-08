package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Find all published blogs ordered by creation date (newest first)
    List<Blog> findByPublishedTrueOrderByCreatedAtDesc();

    // Find all blogs ordered by creation date (newest first)
    List<Blog> findAllByOrderByCreatedAtDesc();

    // Find blogs by author ID ordered by creation date (newest first)
    List<Blog> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Find published blogs by author ID ordered by creation date (newest first)
    List<Blog> findByAuthorIdAndPublishedTrueOrderByCreatedAtDesc(Long authorId);

    // Search published blogs by title or content (case-insensitive)
    @Query("SELECT b FROM Blog b WHERE b.published = true AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY b.createdAt DESC")
    List<Blog> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseAndPublishedTrue(
            @Param("searchTerm") String titleSearchTerm);

    // Count published blogs
    Long countByPublishedTrue();

    // Count blogs by author
    Long countByAuthorId(Long authorId);

    // Find published blogs by author
    List<Blog> findByAuthorIdAndPublishedTrue(Long authorId);

    // Find draft blogs by author
    List<Blog> findByAuthorIdAndPublishedFalse(Long authorId);
}
