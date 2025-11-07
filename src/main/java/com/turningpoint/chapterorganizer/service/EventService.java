package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.entity.EventType;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getEventsByChapterId(Long chapterId) {
        return eventRepository.findByChapterId(chapterId);
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findEventsByCriteria(null, null, null, null, null, null, true);
    }

    public Event createEvent(Event event) {
        if (event.getEventDateTime() != null && event.getEventDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date and time cannot be in the past");
        }
        return eventRepository.save(event);
    }

    public List<Event> getUpcomingEventsByChapter(Long chapterId) {
        return eventRepository.findByChapterIdAndEventDateTimeAfterOrderByEventDateTime(chapterId, LocalDateTime.now());
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        
        Event event = existingEvent.get();
        if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
        if (updatedEvent.getDescription() != null) event.setDescription(updatedEvent.getDescription());
        if (updatedEvent.getEventDateTime() != null) event.setEventDateTime(updatedEvent.getEventDateTime());
        if (updatedEvent.getLocation() != null) event.setLocation(updatedEvent.getLocation());
        if (updatedEvent.getType() != null) event.setType(updatedEvent.getType());
        if (updatedEvent.getMaxAttendees() != null) event.setMaxAttendees(updatedEvent.getMaxAttendees());
        
        return eventRepository.save(event);
    }

    public Event registerAttendee(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }
        
        Event event = optionalEvent.get();
        if (event.getMaxAttendees() != null && event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new RuntimeException("Event is at maximum capacity");
        }
        
        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        return eventRepository.save(event);
    }

    public Event unregisterAttendee(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }
        
        Event event = optionalEvent.get();
        if (event.getCurrentAttendees() > 0) {
            event.setCurrentAttendees(event.getCurrentAttendees() - 1);
        }
        return eventRepository.save(event);
    }

    public void cancelEvent(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new RuntimeException("Event not found with id: " + eventId);
        }
        
        Event event = optionalEvent.get();
        event.setActive(false);
        eventRepository.save(event);
    }

    public List<Event> getEventsByType(Long chapterId, EventType eventType) {
        return eventRepository.findByChapterIdAndType(chapterId, eventType);
    }

    public Long countUpcomingEventsByChapter(Long chapterId) {
        return (long) eventRepository.findByChapterIdAndEventDateTimeAfterOrderByEventDateTime(chapterId, LocalDateTime.now()).size();
    }
}
