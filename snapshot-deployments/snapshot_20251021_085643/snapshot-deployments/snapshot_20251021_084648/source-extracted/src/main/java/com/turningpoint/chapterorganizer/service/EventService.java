package com.turningpoint.chapterorganizer.service;

import com.turningpoint.chapterorganizer.entity.*;
import com.turningpoint.chapterorganizer.repository.EventRepository;
import com.turningpoint.chapterorganizer.repository.EventRSVPRepository;
import com.turningpoint.chapterorganizer.repository.RecurringEventRepository;
import com.turningpoint.chapterorganizer.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRSVPRepository eventRSVPRepository;
    private final RecurringEventRepository recurringEventRepository;
    private final MemberRepository memberRepository;
    private final ChapterService chapterService;

    @Autowired
    public EventService(EventRepository eventRepository, 
                       EventRSVPRepository eventRSVPRepository,
                       RecurringEventRepository recurringEventRepository,
                       MemberRepository memberRepository,
                       ChapterService chapterService) {
        this.eventRepository = eventRepository;
        this.eventRSVPRepository = eventRSVPRepository;
        this.recurringEventRepository = recurringEventRepository;
        this.memberRepository = memberRepository;
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

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
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

    // RSVP Management Methods

    @Transactional
    public EventRSVP createOrUpdateRSVP(Long eventId, Long memberId, RSVPStatus status, String notes) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        // Check if RSVP already exists
        Optional<EventRSVP> existingRSVP = eventRSVPRepository.findByEventIdAndMemberId(eventId, memberId);
        
        EventRSVP rsvp;
        if (existingRSVP.isPresent()) {
            // Update existing RSVP
            rsvp = existingRSVP.get();
            rsvp.setStatus(status);
            rsvp.setNotes(notes);
        } else {
            // Create new RSVP
            rsvp = new EventRSVP(event, member, status);
            rsvp.setNotes(notes);
            
            // Check capacity for ATTENDING status
            if (status == RSVPStatus.ATTENDING && !event.canAddAttendee()) {
                rsvp.setStatus(RSVPStatus.WAITLIST);
            }
        }

        return eventRSVPRepository.save(rsvp);
    }

    public List<EventRSVP> getEventRSVPs(Long eventId) {
        return eventRSVPRepository.findByEventId(eventId);
    }

    public List<EventRSVP> getMemberRSVPs(Long memberId) {
        return eventRSVPRepository.findByMemberId(memberId);
    }

    public long getAttendeeCount(Long eventId) {
        return eventRSVPRepository.countByEventIdAndStatus(eventId, RSVPStatus.ATTENDING);
    }

    public boolean hasUserRSVPd(Long eventId, Long memberId) {
        return eventRSVPRepository.existsByEventIdAndMemberId(eventId, memberId);
    }

    @Transactional
    public void markAttendance(Long eventId, Long memberId, boolean attended) {
        EventRSVP rsvp = eventRSVPRepository.findByEventIdAndMemberId(eventId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("RSVP not found for event and member"));
        
        rsvp.setAttended(attended);
        eventRSVPRepository.save(rsvp);
    }

    // Recurring Event Management

    @Transactional
    public RecurringEvent createRecurringEvent(Long baseEventId, RecurrencePattern pattern, 
                                             Integer interval, LocalDateTime endDate, Integer maxOccurrences) {
        Event baseEvent = eventRepository.findById(baseEventId)
                .orElseThrow(() -> new IllegalArgumentException("Base event not found with id: " + baseEventId));

        RecurringEvent recurringEvent = new RecurringEvent(baseEvent, pattern, interval);
        recurringEvent.setEndDate(endDate);
        recurringEvent.setMaxOccurrences(maxOccurrences);

        return recurringEventRepository.save(recurringEvent);
    }

    @Transactional
    public void generateNextOccurrences() {
        List<RecurringEvent> activeRecurring = recurringEventRepository
                .findActiveRecurringEventsNeedingOccurrences(LocalDateTime.now());

        for (RecurringEvent recurring : activeRecurring) {
            if (recurring.shouldCreateNextOccurrence()) {
                createNextOccurrence(recurring);
            }
        }
    }

    @Transactional
    public Event createNextOccurrence(RecurringEvent recurringEvent) {
        Event baseEvent = recurringEvent.getBaseEvent();
        LocalDateTime nextEventDate = recurringEvent.calculateNextEventDate();

        Event nextEvent = new Event();
        nextEvent.setTitle(baseEvent.getTitle());
        nextEvent.setDescription(baseEvent.getDescription());
        nextEvent.setEventDateTime(nextEventDate);
        nextEvent.setLocation(baseEvent.getLocation());
        nextEvent.setType(baseEvent.getType());
        nextEvent.setMaxAttendees(baseEvent.getMaxAttendees());
        nextEvent.setChapter(baseEvent.getChapter());
        nextEvent.setActive(true);

        Event savedEvent = eventRepository.save(nextEvent);

        // Update occurrences count
        recurringEvent.setOccurrencesCreated(recurringEvent.getOccurrencesCreated() + 1);
        recurringEventRepository.save(recurringEvent);

        return savedEvent;
    }

    public List<RecurringEvent> getRecurringEventsByChapter(Long chapterId) {
        return recurringEventRepository.findByChapterId(chapterId);
    }
}