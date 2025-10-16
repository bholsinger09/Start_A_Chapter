package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChapterService chapterService;

    @Autowired
    public MemberService(MemberRepository memberRepository, ChapterService chapterService) {
        this.memberRepository = memberRepository;
        this.chapterService = chapterService;
    }

    /**
     * Create a new member
     */
    public Member createMember(Member member) {
        // Check if email already exists
        if (memberRepository.existsByEmail(member.getEmail())) {
            throw new IllegalArgumentException("Member with this email already exists");
        }

                // Validate chapter exists if provided (chapter is now optional)
        if (member.getChapter() != null) {
            Optional<Chapter> chapter = chapterService.getChapterById(member.getChapter().getId());
            if (chapter.isEmpty()) {
                throw new IllegalArgumentException("Chapter not found with id: " + member.getChapter().getId());
            }
            member.setChapter(chapter.get());
        }
        // Chapter is optional - members can be created without a chapter and join one later

        // Set default values
        if (member.getActive() == null) {
            member.setActive(true);
        }
        if (member.getRole() == null) {
            member.setRole(MemberRole.MEMBER);
        }

        return memberRepository.save(member);
    }

    /**
     * Get all members across all chapters
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Get member by ID
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * Get member by email
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Get member by username
     */
    @Transactional(readOnly = true)
    public Optional<Member> getMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    /**
     * Check if member exists by username
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return memberRepository.existsByUsername(username);
    }

    /**
     * Check if member exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * Get all active members by chapter
     */
    @Transactional(readOnly = true)
    public List<Member> getMembersByChapter(Long chapterId) {
        return memberRepository.findByChapterIdAndActiveTrue(chapterId);
    }

    /**
     * Get all members by chapter (including inactive)
     */
    @Transactional(readOnly = true)
    public List<Member> getAllMembersByChapter(Long chapterId) {
        return memberRepository.findByChapterId(chapterId);
    }

    /**
     * Get members by role in a chapter
     */
    @Transactional(readOnly = true)
    public List<Member> getMembersByRole(Long chapterId, MemberRole role) {
        return memberRepository.findByChapterIdAndRoleAndActiveTrue(chapterId, role);
    }

    /**
     * Get chapter officers (President, VP, Secretary, Treasurer, Officer)
     */
    @Transactional(readOnly = true)
    public List<Member> getChapterOfficers(Long chapterId) {
        return memberRepository.findChapterOfficers(chapterId);
    }

    /**
     * Get the chapter president
     */
    @Transactional(readOnly = true)
    public Optional<Member> getChapterPresident(Long chapterId) {
        return memberRepository.findChapterPresident(chapterId);
    }

    /**
     * Update member information
     */
    public Member updateMember(Long id, Member updatedMember) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));

        // Check if email is changing and if new email already exists
        if (!existingMember.getEmail().equals(updatedMember.getEmail())) {
            if (memberRepository.existsByEmailAndIdNot(updatedMember.getEmail(), id)) {
                throw new IllegalArgumentException("Member with this email already exists");
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
        return memberRepository.findMembersByCriteria(chapterId, firstName, lastName, email,
                role, major, graduationYear, active);
    }

    /**
     * Search members by name
     */
    @Transactional(readOnly = true)
    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    /**
     * Count active members by chapter
     */
    @Transactional(readOnly = true)
    public Long countActiveMembersByChapter(Long chapterId) {
        return memberRepository.countActiveMembersByChapter(chapterId);
    }

    /**
     * Count members by role in chapter
     */
    @Transactional(readOnly = true)
    public Long countMembersByRoleInChapter(Long chapterId, MemberRole role) {
        return memberRepository.countMembersByRoleInChapter(chapterId, role);
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
     * Get paginated members with sorting
     */
    @Transactional(readOnly = true)
    public Page<Member> getPaginatedMembers(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }
}