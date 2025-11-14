package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.MemberDTO;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.interfaces.MemberCommandService;
import com.turningpoint.chapterorganizer.util.ControllerUtils;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Focused controller for member command operations.
 * Handles all write operations (create, update, delete) with clear responsibilities.
 */
@RestController
@RequestMapping("/api/v2/members")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class MemberCommandController {

    private final MemberCommandService memberCommandService;

    public MemberCommandController(MemberCommandService memberCommandService) {
        this.memberCommandService = memberCommandService;
    }

    /**
     * Create a new member
     */
    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody Member member) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Member createdMember = memberCommandService.createMember(member);
            return ControllerUtils.created(MemberDTO.from(createdMember));
        });
    }

    /**
     * Create a new member in a specific chapter
     */
    @PostMapping("/chapter/{chapterId}")
    public ResponseEntity<MemberDTO> createMemberInChapter(
            @RequestBody Member member, 
            @PathVariable Long chapterId) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            // Set chapter ID before creation
            if (member.getChapter() == null) {
                member.setChapter(new com.turningpoint.chapterorganizer.entity.Chapter());
            }
            member.getChapter().setId(chapterId);
            
            Member createdMember = memberCommandService.createMember(member);
            return ControllerUtils.created(MemberDTO.from(createdMember));
        });
    }

    /**
     * Update an existing member
     */
    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Member updatedMember = memberCommandService.updateMember(id, member);
            return ControllerUtils.ok(MemberDTO.from(updatedMember));
        });
    }

    /**
     * Transfer a member to a different chapter
     */
    @PutMapping("/{id}/transfer/{newChapterId}")
    public ResponseEntity<MemberDTO> transferMember(
            @PathVariable Long id, 
            @PathVariable Long newChapterId) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Member transferredMember = memberCommandService.transferMember(id, newChapterId);
            return ControllerUtils.ok(MemberDTO.from(transferredMember));
        });
    }

    /**
     * Deactivate a member (soft delete)
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateMember(@PathVariable Long id) {
        try {
            memberCommandService.deactivateMember(id);
            return ControllerUtils.noContent();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activate a previously deactivated member
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable Long id) {
        try {
            memberCommandService.activateMember(id);
            return ControllerUtils.noContent();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Permanently delete a member
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        try {
            memberCommandService.deleteMember(id);
            return ControllerUtils.noContent();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}