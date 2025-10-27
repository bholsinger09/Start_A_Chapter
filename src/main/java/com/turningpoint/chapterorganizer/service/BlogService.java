package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Blog;
import com.turningpoint.chapterorganizer.entity.Comment;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.repository.BlogRepository;
import com.turningpoint.chapterorganizer.repository.CommentRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository, CommentRepository commentRepository, MemberRepository memberRepository) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
    }

    // Blog CRUD operations
    public Blog createBlog(String title, String content, Long authorId, boolean published) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        
        Blog blog = new Blog(title, content, author, published);
        return blogRepository.save(blog);
    }

    public Blog createBlog(Blog blog) {
        System.out.println("DEBUG: createBlog method called");
        
        if (blog.getAuthor() == null || blog.getAuthor().getId() == null) {
            System.out.println("DEBUG: Blog author is null or ID is null");
            throw new RuntimeException("Blog must have an author");
        }
        
        Long authorId = blog.getAuthor().getId();
        System.out.println("DEBUG: Looking for member with ID: " + authorId);
        
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> {
                    System.err.println("ERROR: Member not found with id: " + authorId);
                    // Let's also check if any members exist
                    long totalMembers = memberRepository.count();
                    System.err.println("ERROR: Total members in database: " + totalMembers);
                    return new RuntimeException("Member not found with id: " + authorId);
                });
        
        System.out.println("DEBUG: Found member: " + author.getFirstName() + " " + author.getLastName());
        blog.setAuthor(author);
        Blog savedBlog = blogRepository.save(blog);
        System.out.println("DEBUG: Blog saved with ID: " + savedBlog.getId());
        return savedBlog;
    }

    @Transactional(readOnly = true)
    public Optional<Blog> getBlogById(Long id) {
        return blogRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Blog> getPublishedBlogById(Long id) {
        return blogRepository.findByIdAndPublishedTrue(id);
    }

    @Transactional(readOnly = true)
    public List<Blog> getAllPublishedBlogs() {
        return blogRepository.findByPublishedTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Page<Blog> getAllPublishedBlogs(Pageable pageable) {
        return blogRepository.findByPublishedTrueOrderByCreatedAtDesc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Blog> getBlogsByAuthor(Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        return blogRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    @Transactional(readOnly = true)
    public List<Blog> getPublishedBlogsByAuthor(Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        return blogRepository.findByAuthorAndPublishedTrueOrderByCreatedAtDesc(author);
    }

    @Transactional(readOnly = true)
    public Page<Blog> getBlogsByAuthor(Long authorId, Pageable pageable) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        return blogRepository.findByAuthorOrderByCreatedAtDesc(author, pageable);
    }

    public Blog updateBlog(Long id, String title, String content, Boolean published) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        
        if (title != null) blog.setTitle(title);
        if (content != null) blog.setContent(content);
        if (published != null) blog.setPublished(published);
        
        return blogRepository.save(blog);
    }

    public Blog updateBlog(Blog blog) {
        if (blog.getId() == null) {
            throw new RuntimeException("Blog ID is required for update");
        }
        
        Blog existingBlog = blogRepository.findById(blog.getId())
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + blog.getId()));
        
        // Update fields
        existingBlog.setTitle(blog.getTitle());
        existingBlog.setContent(blog.getContent());
        existingBlog.setPublished(blog.getPublished());
        
        return blogRepository.save(existingBlog);
    }

    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        
        // Delete associated comments first
        commentRepository.deleteByBlog(blog);
        
        // Delete the blog
        blogRepository.delete(blog);
    }

    public Blog publishBlog(Long id) {
        return updateBlog(id, null, null, true);
    }

    public Blog unpublishBlog(Long id) {
        return updateBlog(id, null, null, false);
    }

    // Comment operations
    public Comment addComment(Long blogId, String content, Long authorId) {
        Blog blog = blogRepository.findByIdAndPublishedTrue(blogId)
                .orElseThrow(() -> new RuntimeException("Published blog not found with id: " + blogId));
        
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        
        Comment comment = new Comment(content, author, blog);
        return commentRepository.save(comment);
    }

    public Comment addComment(Comment comment) {
        if (comment.getBlog() == null || comment.getBlog().getId() == null) {
            throw new RuntimeException("Comment must be associated with a blog");
        }
        
        if (comment.getAuthor() == null || comment.getAuthor().getId() == null) {
            throw new RuntimeException("Comment must have an author");
        }
        
        Blog blog = blogRepository.findByIdAndPublishedTrue(comment.getBlog().getId())
                .orElseThrow(() -> new RuntimeException("Published blog not found with id: " + comment.getBlog().getId()));
        
        Member author = memberRepository.findById(comment.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + comment.getAuthor().getId()));
        
        comment.setBlog(blog);
        comment.setAuthor(author);
        
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByBlog(Long blogId) {
        return commentRepository.findByBlogIdOrderByCreatedAtAsc(blogId);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByBlog(Long blogId, Pageable pageable) {
        return commentRepository.findByBlogIdOrderByCreatedAtAsc(blogId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByAuthor(Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        return commentRepository.findByAuthorOrderByCreatedAtDesc(author);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        commentRepository.delete(comment);
    }

    // Search operations
    @Transactional(readOnly = true)
    public List<Blog> searchBlogs(String keyword) {
        return blogRepository.findByTitleOrContentContainingIgnoreCase(keyword);
    }

    @Transactional(readOnly = true)
    public List<Blog> searchBlogsByTitle(String keyword) {
        return blogRepository.findByTitleContainingIgnoreCaseAndPublishedTrueOrderByCreatedAtDesc(keyword);
    }

    @Transactional(readOnly = true)
    public List<Blog> searchBlogsByContent(String keyword) {
        return blogRepository.findByContentContainingIgnoreCase(keyword);
    }

    // Statistics operations
    @Transactional(readOnly = true)
    public long getPublishedBlogCount() {
        return blogRepository.countByPublishedTrue();
    }

    @Transactional(readOnly = true)
    public long getBlogCountByAuthor(Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        return blogRepository.countByAuthor(author);
    }

    @Transactional(readOnly = true)
    public long getPublishedBlogCountByAuthor(Long authorId) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + authorId));
        return blogRepository.countByAuthorAndPublishedTrue(author);
    }

    @Transactional(readOnly = true)
    public long getCommentCount(Long blogId) {
        return commentRepository.countByBlogId(blogId);
    }

    @Transactional(readOnly = true)
    public List<Blog> getRecentBlogs(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return blogRepository.findRecentPublishedBlogs(since);
    }

    @Transactional(readOnly = true)
    public List<Blog> getPopularBlogs(Pageable pageable) {
        return blogRepository.findPopularPublishedBlogs(pageable);
    }

    // Helper methods
    @Transactional(readOnly = true)
    public boolean canUserEditBlog(Long blogId, Long userId) {
        Optional<Blog> blogOpt = blogRepository.findById(blogId);
        if (blogOpt.isEmpty()) {
            return false;
        }
        
        Blog blog = blogOpt.get();
        return blog.getAuthor().getId().equals(userId);
    }

    @Transactional(readOnly = true)
    public boolean canUserDeleteComment(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isEmpty()) {
            return false;
        }
        
        Comment comment = commentOpt.get();
        // User can delete their own comments or comments on their own blogs
        return comment.getAuthor().getId().equals(userId) || 
               comment.getBlog().getAuthor().getId().equals(userId);
    }
}