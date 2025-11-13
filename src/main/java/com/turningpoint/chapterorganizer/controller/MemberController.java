package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.dto.MemberDTO;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.MemberService;
import com.turningpoint.chapterorganizer.util.ControllerUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(
    origins = {"https://startachapter.duckdns.org", "http://startachapter.duckdns.org", "*"}, 
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * Get member by username (treating username as email for compatibility)
     * This endpoint is needed for blog functionality
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<MemberDTO> getMemberByUsername(@PathVariable String username) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            // In our system, username is treated as email
            Optional<Member> member = memberService.getMemberByEmail(username);
            
            return member.isPresent() 
                ? ControllerUtils.ok(convertToDTO(member.get()))
                : ControllerUtils.notFound();
        });
    }

    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        try {
            List<Member> members = memberService.getAllMembers();
            return ResponseEntity.ok(convertToMemberDTOList(members));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Helper method to convert Member entity to immutable DTO
     * Uses factory method to create thread-safe immutable object
     */
    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.from(member);
    }

    /**
     * Helper method to convert list of Members to list of DTOs
     * Extracts repetitive stream operations following DRY principle
     */
    private List<MemberDTO> convertToMemberDTOList(List<Member> members) {
        return members.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Helper method to handle member creation with proper error responses
     * Extracts repetitive exception handling for creation operations
     */
    private ResponseEntity<MemberDTO> handleMemberCreation(Member member) {
        try {
            Member createdMember = memberService.createMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to handle member updates with proper error responses
     * Extracts repetitive exception handling for update operations
     */
    private ResponseEntity<MemberDTO> handleMemberUpdate(Long id, Member member) {
        try {
            Member updatedMember = memberService.updateMember(id, member);
            return ResponseEntity.ok(convertToDTO(updatedMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to handle chapter-specific member operations
     * Extracts repetitive exception handling for chapter operations
     */
    private ResponseEntity<MemberDTO> handleChapterMemberOperation(Long chapterId, Member member) {
        try {
            Member createdMember = memberService.addMemberToChapter(chapterId, member);
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(createdMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to handle member transfer operations
     * Extracts repetitive exception handling for transfer operations
     */
    private ResponseEntity<MemberDTO> handleMemberTransfer(Long memberId, Long newChapterId) {
        try {
            Member transferredMember = memberService.transferMemberToChapter(memberId, newChapterId);
            return ResponseEntity.ok(convertToDTO(transferredMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Helper method to handle member deletion operations
     * Extracts repetitive exception handling for delete operations
     */
    private ResponseEntity<Void> handleMemberDeletion(Long memberId) {
        try {
            memberService.deleteMember(memberId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable Long id) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Optional<Member> member = memberService.getMemberById(id);
            return member.isPresent() 
                ? ControllerUtils.ok(convertToDTO(member.get()))
                : ControllerUtils.notFound();
        });
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<MemberDTO> getMemberByEmail(@PathVariable String email) {
        return ControllerUtils.executeWithErrorHandling(() -> {
            Optional<Member> member = memberService.getMemberByEmail(email);
            return member.isPresent() 
                ? ControllerUtils.ok(convertToDTO(member.get()))
                : ControllerUtils.notFound();
        });
    }

    @PostMapping
    public ResponseEntity<MemberDTO> createMember(@RequestBody Member member) {
        return handleMemberCreation(member);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemberDTO> updateMember(@PathVariable Long id, @RequestBody Member member) {
        return handleMemberUpdate(id, member);
    }

    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<MemberDTO>> getMembersByChapter(@PathVariable Long chapterId) {
        try {
            List<Member> members = memberService.getMembersByChapter(chapterId);
            return ResponseEntity.ok(convertToMemberDTOList(members));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembersByName(@RequestParam String name) {
        try {
            List<Member> members = memberService.searchMembersByName(name);
            return ResponseEntity.ok(convertToMemberDTOList(members));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/chapter/{chapterId}")
    public ResponseEntity<MemberDTO> addMemberToChapter(@PathVariable Long chapterId, @RequestBody Member member) {
        return handleChapterMemberOperation(chapterId, member);
    }

    @PutMapping("/{id}/transfer/{newChapterId}")
    public ResponseEntity<MemberDTO> transferMemberToChapter(@PathVariable Long id, @PathVariable Long newChapterId) {
        return handleMemberTransfer(id, newChapterId);
    }

    /**
     * Permanently deletes member from system (hard delete).
     * Use with caution - this operation cannot be undone.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        return handleMemberDeletion(id);
    }
}
