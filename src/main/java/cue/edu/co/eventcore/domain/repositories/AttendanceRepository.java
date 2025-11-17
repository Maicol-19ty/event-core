package cue.edu.co.eventcore.domain.repositories;

import cue.edu.co.eventcore.domain.entities.Attendance;
import cue.edu.co.eventcore.domain.entities.AttendanceStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Attendance entity
 * This interface is part of the domain layer and defines the contract
 * that infrastructure layer must implement
 */
public interface AttendanceRepository {

    /**
     * Save or update an attendance
     * @param attendance the attendance to save
     * @return the saved attendance
     */
    Attendance save(Attendance attendance);

    /**
     * Find an attendance by ID
     * @param id the attendance ID
     * @return an Optional containing the attendance if found
     */
    Optional<Attendance> findById(Long id);

    /**
     * Find an attendance by event ID and participant ID
     * @param eventId the event ID
     * @param participantId the participant ID
     * @return an Optional containing the attendance if found
     */
    Optional<Attendance> findByEventIdAndParticipantId(Long eventId, Long participantId);

    /**
     * Find all attendances for an event
     * @param eventId the event ID
     * @return list of attendances for the event
     */
    List<Attendance> findByEventId(Long eventId);

    /**
     * Find all attendances for a participant
     * @param participantId the participant ID
     * @return list of attendances for the participant
     */
    List<Attendance> findByParticipantId(Long participantId);

    /**
     * Find attendances by event ID and status
     * @param eventId the event ID
     * @param status the attendance status
     * @return list of attendances matching the criteria
     */
    List<Attendance> findByEventIdAndStatus(Long eventId, AttendanceStatus status);

    /**
     * Check if an attendance exists by event ID and participant ID
     * @param eventId the event ID
     * @param participantId the participant ID
     * @return true if the attendance exists
     */
    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);

    /**
     * Count attendances for an event
     * @param eventId the event ID
     * @return number of attendances for the event
     */
    long countByEventId(Long eventId);

    /**
     * Count attendances for an event by status
     * @param eventId the event ID
     * @param status the attendance status
     * @return number of attendances matching the criteria
     */
    long countByEventIdAndStatus(Long eventId, AttendanceStatus status);

    /**
     * Delete an attendance by ID
     * @param id the attendance ID
     */
    void deleteById(Long id);

    /**
     * Delete all attendances for an event
     * @param eventId the event ID
     */
    void deleteByEventId(Long eventId);

    /**
     * Find all attendances
     * @return list of all attendances
     */
    List<Attendance> findAll();
}
