package com.turningpoint.chapterorganizer.service.interfaces;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for member query operations.
 * Focused on read-only member data retrieval with clear method contracts.
 */
public interface MemberQueryService {
    
    /**
     * Find a member by their unique ID
     * @param id the member ID
     * @return Optional containing the member if found
     */
    Optional<Member> findById(Long id);
    
    /**
     * Find a member by their email address
     * @param email the member's email
     * @return Optional containing the member if found
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find a member by their username
     * @param username the member's username
     * @return Optional containing the member if found
     */
    Optional<Member> findByUsername(String username);
    
    /**
     * Get all members in the system
     * @return List of all members
     */
    List<Member> findAll();
    
    /**
     * Find all active members in a specific chapter
     * @param chapterId the chapter ID
     * @return List of active members in the chapter
     */
    List<Member> findActiveByChapterId(Long chapterId);
    
    /**
     * Find all members in a specific chapter (including inactive)
     * @param chapterId the chapter ID
     * @return List of all members in the chapter
     */
    List<Member> findAllByChapterId(Long chapterId);
    
    /**
     * Find members by role and chapter
     * @param chapterId the chapter ID
     * @param role the member role
     * @return List of members with the specified role in the chapter
     */
    List<Member> findByChapterAndRole(Long chapterId, MemberRole role);
    
    /**
     * Search members by name (first or last name)
     * @param searchTerm the search term
     * @return List of members matching the search criteria
     */
    List<Member> searchByName(String searchTerm);
}