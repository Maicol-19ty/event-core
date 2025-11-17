package cue.edu.co.eventcore.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity representing an Event
 * This is a pure domain object with no framework dependencies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private Integer currentAttendees;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if event has available spots
     * @return true if there are spots available
     */
    public boolean hasAvailableSpots() {
        if (capacity == null || currentAttendees == null) {
            return false;
        }
        return currentAttendees < capacity;
    }

    /**
     * Business logic: Get remaining capacity
     * @return number of remaining spots
     */
    public int getRemainingCapacity() {
        if (capacity == null || currentAttendees == null) {
            return 0;
        }
        return capacity - currentAttendees;
    }

    /**
     * Business logic: Check if event is active
     * @return true if event is active
     */
    public boolean isActive() {
        return EventStatus.ACTIVE.equals(status);
    }

    /**
     * Business logic: Check if event is in the future
     * @return true if event has not started yet
     */
    public boolean isFuture() {
        return startDate != null && startDate.isAfter(LocalDateTime.now());
    }

    /**
     * Business logic: Check if event has ended
     * @return true if event has ended
     */
    public boolean hasEnded() {
        return endDate != null && endDate.isBefore(LocalDateTime.now());
    }

    /**
     * Business logic: Increment attendees count
     */
    public void incrementAttendees() {
        if (currentAttendees == null) {
            currentAttendees = 0;
        }
        currentAttendees++;
    }

    /**
     * Business logic: Decrement attendees count
     */
    public void decrementAttendees() {
        if (currentAttendees != null && currentAttendees > 0) {
            currentAttendees--;
        }
    }
}
