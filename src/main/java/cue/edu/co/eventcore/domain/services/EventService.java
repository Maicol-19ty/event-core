package cue.edu.co.eventcore.domain.services;

import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.EventStatus;
import cue.edu.co.eventcore.domain.exceptions.BusinessRuleException;
import cue.edu.co.eventcore.domain.exceptions.ResourceNotFoundException;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain service for Event business logic
 * Contains complex business rules and orchestrates operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * Create a new event
     * @param event the event to create
     * @return the created event
     */
    public Event createEvent(Event event) {
        validateEventDates(event);
        validateEventCapacity(event);

        event.setCurrentAttendees(0);
        event.setStatus(EventStatus.ACTIVE);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * Update an existing event
     * @param id the event ID
     * @param updatedEvent the updated event data
     * @return the updated event
     */
    public Event updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        validateEventDates(updatedEvent);
        validateEventCapacityUpdate(existingEvent, updatedEvent);

        existingEvent.setName(updatedEvent.getName());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setStartDate(updatedEvent.getStartDate());
        existingEvent.setEndDate(updatedEvent.getEndDate());
        existingEvent.setCapacity(updatedEvent.getCapacity());
        existingEvent.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(existingEvent);
    }

    /**
     * Get event by ID
     * @param id the event ID
     * @return the event
     */
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    /**
     * Get all events
     * @return list of all events
     */
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Get upcoming events
     * @return list of upcoming events
     */
    @Transactional(readOnly = true)
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents();
    }

    /**
     * Get events by status
     * @param status the event status
     * @return list of events with the given status
     */
    @Transactional(readOnly = true)
    public List<Event> getEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    /**
     * Cancel an event
     * @param id the event ID
     * @return the cancelled event
     */
    public Event cancelEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        if (event.hasEnded()) {
            throw new BusinessRuleException("Cannot cancel an event that has already ended");
        }

        event.setStatus(EventStatus.CANCELLED);
        event.setUpdatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * Delete an event
     * @param id the event ID
     */
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }

        long attendanceCount = attendanceRepository.countByEventId(id);
        if (attendanceCount > 0) {
            throw new BusinessRuleException("Cannot delete an event with registered attendances");
        }

        eventRepository.deleteById(id);
    }

    /**
     * Validate event dates
     */
    private void validateEventDates(Event event) {
        if (event.getStartDate() == null || event.getEndDate() == null) {
            throw new BusinessRuleException("Event start and end dates are required");
        }

        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new BusinessRuleException("Event end date must be after start date");
        }

        if (event.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Event start date must be in the future");
        }
    }

    /**
     * Validate event capacity
     */
    private void validateEventCapacity(Event event) {
        if (event.getCapacity() == null || event.getCapacity() <= 0) {
            throw new BusinessRuleException("Event capacity must be greater than zero");
        }
    }

    /**
     * Validate event capacity when updating
     */
    private void validateEventCapacityUpdate(Event existingEvent, Event updatedEvent) {
        if (updatedEvent.getCapacity() == null || updatedEvent.getCapacity() <= 0) {
            throw new BusinessRuleException("Event capacity must be greater than zero");
        }

        if (updatedEvent.getCapacity() < existingEvent.getCurrentAttendees()) {
            throw new BusinessRuleException(
                    String.format("Cannot reduce capacity below current attendees (%d)",
                            existingEvent.getCurrentAttendees())
            );
        }
    }
}
