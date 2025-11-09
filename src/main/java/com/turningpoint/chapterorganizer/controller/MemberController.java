package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * Get member by username (treating username as email for compatibility)
     * This endpoint is needed for blog functionality
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Member> getMemberByUsername(@PathVariable String username) {
        try {
            // In our system, username is treated as email
            Optional<Member> member = memberService.getMemberByEmail(username);
            
            if (member.isPresent()) {
                return ResponseEntity.ok(member.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all members
     */
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        try {
            // For now, get all members from all chapters
            // In a real system, you might want to filter by user's chapter
            List<Member> members = memberService.getAllMembers();
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get member by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        try {
            Optional<Member> member = memberService.getMemberById(id);
            
            if (member.isPresent()) {
                return ResponseEntity.ok(member.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get member by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Member> getMemberByEmail(@PathVariable String email) {
        try {
            Optional<Member> member = memberService.getMemberByEmail(email);
            
            if (member.isPresent()) {
                return ResponseEntity.ok(member.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new member
     */
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        try {
            Member createdMember = memberService.createMember(member);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update member
     */
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        try {
            Member updatedMember = memberService.updateMember(id, member);
            return ResponseEntity.ok(updatedMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get members by chapter
     */
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<Member>> getMembersByChapter(@PathVariable Long chapterId) {
        try {
            List<Member> members = memberService.getMembersByChapter(chapterId);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search members by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Member>> searchMembersByName(@RequestParam String name) {
        try {
            List<Member> members = memberService.searchMembersByName(name);
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
