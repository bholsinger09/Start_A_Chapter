package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.MemberDTO;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.service.interfaces.MemberQueryService;
import com.turningpoint.chapterorganizer.util.ControllerUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Focused controller for member query operations.
 * Handles all read-only member endpoints with clear, single responsibilities.
 */
@RestController
@RequestMapping("/api/v2/members")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.GET, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class MemberQueryController {

    private final MemberQueryService memberQueryService;

    public MemberQueryController(MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    /**
     * Get all members
     */
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        return ControllerUtils.executeWithErrorHandling(() -> {
            List<Member> members = memberQueryService.findAll();
            List<MemberDTO> memberDTOs = members.stream()
                    .map(MemberDTO::from)
                    .toList();
            return ControllerUtils.ok(memberDTOs);
        });
    }

    /**
     * Get member by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Optional<Member> member = memberQueryService.findById(id);
            return member.map(m -> ControllerUtils.ok(MemberDTO.from(m)))
                         .orElse(ControllerUtils.notFound());
        });
    }

    /**
     * Get member by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<MemberDTO> getMemberByEmail(@PathVariable String email) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Optional<Member> member = memberQueryService.findByEmail(email);
            return member.map(m -> ControllerUtils.ok(MemberDTO.from(m)))
                         .orElse(ControllerUtils.notFound());
        });
    }

    /**
     * Get member by username (compatibility endpoint for blog functionality)
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<MemberDTO> getMemberByUsername(@PathVariable String username) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            // In our system, username is often treated as email for compatibility
            Optional<Member> member = memberQueryService.findByUsername(username);
            if (member.isEmpty()) {
                // Fallback: try finding by email if username lookup fails
                member = memberQueryService.findByEmail(username);
            }
            return member.map(m -> ControllerUtils.ok(MemberDTO.from(m)))
                         .orElse(ControllerUtils.notFound());
        });
    }

    /**
     * Get all members in a specific chapter
     */
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<MemberDTO>> getMembersByChapter(@PathVariable Long chapterId) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            List<Member> members = memberQueryService.findActiveByChapterId(chapterId);
            List<MemberDTO> memberDTOs = members.stream()
                    .map(MemberDTO::from)
                    .toList();
            return ControllerUtils.ok(memberDTOs);
        });
    }

    /**
     * Get all members in a chapter (including inactive)
     */
    @GetMapping("/chapter/{chapterId}/all")
    public ResponseEntity<List<MemberDTO>> getAllMembersByChapter(@PathVariable Long chapterId) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            List<Member> members = memberQueryService.findAllByChapterId(chapterId);
            List<MemberDTO> memberDTOs = members.stream()
                    .map(MemberDTO::from)
                    .toList();
            return ControllerUtils.ok(memberDTOs);
        });
    }

    /**
     * Get members by chapter and role
     */
    @GetMapping("/chapter/{chapterId}/role/{role}")
    public ResponseEntity<List<MemberDTO>> getMembersByChapterAndRole(
            @PathVariable Long chapterId, 
            @PathVariable MemberRole role) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            List<Member> members = memberQueryService.findByChapterAndRole(chapterId, role);
            List<MemberDTO> memberDTOs = members.stream()
                    .map(MemberDTO::from)
                    .toList();
            return ControllerUtils.ok(memberDTOs);
        });
    }

    /**
     * Search members by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembers(@RequestParam String term) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            List<Member> members = memberQueryService.searchByName(term);
            List<MemberDTO> memberDTOs = members.stream()
                    .map(MemberDTO::from)
                    .toList();
            return ControllerUtils.ok(memberDTOs);
        });
    }
}