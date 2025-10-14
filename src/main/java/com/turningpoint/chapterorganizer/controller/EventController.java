package com.turningpoint.chapterorganizer.controller;

import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.entity.EventType;
import com.turningpoint.chapterorganizer.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET /api/events - Get all events across all chapters
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    // GET /api/events/{id} - Get event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/events/chapter/{chapterId} - Get all events for a chapter
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<Event>> getEventsByChapter(@PathVariable Long chapterId) {
        List<Event> events = eventService.getAllEventsByChapter(chapterId);
        return ResponseEntity.ok(events);
    }

    // GET /api/events/chapter/{chapterId}/active - Get active events for a chapter
    @GetMapping("/chapter/{chapterId}/active")
    public ResponseEntity<List<Event>> getActiveEventsByChapter(@PathVariable Long chapterId) {
        List<Event> events = eventService.getActiveEventsByChapter(chapterId);
        return ResponseEntity.ok(events);
    }

    // GET /api/events/chapter/{chapterId}/upcoming - Get upcoming events for a chapter
    @GetMapping("/chapter/{chapterId}/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEventsByChapter(@PathVariable Long chapterId) {
        List<Event> events = eventService.getUpcomingEventsByChapter(chapterId);
        return ResponseEntity.ok(events);
    }

    // GET /api/events/chapter/{chapterId}/past - Get past events for a chapter
    @GetMapping("/chapter/{chapterId}/past")
    public ResponseEntity<List<Event>> getPastEventsByChapter(@PathVariable Long chapterId) {
        List<Event> events = eventService.getPastEventsByChapter(chapterId);
        return ResponseEntity.ok(events);
    }

    // GET /api/events/chapter/{chapterId}/type/{type} - Get events by type for a chapter
    @GetMapping("/chapter/{chapterId}/type/{type}")
    public ResponseEntity<List<Event>> getEventsByType(@PathVariable Long chapterId, @PathVariable EventType type) {
        List<Event> events = eventService.getEventsByType(chapterId, type);
        return ResponseEntity.ok(events);
    }

    // POST /api/events - Create new event
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        try {
            Event createdEvent = eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/events/{id} - Update existing event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, 
                                           @Valid @RequestBody Event eventDetails) {
        try {
            Event updatedEvent = eventService.updateEvent(id, eventDetails);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/events/{id} - Cancel event (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelEvent(@PathVariable Long id) {
        try {
            eventService.cancelEvent(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/events/{id}/register - Register attendee for event
    @PutMapping("/{id}/register")
    public ResponseEntity<Event> registerAttendee(@PathVariable Long id) {
        try {
            Event event = eventService.registerAttendee(id);
            return ResponseEntity.ok(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/events/{id}/unregister - Unregister attendee from event
    @PutMapping("/{id}/unregister")
    public ResponseEntity<Event> unregisterAttendee(@PathVariable Long id) {
        try {
            Event event = eventService.unregisterAttendee(id);
            return ResponseEntity.ok(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/events/chapter/{chapterId}/count/upcoming - Count upcoming events for a chapter
    @GetMapping("/chapter/{chapterId}/count/upcoming")
    public ResponseEntity<Long> countUpcomingEventsByChapter(@PathVariable Long chapterId) {
        Long count = eventService.countUpcomingEventsByChapter(chapterId);
        return ResponseEntity.ok(count);
    }

    // GET /api/events/{id}/stats - Get event statistics
    @GetMapping("/{id}/stats")
    public ResponseEntity<EventStatsDto> getEventStats(@PathVariable Long id) {
        try {
            Optional<Event> event = eventService.getEventById(id);
            if (event.isPresent()) {
                Event e = event.get();
                EventStatsDto stats = new EventStatsDto(
                    e.getTitle(),
                    e.getCurrentAttendees(),
                    e.getMaxAttendees(),
                    e.getActive(),
                    e.getType()
                );
                return ResponseEntity.ok(stats);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Inner DTO class for event statistics
    public static class EventStatsDto {
        public final String title;
        public final Integer currentAttendees;
        public final Integer maxAttendees;
        public final boolean active;
        public final EventType type;

        public EventStatsDto(String title, Integer currentAttendees, Integer maxAttendees, boolean active, EventType type) {
            this.title = title;
            this.currentAttendees = currentAttendees;
            this.maxAttendees = maxAttendees;
            this.active = active;
            this.type = type;
        }
    }
}