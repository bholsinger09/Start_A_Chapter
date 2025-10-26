package com.turningpoint.chapterorganizer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    private Event testEvent;
    private List<Event> testEvents;
    private Chapter testChapter;

    @BeforeEach
    void setUp() {
        // Create test chapter
        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setName("Test Chapter");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setEventDateTime(LocalDateTime.now().plusDays(7));
        testEvent.setLocation("Test Location");
        testEvent.setType(EventType.MEETING);
        testEvent.setMaxAttendees(50);
        testEvent.setChapter(testChapter);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Second Event");
        event2.setDescription("Second Description");
        event2.setEventDateTime(LocalDateTime.now().plusDays(14));
        event2.setLocation("Second Location");
        event2.setType(EventType.SOCIAL);
        event2.setMaxAttendees(30);
        event2.setChapter(testChapter);

        testEvents = Arrays.asList(testEvent, event2);
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() throws Exception {
        // Given
        when(eventService.getAllEvents()).thenReturn(testEvents);

        // When & Then
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Test Event"))
                .andExpect(jsonPath("$[1].title").value("Second Event"));        verify(eventService).getAllEvents();
    }

    @Test
    void getAllEvents_ShouldReturnEmptyList_WhenNoEvents() throws Exception {
        // Given
        when(eventService.getAllEvents()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(eventService).getAllEvents();
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenExists() throws Exception {
        // Given
        when(eventService.getEventById(1L)).thenReturn(Optional.of(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"))
                .andExpect(jsonPath("$.description").value("Test Description"));

        verify(eventService).getEventById(1L);
    }

    @Test
    void getEventById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        // Given
        when(eventService.getEventById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/events/999"))
                .andExpect(status().isNotFound());

        verify(eventService).getEventById(999L);
    }

    @Test
    void getEventsByChapter_ShouldReturnChapterEvents() throws Exception {
        // Given
        when(eventService.getAllEventsByChapter(1L)).thenReturn(testEvents);

        // When & Then
        mockMvc.perform(get("/api/events/chapter/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Test Event"));

        verify(eventService).getAllEventsByChapter(1L);
    }

    @Test
    void getActiveEventsByChapter_ShouldReturnActiveEvents() throws Exception {
        // Given
        List<Event> activeEvents = Arrays.asList(testEvent);
        when(eventService.getActiveEventsByChapter(1L)).thenReturn(activeEvents);

        // When & Then
        mockMvc.perform(get("/api/events/chapter/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(eventService).getActiveEventsByChapter(1L);
    }

    @Test
    void getUpcomingEventsByChapter_ShouldReturnUpcomingEvents() throws Exception {
        // Given
        when(eventService.getUpcomingEventsByChapter(1L)).thenReturn(testEvents);

        // When & Then
        mockMvc.perform(get("/api/events/chapter/1/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(eventService).getUpcomingEventsByChapter(1L);
    }

    @Test
    void getPastEventsByChapter_ShouldReturnPastEvents() throws Exception {
        // Given
        when(eventService.getPastEventsByChapter(1L)).thenReturn(Arrays.asList(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/chapter/1/past"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(eventService).getPastEventsByChapter(1L);
    }

    @Test
    void getEventsByChapterAndType_ShouldReturnFilteredEvents() throws Exception {
        // Given
        when(eventService.getEventsByType(1L, EventType.MEETING))
                .thenReturn(Arrays.asList(testEvent));

        // When & Then
        mockMvc.perform(get("/api/events/chapter/1/type/MEETING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("MEETING"));

        verify(eventService).getEventsByType(1L, EventType.MEETING);
    }

    @Test
    void createEvent_ShouldReturnCreatedEvent_WhenValidInput() throws Exception {
        // Given
        Event newEvent = new Event();
        newEvent.setTitle("New Event");
        newEvent.setDescription("New Description");
        newEvent.setEventDateTime(LocalDateTime.now().plusDays(1));
        newEvent.setLocation("New Location");
        newEvent.setType(EventType.MEETING);
        newEvent.setChapter(testChapter);

        when(eventService.createEvent(any(Event.class))).thenReturn(testEvent);

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"));

        verify(eventService).createEvent(any(Event.class));
    }

    @Test
    void updateEvent_ShouldReturnUpdatedEvent_WhenExists() throws Exception {
        // Given
        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setTitle("Updated Event");
        updatedEvent.setDescription("Updated Description");
        updatedEvent.setEventDateTime(LocalDateTime.now().plusDays(7));
        updatedEvent.setType(EventType.MEETING);
        updatedEvent.setLocation("Updated Location");

        when(eventService.updateEvent(eq(1L), any(Event.class))).thenReturn(testEvent);

        // When & Then
        mockMvc.perform(put("/api/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(eventService).updateEvent(eq(1L), any(Event.class));
    }

    @Test
    void cancelEvent_ShouldReturnNoContent_WhenExists() throws Exception {
        // Given
        doNothing().when(eventService).cancelEvent(1L);

        // When & Then
        mockMvc.perform(delete("/api/events/1"))
                .andExpect(status().isNoContent());

        verify(eventService).cancelEvent(1L);
    }

    @Test
    void registerAttendee_ShouldReturnUpdatedEvent() throws Exception {
        // Given
        when(eventService.registerAttendee(1L)).thenReturn(testEvent);

        // When & Then
        mockMvc.perform(put("/api/events/1/register")
                        .param("memberId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(eventService).registerAttendee(1L);
    }

    @Test
    void unregisterAttendee_ShouldReturnUpdatedEvent() throws Exception {
        // Given
        when(eventService.unregisterAttendee(1L)).thenReturn(testEvent);

        // When & Then
        mockMvc.perform(put("/api/events/1/unregister")
                        .param("memberId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(eventService).unregisterAttendee(1L);
    }

    @Test
    void getUpcomingEventsCount_ShouldReturnCount() throws Exception {
        // Given
        when(eventService.countUpcomingEventsByChapter(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/events/chapter/1/count/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));

        verify(eventService).countUpcomingEventsByChapter(1L);
    }

    @Test
    void getAttendeesCount_ShouldReturnCount() throws Exception {
        // Given
        when(eventService.getAttendeeCount(1L)).thenReturn(15L);

        // When & Then
        mockMvc.perform(get("/api/events/1/attendees/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(15));

        verify(eventService).getAttendeeCount(1L);
    }

    @Test
    void markAttendance_ShouldReturnSuccess() throws Exception {
        // Given
        doNothing().when(eventService).markAttendance(1L, 1L, true);

        // When & Then
        mockMvc.perform(put("/api/events/1/attendance/1")
                        .param("attended", "true"))
                .andExpect(status().isOk());

        verify(eventService).markAttendance(1L, 1L, true);
    }

    @Test
    void getEventRSVPs_ShouldReturnRSVPList() throws Exception {
        // Given
        List<EventRSVP> rsvps = Arrays.asList();
        when(eventService.getEventRSVPs(1L)).thenReturn(rsvps);

        // When & Then
        mockMvc.perform(get("/api/events/1/rsvps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(eventService).getEventRSVPs(1L);
    }
}