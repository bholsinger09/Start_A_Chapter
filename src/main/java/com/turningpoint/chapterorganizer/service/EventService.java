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

    private final EventRepository eventRepository;
    private final ChapterService chapterService;

    @Autowired
    public EventService(EventRepository eventRepository, ChapterService chapterService) {
        this.eventRepository = eventRepository;
        this.chapterService = chapterService;
    }

    public Event createEvent(Event event) {
        // Validate that the chapter exists
        if (event.getChapter() != null && event.getChapter().getId() != null) {
            Optional<com.turningpoint.chapterorganizer.entity.Chapter> chapter = 
                chapterService.getChapterById(event.getChapter().getId());
            if (chapter.isEmpty()) {
                throw new IllegalArgumentException("Chapter not found with id: " + event.getChapter().getId());
            }
        }

        // Validate that the event date is not in the past
        if (event.getEventDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date and time cannot be in the past");
        }

        event.setActive(true);
        return eventRepository.save(event);
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getUpcomingEventsByChapter(Long chapterId) {
        return eventRepository.findUpcomingEventsByChapter(chapterId, LocalDateTime.now());
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found with id: " + id);
        }

        Event event = existingEvent.get();
        
        if (updatedEvent.getTitle() != null) {
            event.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null) {
            event.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getEventDateTime() != null) {
            event.setEventDateTime(updatedEvent.getEventDateTime());
        }
        if (updatedEvent.getLocation() != null) {
            event.setLocation(updatedEvent.getLocation());
        }
        if (updatedEvent.getType() != null) {
            event.setType(updatedEvent.getType());
        }
        if (updatedEvent.getMaxAttendees() != null) {
            event.setMaxAttendees(updatedEvent.getMaxAttendees());
        }

        return eventRepository.save(event);
    }

    public Event registerAttendee(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }

        Event event = optionalEvent.get();
        
        if (event.getMaxAttendees() != null && 
            event.getCurrentAttendees() >= event.getMaxAttendees()) {
            throw new IllegalArgumentException("Event is already at maximum capacity");
        }

        event.setCurrentAttendees(event.getCurrentAttendees() + 1);
        return eventRepository.save(event);
    }

    public Event unregisterAttendee(Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new IllegalArgumentException("Event not found with id: " + eventId);
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
            throw new IllegalArgumentException("Event not found with id: " + eventId);
        }

        Event event = optionalEvent.get();
        event.setActive(false);
        eventRepository.save(event);
    }

    public List<Event> getEventsByType(Long chapterId, EventType type) {
        return eventRepository.findByTypeAndChapterId(type, chapterId);
    }

    public Long countUpcomingEventsByChapter(Long chapterId) {
        return eventRepository.countUpcomingEventsByChapter(chapterId, LocalDateTime.now());
    }

    public List<Event> getAllEventsByChapter(Long chapterId) {
        return eventRepository.findByChapterId(chapterId);
    }

    public List<Event> getActiveEventsByChapter(Long chapterId) {
        return eventRepository.findByChapterIdAndActiveTrue(chapterId);
    }

    public List<Event> getPastEventsByChapter(Long chapterId) {
        return eventRepository.findPastEventsByChapter(chapterId, LocalDateTime.now());
    }
}