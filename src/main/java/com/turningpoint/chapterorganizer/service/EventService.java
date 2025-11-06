package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.Event;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
