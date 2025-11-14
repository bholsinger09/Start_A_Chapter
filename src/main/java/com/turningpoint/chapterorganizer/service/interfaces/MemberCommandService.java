package com.turningpoint.chapterorganizer.service.interfaces;

import com.turningpoint.chapterorganizer.entity.Member;

/**
 * Service interface for member command operations.
 * Focused on write operations and business logic with clear separation of concerns.
 */
public interface MemberCommandService {
    
    /**
     * Create a new member
     * @param member the member to create
     * @return the created member with generated ID
     * @throws IllegalArgumentException if member data is invalid
     */
    Member createMember(Member member);
    
    /**
     * Update an existing member
     * @param id the member ID to update
     * @param member the updated member data
     * @return the updated member
     * @throws IllegalArgumentException if member data is invalid
     * @throws RuntimeException if member not found
     */
    Member updateMember(Long id, Member member);
    
    /**
     * Transfer a member to a different chapter
     * @param memberId the member ID
     * @param newChapterId the target chapter ID
     * @return the updated member
     * @throws RuntimeException if member or chapter not found
     */
    Member transferMember(Long memberId, Long newChapterId);
    
    /**
     * Deactivate a member (soft delete)
     * @param id the member ID to deactivate
     * @throws RuntimeException if member not found
     */
    void deactivateMember(Long id);
    
    /**
     * Activate a previously deactivated member
     * @param id the member ID to activate
     * @throws RuntimeException if member not found
     */
    void activateMember(Long id);
    
    /**
     * Permanently delete a member from the system
     * @param id the member ID to delete
     * @throws RuntimeException if member not found
     */
    void deleteMember(Long id);
}