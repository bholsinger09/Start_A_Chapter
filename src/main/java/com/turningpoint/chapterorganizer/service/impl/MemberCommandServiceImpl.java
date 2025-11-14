package com.turningpoint.chapterorganizer.service.impl;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.service.interfaces.MemberCommandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of MemberCommandService focused on write operations.
 * Uses transactional boundaries for data consistency.
 */
@Service
@Transactional
public class MemberCommandServiceImpl implements MemberCommandService {
    
    private final MemberRepository memberRepository;
    private final ChapterRepository chapterRepository;
    
    public MemberCommandServiceImpl(MemberRepository memberRepository, ChapterRepository chapterRepository) {
        this.memberRepository = memberRepository;
        this.chapterRepository = chapterRepository;
    }
    
    @Override
    public Member createMember(Member member) {
        validateMemberForCreation(member);
        
        // Ensure unique email and username
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Member with email " + member.getEmail() + " already exists");
        }
        if (memberRepository.findByUsername(member.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Member with username " + member.getUsername() + " already exists");
        }
        
        return memberRepository.save(member);
    }
    
    @Override
    public Member updateMember(Long id, Member updatedMember) {
        validateMemberId(id);
        validateMemberForUpdate(updatedMember);
        
        Member existingMember = findMemberById(id);
        
        // Check for email conflicts with other members
        Optional<Member> memberWithEmail = memberRepository.findByEmail(updatedMember.getEmail());
        if (memberWithEmail.isPresent() && !memberWithEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Another member with email " + updatedMember.getEmail() + " already exists");
        }
        
        // Update fields
        updateMemberFields(existingMember, updatedMember);
        
        return memberRepository.save(existingMember);
    }
    
    @Override
    public Member transferMember(Long memberId, Long newChapterId) {
        validateMemberId(memberId);
        validateChapterId(newChapterId);
        
        Member member = findMemberById(memberId);
        Chapter newChapter = findChapterById(newChapterId);
        
        member.setChapter(newChapter);
        return memberRepository.save(member);
    }
    
    @Override
    public void deactivateMember(Long id) {
        validateMemberId(id);
        Member member = findMemberById(id);
        member.setActive(false);
        memberRepository.save(member);
    }
    
    @Override
    public void activateMember(Long id) {
        validateMemberId(id);
        Member member = findMemberById(id);
        member.setActive(true);
        memberRepository.save(member);
    }
    
    @Override
    public void deleteMember(Long id) {
        validateMemberId(id);
        if (!memberRepository.existsById(id)) {
            throw new RuntimeException("Member not found with ID: " + id);
        }
        memberRepository.deleteById(id);
    }
    
    private void validateMemberForCreation(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        validateRequiredFields(member);
    }
    
    private void validateMemberForUpdate(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        validateRequiredFields(member);
    }
    
    private void validateRequiredFields(Member member) {
        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (member.getFirstName() == null || member.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (member.getLastName() == null || member.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }
    
    private void validateMemberId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid member ID: " + id);
        }
    }
    
    private void validateChapterId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid chapter ID: " + id);
        }
    }
    
    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + id));
    }
    
    private Chapter findChapterById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with ID: " + id));
    }
    
    private void updateMemberFields(Member existing, Member updated) {
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setMajor(updated.getMajor());
        existing.setGraduationYear(updated.getGraduationYear());
        existing.setRole(updated.getRole());
        if (updated.getChapter() != null) {
            existing.setChapter(updated.getChapter());
        }
    }
}