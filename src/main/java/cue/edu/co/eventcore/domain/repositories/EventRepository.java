package cue.edu.co.eventcore.domain.repositories;

import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.EventStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Event entity
 * This interface is part of the domain layer and defines the contract
 * that infrastructure layer must implement
 */
public interface EventRepository {

    /**
     * Save or update an event
     * @param event the event to save
     * @return the saved event
     */
    Event save(Event event);

    /**
     * Find an event by its ID
     * @param id the event ID
     * @return an Optional containing the event if found
     */
    Optional<Event> findById(Long id);

    /**
     * Find all events
     * @return list of all events
     */
    List<Event> findAll();

    /**
     * Find events by status
     * @param status the event status
     * @return list of events with the given status
     */
    List<Event> findByStatus(EventStatus status);

    /**
     * Find events within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of events within the date range
     */
    List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find upcoming events
     * @return list of upcoming events
     */
    List<Event> findUpcomingEvents();

    /**
     * Check if an event exists by ID
     * @param id the event ID
     * @return true if the event exists
     */
    boolean existsById(Long id);

    /**
     * Delete an event by ID
     * @param id the event ID
     */
    void deleteById(Long id);

    /**
     * Count total events
     * @return total number of events
     */
    long count();
}
