package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.service.constants.MemberServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChapterService chapterService;

    @Autowired
    public MemberService(MemberRepository memberRepository, ChapterService chapterService) {
        this.memberRepository = memberRepository;
        this.chapterService = chapterService;
    }

    /**
     * Create a new member - REFACTORED using Successive Refinement
     * 
     * BEFORE: 50+ line method with multiple responsibilities
     * AFTER: Clean orchestrator calling focused helper methods
     */
    public Member createMember(Member member) {
        try {
            // Step 1: Validate and set chapter (extracted method)
            validateAndSetChapter(member);
            
            // Step 2: Set default values (extracted method) 
            setMemberDefaults(member);
            
            // Step 3: Persist member (database handles constraints)
            return memberRepository.save(member);
            
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Step 4: Handle constraint violations (extracted method)
            throw handleConstraintViolation(e);
        } catch (Exception e) {
            // Re-throw any other exception
            throw e;
        }
    }
    
    /**
     * EXTRACTED METHOD 1: Validate and set chapter
     * Single responsibility: Chapter validation and assignment
     */
    private void validateAndSetChapter(Member member) {
        if (member.hasChapter() && member.getChapterId() != null) {
            Long chapterId = member.getChapterId();
            Optional<Chapter> chapter = chapterService.getChapterById(chapterId);
            if (chapter.isEmpty()) {
                throw new IllegalArgumentException(MemberServiceConstants.formatErrorMessage(
                    MemberServiceConstants.CHAPTER_NOT_FOUND_ERROR, chapterId));
            }
            member.setChapter(chapter.get());
        }
        // Note: Chapter is optional - member can be created without a chapter
    }
    
    /**
     * EXTRACTED METHOD 2: Set member defaults - Boy Scout Rule: Use constants for defaults
     * Single responsibility: Ensure proper default values
     */
    private void setMemberDefaults(Member member) {
        if (member.getActive() == null) {
            member.setActive(MemberServiceConstants.DEFAULT_MEMBER_ACTIVE_STATUS);
        }
        if (member.getRole() == null) {
            member.setRole(MemberServiceConstants.DEFAULT_MEMBER_ROLE);
        }
    }
    
    /**
     * EXTRACTED METHOD 3: Handle constraint violations 
     * Single responsibility: Convert database exceptions to meaningful messages
     */
    /**
     * EXTRACTED METHOD 3: Handle constraint violations - Boy Scout Rule: Use constants for error messages
     * Single responsibility: Convert database exceptions to meaningful messages
     */
    private IllegalArgumentException handleConstraintViolation(org.springframework.dao.DataIntegrityViolationException e) {
        String message = e.getMessage();
        if (message != null) {
            if (message.contains(MemberServiceConstants.EMAIL_CONSTRAINT_KEY) || 
                message.contains(MemberServiceConstants.EMAIL_CONSTRAINT_KEYWORD)) {
                return new IllegalArgumentException(MemberServiceConstants.EMAIL_ALREADY_EXISTS_ERROR);
            } else if (message.contains(MemberServiceConstants.USERNAME_CONSTRAINT_KEY) || 
                      message.contains(MemberServiceConstants.USERNAME_CONSTRAINT_KEYWORD)) {
                return new IllegalArgumentException(MemberServiceConstants.USERNAME_ALREADY_EXISTS_ERROR);
            } else if (message.contains(MemberServiceConstants.UNIQUE_CONSTRAINT_KEYWORD) || 
                      message.contains(MemberServiceConstants.DUPLICATE_CONSTRAINT_KEYWORD)) {
                return new IllegalArgumentException(MemberServiceConstants.MEMBER_ALREADY_EXISTS_ERROR);
            }
        }
        return new IllegalArgumentException(MemberServiceConstants.formatErrorMessage(
            MemberServiceConstants.DATA_CONFLICT_ERROR, e.getMessage()));
    }

    /**
     * Get member by ID
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Get member by email - Boy Scout Rule: Clear method naming
     */
    @Transactional(readOnly = true)
    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    /**
     * Get member by username - Boy Scout Rule: Consistent naming pattern
     */
    @Transactional(readOnly = true)
    public Optional<Member> findMemberByUsername(String username) {
        return memberRepository.findMemberByUsername(username);
    }

    /**
     * Get all active members by chapter - Boy Scout Rule: Descriptive method name
     */
    @Transactional(readOnly = true)
    public List<Member> findActiveMembersByChapter(Long chapterId) {
        return memberRepository.findActiveMembersByChapterId(chapterId);
    }

    /**
     * Get all members by chapter (including inactive) - Boy Scout Rule: Clear intent
     */
    @Transactional(readOnly = true)
    public List<Member> findAllMembersByChapter(Long chapterId) {
        return memberRepository.findAllMembersByChapterId(chapterId);
    }

    /**
     * Get all members across all chapters
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    
    // ============= BACKWARD COMPATIBILITY METHODS - Boy Scout Rule: Maintain existing API =============
    // These methods maintain backward compatibility while we gradually migrate to better names
    
    /**
     * @deprecated Use findMemberByEmail(String email) instead
     */
    @Deprecated(since = "1.0.0")
    @Transactional(readOnly = true)
    public Optional<Member> getMemberByEmail(String email) {
        return findMemberByEmail(email);
    }
    
    /**
     * @deprecated Use findMemberByUsername(String username) instead
     */
    @Deprecated(since = "1.0.0") 
    @Transactional(readOnly = true)
    public Optional<Member> getMemberByUsername(String username) {
        return findMemberByUsername(username);
    }
    
    /**
     * @deprecated Use findActiveMembersByChapter(Long chapterId) instead
     */
    @Deprecated(since = "1.0.0")
    @Transactional(readOnly = true)
    public List<Member> getMembersByChapter(Long chapterId) {
        return findActiveMembersByChapter(chapterId);
    }

    /**
     * Get active members by role in a chapter - Boy Scout Rule: Clear business intent
     */
    @Transactional(readOnly = true)
    public List<Member> findActiveMembersByChapterAndRole(Long chapterId, MemberRole role) {
        return memberRepository.findActiveMembersByChapterIdAndRole(chapterId, role);
    }

    /**
     * Get active chapter officers - Boy Scout Rule: Clear active status 
     */
    @Transactional(readOnly = true)
    public List<Member> findActiveChapterOfficers(Long chapterId) {
        return memberRepository.findActiveChapterOfficers(chapterId);
    }

    /**
     * Get active chapter president - Boy Scout Rule: Clear active status
     */
    @Transactional(readOnly = true)
    public Optional<Member> findActiveChapterPresident(Long chapterId) {
        return memberRepository.findActiveChapterPresident(chapterId);
    }

    /**
     * Update member information
     */
    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(MemberServiceConstants.formatErrorMessage(
                    MemberServiceConstants.MEMBER_NOT_FOUND_ERROR, id)));

        // Check if email is changing and if new email already exists - Boy Scout Rule: Use constants
        if (!existingMember.getEmail().equals(updatedMember.getEmail())) {
            if (memberRepository.existsMemberByEmailAndIdNot(updatedMember.getEmail(), id)) {
                throw new IllegalArgumentException(MemberServiceConstants.EMAIL_ALREADY_EXISTS_ERROR);
            }
        }

        // Update fields
        existingMember.setFirstName(updatedMember.getFirstName());
        existingMember.setLastName(updatedMember.getLastName());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setPhoneNumber(updatedMember.getPhoneNumber());
        existingMember.setMajor(updatedMember.getMajor());
        existingMember.setGraduationYear(updatedMember.getGraduationYear());

        if (updatedMember.getActive() != null) {
            existingMember.setActive(updatedMember.getActive());
        }

        return memberRepository.save(existingMember);
    }

    /**
     * Update member role
     */
    public Member updateMemberRole(Long id, MemberRole newRole) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        member.setRole(newRole);
        return memberRepository.save(member);
    }

    /**
     * Deactivate member (soft delete)
     */
    public void deactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        member.setActive(false);
        memberRepository.save(member);
    }

    /**
     * Reactivate member
     */
    public Member reactivateMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        member.setActive(true);
        return memberRepository.save(member);
    }

    /**
     * Permanently delete member (use with caution)
     */
    public void permanentlyDeleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    /**
     * Search members by criteria
     */
    @Transactional(readOnly = true)
    public List<Member> searchMembers(Long chapterId, String firstName, String lastName,
            String email, MemberRole role, String major,
            String graduationYear, Boolean active) {
        return memberRepository.searchMembersByMultipleCriteria(chapterId, firstName, lastName, email,
                role, major, graduationYear, active);
    }

    /**
     * Search members by name
     */
    @Transactional(readOnly = true)
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findMembersByPartialNameMatch(name, name);
    }

    /**
     * Count active members by chapter
     */
    @Transactional(readOnly = true)
    public Long countActiveMembersByChapter(Long chapterId) {
        return memberRepository.countActiveMembersByChapterId(chapterId);
    }

    /**
     * Count members by role in chapter
     */
    @Transactional(readOnly = true)
    public Long countMembersByRoleInChapter(Long chapterId, MemberRole role) {
        return memberRepository.countActiveMembersByChapterIdAndRole(chapterId, role);
    }

    /**
     * Transfer member to another chapter
     */
    public Member transferMemberToChapter(Long memberId, Long newChapterId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        Chapter newChapter = chapterService.getChapterById(newChapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + newChapterId));

        member.setChapter(newChapter);
        return memberRepository.save(member);
    }

    /**
     * Add member to a specific chapter
     */
    public Member addMemberToChapter(Long chapterId, Member member) {
        // Check if email already exists
        if (memberRepository.existsMemberByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Member with this email already exists");
        }

        // Get the chapter
        Chapter chapter = chapterService.getChapterById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found with id: " + chapterId));

        // Set chapter and defaults
        member.setChapter(chapter);
        if (member.getActive() == null) {
            member.setActive(true);
        }
        if (member.getRole() == null) {
            member.setRole(MemberRole.MEMBER);
        }

        return memberRepository.save(member);
    }

    /**
     * Delete member (hard delete)
     */
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }
}