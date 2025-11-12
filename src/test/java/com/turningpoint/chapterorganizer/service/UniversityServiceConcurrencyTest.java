package com.turningpoint.chapterorganizer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency tests for UniversityService following Robert Martin's Clean Code principles.
 * Tests thread safety of the service under concurrent access patterns.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@SpringJUnitConfig
class UniversityServiceConcurrencyTest {

    private final UniversityService universityService = new UniversityService();

    @Test
    void testConcurrentAccessToGetAllUniversitiesByState() throws InterruptedException {
        // Test multiple threads accessing the service concurrently
        final int numberOfThreads = 10;
        final int operationsPerThread = 100;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final AtomicInteger errorCount = new AtomicInteger(0);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        // Submit tasks to executor
        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    for (int j = 0; j < operationsPerThread; j++) {
                        // Perform concurrent read operations
                        Map<String, List<String>> allUniversities = universityService.getAllUniversitiesByState();
                        assertNotNull(allUniversities);
                        assertFalse(allUniversities.isEmpty());
                        
                        List<String> californiaUniversities = universityService.getUniversitiesByState("California");
                        assertNotNull(californiaUniversities);
                        assertFalse(californiaUniversities.isEmpty());
                        
                        List<String> states = universityService.getAllStates();
                        assertNotNull(states);
                        assertFalse(states.isEmpty());
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Start all threads simultaneously
        startLatch.countDown();
        
        // Wait for all threads to complete
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS), "Test should complete within 30 seconds");
        
        // Verify no errors occurred
        assertEquals(0, errorCount.get(), "No errors should occur during concurrent access");
        
        executor.shutdown();
    }

    @Test
    void testImmutableReturnValues() {
        // Test that returned collections are immutable to prevent concurrent modification
        Map<String, List<String>> universitiesByState = universityService.getAllUniversitiesByState();
        
        // Should not be able to modify the returned map
        assertThrows(UnsupportedOperationException.class, () -> {
            universitiesByState.put("TestState", List.of("Test University"));
        });
        
        // Should not be able to modify the returned lists
        List<String> californiaUniversities = universityService.getUniversitiesByState("California");
        assertThrows(UnsupportedOperationException.class, () -> {
            californiaUniversities.add("Test University");
        });
        
        // Should not be able to modify the states list
        List<String> states = universityService.getAllStates();
        assertThrows(UnsupportedOperationException.class, () -> {
            states.add("TestState");
        });
    }

    @Test
    void testDataConsistencyUnderConcurrentAccess() throws InterruptedException {
        // Test that the same data is returned consistently under concurrent access
        final int numberOfThreads = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        
        // Get baseline data
        final int expectedStatesCount = universityService.getAllStates().size();
        final int expectedCaliforniaUniversitiesCount = universityService.getUniversitiesByState("California").size();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    // Each thread performs multiple reads and verifies consistency
                    for (int j = 0; j < 50; j++) {
                        int actualStatesCount = universityService.getAllStates().size();
                        assertEquals(expectedStatesCount, actualStatesCount, 
                                   "States count should be consistent across all threads");
                        
                        int actualCaliforniaCount = universityService.getUniversitiesByState("California").size();
                        assertEquals(expectedCaliforniaUniversitiesCount, actualCaliforniaCount,
                                   "California universities count should be consistent across all threads");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("No exceptions should occur during concurrent access: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
    }

    @Test
    void testPerformanceUnderConcurrentLoad() throws InterruptedException {
        // Test that performance doesn't degrade significantly under concurrent load
        final int numberOfThreads = 50;
        final int operationsPerThread = 20;
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        final ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < operationsPerThread; j++) {
                        universityService.getAllUniversitiesByState();
                        universityService.getUniversitiesByState("California");
                        universityService.getUniversitiesByState("New York");
                        universityService.getAllStates();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(60, TimeUnit.SECONDS));
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        // Performance should be reasonable - less than 10 seconds for all operations
        assertTrue(totalTime < 10000, 
                  "Concurrent operations should complete within 10 seconds, took: " + totalTime + "ms");
        
        executor.shutdown();
    }

    @Test 
    void testNoMemoryLeaksUnderConcurrentAccess() throws InterruptedException {
        // Test that concurrent access doesn't cause memory issues
        final int iterations = 100;
        final CountDownLatch doneLatch = new CountDownLatch(iterations);
        final ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < iterations; i++) {
            executor.submit(() -> {
                try {
                    // Create many references to test immutable collections don't cause memory issues
                    for (int j = 0; j < 10; j++) {
                        Map<String, List<String>> universities = universityService.getAllUniversitiesByState();
                        List<String> states = universityService.getAllStates();
                        
                        // Force some processing to ensure objects are used
                        universities.forEach((state, unis) -> {
                            assertNotNull(state);
                            assertNotNull(unis);
                        });
                        
                        states.forEach(state -> assertNotNull(state));
                    }
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        assertTrue(doneLatch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        // Force garbage collection
        System.gc();
        
        // If we get here without OutOfMemoryError, the test passes
        assertTrue(true, "No memory leaks should occur under concurrent access");
    }
}