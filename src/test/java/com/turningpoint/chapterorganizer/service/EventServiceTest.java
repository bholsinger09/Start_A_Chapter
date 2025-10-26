package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Chapter;
import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.entity.EventType;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ChapterService chapterService;

    @Mock
    private WebSocketNotificationService notificationService;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private Chapter testChapter;
    private LocalDateTime futureDateTime;

    @BeforeEach
    void setUp() {
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");
        testChapter.setUniversityName("Test University");
        testChapter.setActive(true);

        futureDateTime = LocalDateTime.now().plusDays(7);

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setEventDateTime(futureDateTime);
        testEvent.setLocation("Test Location");
        testEvent.setType(EventType.MEETING);
        testEvent.setMaxAttendees(50);
        testEvent.setCurrentAttendees(0);
        testEvent.setActive(true);
        testEvent.setChapter(testChapter);
    }

    @Test
    void createEvent_ShouldReturnSavedEvent_WhenValidInput() {
        // Given
        Event newEvent = new Event("New Event", futureDateTime, testChapter);
        when(chapterService.getChapterById(testChapter.getId())).thenReturn(Optional.of(testChapter));
        when(eventRepository.save(any(Event.class))).thenReturn(newEvent);

        // When
        Event result = eventService.createEvent(newEvent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("New Event");
        assertThat(result.getActive()).isTrue();
        verify(eventRepository).save(newEvent);
    }

    @Test
    void createEvent_ShouldThrowException_WhenChapterNotFound() {
        // Given
        testEvent.getChapter().setId(999L);
        when(chapterService.getChapterById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(testEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Chapter not found with id: 999");
    }

    @Test
    void createEvent_ShouldThrowException_WhenEventDateTimeInPast() {
        // Given
        LocalDateTime pastDateTime = LocalDateTime.now().minusDays(1);
        testEvent.setEventDateTime(pastDateTime);
        when(chapterService.getChapterById(testChapter.getId())).thenReturn(Optional.of(testChapter));

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(testEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event date and time cannot be in the past");
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenExists() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        Optional<Event> result = eventService.getEventById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Event");
    }

    @Test
    void getUpcomingEventsByChapter_ShouldReturnUpcomingEvents() {
        // Given
        List<Event> upcomingEvents = Arrays.asList(testEvent);
        when(eventRepository.findUpcomingEventsByChapter(eq(1L), any(LocalDateTime.class))).thenReturn(upcomingEvents);

        // When
        List<Event> result = eventService.getUpcomingEventsByChapter(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventDateTime()).isAfter(LocalDateTime.now());
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent_WhenExists() {
        // Given
        Event updatedEvent = new Event();
        updatedEvent.setTitle("Updated Event");
        updatedEvent.setEventDateTime(futureDateTime.plusDays(1));
        updatedEvent.setLocation("Updated Location");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.updateEvent(1L, updatedEvent);

        // Then
        assertThat(result).isNotNull();
        verify(eventRepository).save(testEvent);
    }

    @Test
    void updateEvent_ShouldThrowException_WhenNotExists() {
        // Given
        Event updatedEvent = new Event();
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.updateEvent(999L, updatedEvent))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event not found with id: 999");
    }

    @Test
    void registerAttendee_ShouldIncrementAttendees_WhenSpaceAvailable() {
        // Given
        testEvent.setCurrentAttendees(10);
        testEvent.setMaxAttendees(50);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.registerAttendee(1L);

        // Then
        assertThat(result.getCurrentAttendees()).isEqualTo(11);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void registerAttendee_ShouldThrowException_WhenEventFull() {
        // Given
        testEvent.setCurrentAttendees(50);
        testEvent.setMaxAttendees(50);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When & Then
        assertThatThrownBy(() -> eventService.registerAttendee(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Event is already at maximum capacity");
    }

    @Test
    void unregisterAttendee_ShouldDecrementAttendees_WhenAttendeesExist() {
        // Given
        testEvent.setCurrentAttendees(10);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.unregisterAttendee(1L);

        // Then
        assertThat(result.getCurrentAttendees()).isEqualTo(9);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void cancelEvent_ShouldMarkAsInactive_WhenExists() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        eventService.cancelEvent(1L);

        // Then
        assertThat(testEvent.getActive()).isFalse();
        verify(eventRepository).save(testEvent);
    }

    @Test
    void getEventsByType_ShouldReturnEventsOfSpecificType() {
        // Given
        List<Event> meetingEvents = Arrays.asList(testEvent);
        when(eventRepository.findByTypeAndChapterId(EventType.MEETING, 1L)).thenReturn(meetingEvents);

        // When
        List<Event> result = eventService.getEventsByType(1L, EventType.MEETING);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(EventType.MEETING);
    }

    @Test
    void countUpcomingEvents_ShouldReturnCount() {
        // Given
        when(eventRepository.countUpcomingEventsByChapter(eq(1L), any(LocalDateTime.class))).thenReturn(5L);

        // When
        Long result = eventService.countUpcomingEventsByChapter(1L);

        // Then
        assertThat(result).isEqualTo(5L);
    }
}