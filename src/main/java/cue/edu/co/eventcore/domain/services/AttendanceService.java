package cue.edu.co.eventcore.domain.services;

import cue.edu.co.eventcore.domain.entities.Attendance;
import cue.edu.co.eventcore.domain.entities.AttendanceStatus;
import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.exceptions.BusinessRuleException;
import cue.edu.co.eventcore.domain.exceptions.DuplicateResourceException;
import cue.edu.co.eventcore.domain.exceptions.ResourceNotFoundException;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain service for Attendance business logic
 * Contains complex business rules and orchestrates operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Register a participant to an event
     * @param eventId the event ID
     * @param participantId the participant ID
     * @return the created attendance
     */
    public Attendance registerAttendance(Long eventId, Long participantId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", eventId));

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", participantId));

        validateAttendanceRegistration(event, participant, eventId, participantId);

        Attendance attendance = Attendance.builder()
                .eventId(eventId)
                .participantId(participantId)
                .status(AttendanceStatus.REGISTERED)
                .registrationDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        attendance = attendanceRepository.save(attendance);

        // Update event attendee count
        event.incrementAttendees();
        eventRepository.save(event);

        return attendance;
    }

    /**
     * Check in a participant to an event
     * @param attendanceId the attendance ID
     * @return the updated attendance
     */
    public Attendance checkInAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", attendanceId));

        if (attendance.isCheckedIn()) {
            throw new BusinessRuleException("Participant has already checked in");
        }

        if (attendance.isCancelled()) {
            throw new BusinessRuleException("Cannot check in a cancelled attendance");
        }

        attendance.checkIn();
        attendance.setUpdatedAt(LocalDateTime.now());

        return attendanceRepository.save(attendance);
    }

    /**
     * Cancel an attendance
     * @param attendanceId the attendance ID
     * @return the cancelled attendance
     */
    public Attendance cancelAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", attendanceId));

        if (attendance.isCancelled()) {
            throw new BusinessRuleException("Attendance is already cancelled");
        }

        Attendance finalAttendance = attendance;
        Event event = eventRepository.findById(attendance.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", finalAttendance.getEventId()));

        attendance.cancel();
        attendance.setUpdatedAt(LocalDateTime.now());

        attendance = attendanceRepository.save(attendance);

        // Update event attendee count
        event.decrementAttendees();
        eventRepository.save(event);

        return attendance;
    }

    /**
     * Get attendance by ID
     * @param id the attendance ID
     * @return the attendance
     */
    @Transactional(readOnly = true)
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
    }

    /**
     * Get all attendances for an event
     * @param eventId the event ID
     * @return list of attendances
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAttendancesByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", eventId);
        }
        return attendanceRepository.findByEventId(eventId);
    }

    /**
     * Get all attendances for a participant
     * @param participantId the participant ID
     * @return list of attendances
     */
    @Transactional(readOnly = true)
    public List<Attendance> getAttendancesByParticipant(Long participantId) {
        if (!participantRepository.existsById(participantId)) {
            throw new ResourceNotFoundException("Participant", participantId);
        }
        return attendanceRepository.findByParticipantId(participantId);
    }

    /**
     * Get event statistics
     * @param eventId the event ID
     * @return statistics object
     */
    @Transactional(readOnly = true)
    public EventStatistics getEventStatistics(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", eventId);
        }

        long totalRegistered = attendanceRepository.countByEventIdAndStatus(eventId, AttendanceStatus.REGISTERED);
        long totalCheckedIn = attendanceRepository.countByEventIdAndStatus(eventId, AttendanceStatus.CHECKED_IN);
        long totalCancelled = attendanceRepository.countByEventIdAndStatus(eventId, AttendanceStatus.CANCELLED);
        long totalNoShow = attendanceRepository.countByEventIdAndStatus(eventId, AttendanceStatus.NO_SHOW);

        Event event = eventRepository.findById(eventId).orElseThrow();

        return EventStatistics.builder()
                .eventId(eventId)
                .totalCapacity(event.getCapacity())
                .totalRegistered(totalRegistered)
                .totalCheckedIn(totalCheckedIn)
                .totalCancelled(totalCancelled)
                .totalNoShow(totalNoShow)
                .availableSpots(event.getRemainingCapacity())
                .occupancyPercentage(calculateOccupancyPercentage(event.getCapacity(), totalRegistered))
                .build();
    }

    /**
     * Validate attendance registration
     */
    private void validateAttendanceRegistration(Event event, Participant participant, Long eventId, Long participantId) {
        // Check if participant is active
        if (!participant.isActive()) {
            throw new BusinessRuleException("Participant is not active");
        }

        // Check if event is active
        if (!event.isActive()) {
            throw new BusinessRuleException("Event is not active");
        }

        // Check if event has ended
        if (event.hasEnded()) {
            throw new BusinessRuleException("Cannot register to an event that has already ended");
        }

        // Check for duplicate registration
        if (attendanceRepository.existsByEventIdAndParticipantId(eventId, participantId)) {
            throw new DuplicateResourceException("Attendance for this event and participant already exists");
        }

        // Check event capacity
        if (!event.hasAvailableSpots()) {
            throw new BusinessRuleException("Event has reached maximum capacity");
        }
    }

    /**
     * Calculate occupancy percentage
     */
    private double calculateOccupancyPercentage(int capacity, long registered) {
        if (capacity == 0) {
            return 0.0;
        }
        return (registered * 100.0) / capacity;
    }

    /**
     * Inner class for event statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class EventStatistics {
        private Long eventId;
        private Integer totalCapacity;
        private Long totalRegistered;
        private Long totalCheckedIn;
        private Long totalCancelled;
        private Long totalNoShow;
        private Integer availableSpots;
        private Double occupancyPercentage;
    }
}
