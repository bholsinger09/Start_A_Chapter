package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by blog ID ordered by creation date (oldest first)
    List<Comment> findByBlogIdOrderByCreatedAtAsc(Long blogId);

    // Find comments by author ID
    List<Comment> findByAuthorId(Long authorId);

    // Count comments by blog ID
    Long countByBlogId(Long blogId);

    // Count comments by author ID
    Long countByAuthorId(Long authorId);
}
