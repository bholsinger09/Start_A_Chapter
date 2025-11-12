package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Member;
import com.turningpoint.chapterorganizer.entity.MemberRole;
import com.turningpoint.chapterorganizer.repository.ChapterRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Concurrency tests for service layer race condition fixes.
 * Tests the behavior when multiple threads try to create entities with the same constraints.
 * Follows Robert Martin's Clean Code principles for testing concurrent behavior.
 */
@ExtendWith(MockitoExtension.class)
class ServiceRaceConditionTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChapterService chapterService;

    @InjectMocks
    private ChapterService chapterServiceUnderTest;

    @InjectMocks
    private MemberService memberService;

    @Test
    void testConcurrentChapterCreationWithSameName() throws InterruptedException {
        // Test that concurrent attempts to create chapters with the same name are handled correctly
        final int numberOfThreads = 10;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger constraintViolationCount = new AtomicInteger(0);

        // Mock behavior: first save succeeds, subsequent saves throw constraint violation
        when(chapterRepository.save(any(Chapter.class)))
            .thenReturn(createTestChapter()) // First call succeeds
            .thenThrow(new DataIntegrityViolationException("uk_chapter_name_university")); // Subsequent calls fail

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    Chapter chapter = new Chapter("Test Chapter", "Test University", "Test State", "Test City");
                    chapterServiceUnderTest.createChapter(chapter);
                    successCount.incrementAndGet();
                    
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().contains("already exists")) {
                        constraintViolationCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));

        // Verify that only one creation succeeded and others were properly handled
        assertEquals(1, successCount.get(), "Only one chapter creation should succeed");
        assertEquals(numberOfThreads - 1, constraintViolationCount.get(), 
                    "All other attempts should result in constraint violations");
        
        executor.shutdown();
    }

    @Test
    void testConcurrentMemberCreationWithSameEmail() throws InterruptedException {
        // Test that concurrent attempts to create members with the same email are handled correctly
        final int numberOfThreads = 8;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger constraintViolationCount = new AtomicInteger(0);

        // Mock behavior
        when(chapterService.getChapterById(any())).thenReturn(java.util.Optional.of(createTestChapter()));
        when(memberRepository.save(any(Member.class)))
            .thenReturn(createTestMember()) // First call succeeds
            .thenThrow(new DataIntegrityViolationException("uk_member_email")); // Subsequent calls fail

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    Member member = new Member("Test", "User", "test@example.com", createTestChapter());
                    member.setUsername("testuser" + System.nanoTime()); // Unique username
                    memberService.createMember(member);
                    successCount.incrementAndGet();
                    
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().contains("email already exists")) {
                        constraintViolationCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));

        // Verify that only one creation succeeded and others were properly handled
        assertEquals(1, successCount.get(), "Only one member creation should succeed");
        assertEquals(numberOfThreads - 1, constraintViolationCount.get(), 
                    "All other attempts should result in constraint violations");
        
        executor.shutdown();
    }

    @Test
    void testConcurrentMemberCreationWithSameUsername() throws InterruptedException {
        // Test that concurrent attempts to create members with the same username are handled correctly
        final int numberOfThreads = 6;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger constraintViolationCount = new AtomicInteger(0);

        // Mock behavior
        when(chapterService.getChapterById(any())).thenReturn(java.util.Optional.of(createTestChapter()));
        when(memberRepository.save(any(Member.class)))
            .thenReturn(createTestMember()) // First call succeeds
            .thenThrow(new DataIntegrityViolationException("uk_member_username")); // Subsequent calls fail

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    Member member = new Member("Test", "User", "test" + threadIndex + "@example.com", createTestChapter());
                    member.setUsername("sameusername"); // Same username for all threads
                    memberService.createMember(member);
                    successCount.incrementAndGet();
                    
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().contains("username already exists")) {
                        constraintViolationCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));

        // Verify that only one creation succeeded and others were properly handled
        assertEquals(1, successCount.get(), "Only one member creation should succeed");
        assertEquals(numberOfThreads - 1, constraintViolationCount.get(), 
                    "All other attempts should result in constraint violations");
        
        executor.shutdown();
    }

    @Test
    void testProperExceptionHandlingUnderConcurrentLoad() throws InterruptedException {
        // Test that exceptions are properly categorized and handled under concurrent load
        final int numberOfThreads = 12;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger emailViolations = new AtomicInteger(0);
        final AtomicInteger usernameViolations = new AtomicInteger(0);
        final AtomicInteger unknownViolations = new AtomicInteger(0);

        // Mock to throw different types of constraint violations
        when(chapterService.getChapterById(any())).thenReturn(java.util.Optional.of(createTestChapter()));
        when(memberRepository.save(any(Member.class)))
            .thenThrow(new DataIntegrityViolationException("uk_member_email"))
            .thenThrow(new DataIntegrityViolationException("uk_member_username"))
            .thenThrow(new DataIntegrityViolationException("some other constraint"));

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    Member member = new Member("Test", "User", "test" + threadIndex + "@example.com", createTestChapter());
                    member.setUsername("user" + threadIndex);
                    memberService.createMember(member);
                    
                } catch (IllegalArgumentException e) {
                    String message = e.getMessage();
                    if (message.contains("email already exists")) {
                        emailViolations.incrementAndGet();
                    } else if (message.contains("username already exists")) {
                        usernameViolations.incrementAndGet();
                    } else if (message.contains("data conflict")) {
                        unknownViolations.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));

        // Verify that different constraint violations are properly categorized
        assertTrue(emailViolations.get() > 0, "Should have some email constraint violations");
        assertTrue(usernameViolations.get() > 0, "Should have some username constraint violations");
        assertTrue(unknownViolations.get() > 0, "Should have some unknown constraint violations");
        assertEquals(numberOfThreads, emailViolations.get() + usernameViolations.get() + unknownViolations.get(),
                    "All threads should result in constraint violations");
        
        executor.shutdown();
    }

    private Chapter createTestChapter() {
        Chapter chapter = new Chapter("Test Chapter", "Test University", "Test State", "Test City");
        chapter.setId(1L);
        return chapter;
    }

    private Member createTestMember() {
        Member member = new Member("Test", "User", "test@example.com", createTestChapter());
        member.setId(1L);
        member.setRole(MemberRole.MEMBER);
        return member;
    }
}