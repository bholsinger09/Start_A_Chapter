package com.turningpoint.chapterorganizer.service.impl;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import com.turningpoint.chapterorganizer.service.interfaces.MemberQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of MemberQueryService focused on read operations.
 * Uses read-only transactions for performance optimization.
 */
@Service
@Transactional(readOnly = true)
public class MemberQueryServiceImpl implements MemberQueryService {
    
    private final MemberRepository memberRepository;
    
    public MemberQueryServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    
    @Override
    public Optional<Member> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return memberRepository.findById(id);
    }
    
    @Override
    public Optional<Member> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return memberRepository.findByEmail(email.trim().toLowerCase());
    }
    
    @Override
    public Optional<Member> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return memberRepository.findByUsername(username.trim());
    }
    
    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
    
    @Override
    public List<Member> findActiveByChapterId(Long chapterId) {
        if (chapterId == null || chapterId <= 0) {
            return List.of();
        }
        return memberRepository.findByChapter_IdAndActiveTrue(chapterId);
    }
    
    @Override
    public List<Member> findAllByChapterId(Long chapterId) {
        if (chapterId == null || chapterId <= 0) {
            return List.of();
        }
        return memberRepository.findByChapter_Id(chapterId);
    }
    
    @Override
    public List<Member> findByChapterAndRole(Long chapterId, MemberRole role) {
        if (chapterId == null || chapterId <= 0 || role == null) {
            return List.of();
        }
        return memberRepository.findByChapter_IdAndRole(chapterId, role);
    }
    
    @Override
    public List<Member> searchByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        String cleanTerm = searchTerm.trim();
        return memberRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                cleanTerm, cleanTerm);
    }
}