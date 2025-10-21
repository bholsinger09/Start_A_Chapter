package com.turningpoint.chapterorganizer.repository;

import com.turningpoint.chapterorganizer.entity.RecurringEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecurringEventRepository extends JpaRepository<RecurringEvent, Long> {

    // Find all active recurring events
    List<RecurringEvent> findByActiveTrue();

    // Find recurring events that need new occurrences created
    @Query("SELECT r FROM RecurringEvent r WHERE r.active = true AND " +
           "(r.maxOccurrences IS NULL OR r.occurrencesCreated < r.maxOccurrences) AND " +
           "(r.endDate IS NULL OR r.endDate > :currentDate)")
    List<RecurringEvent> findActiveRecurringEventsNeedingOccurrences(LocalDateTime currentDate);

    // Find recurring event by base event
    RecurringEvent findByBaseEventId(Long baseEventId);

    // Find recurring events by chapter
    @Query("SELECT r FROM RecurringEvent r WHERE r.baseEvent.chapter.id = :chapterId")
    List<RecurringEvent> findByChapterId(Long chapterId);
}