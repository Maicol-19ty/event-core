package cue.edu.co.eventcore.domain.repositories;

import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.entities.ParticipantStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Participant entity
 * This interface is part of the domain layer and defines the contract
 * that infrastructure layer must implement
 */
public interface ParticipantRepository {

    /**
     * Save or update a participant
     * @param participant the participant to save
     * @return the saved participant
     */
    Participant save(Participant participant);

    /**
     * Find a participant by ID
     * @param id the participant ID
     * @return an Optional containing the participant if found
     */
    Optional<Participant> findById(Long id);

    /**
     * Find a participant by email
     * @param email the participant email
     * @return an Optional containing the participant if found
     */
    Optional<Participant> findByEmail(String email);

    /**
     * Find a participant by document number
     * @param documentNumber the participant document number
     * @return an Optional containing the participant if found
     */
    Optional<Participant> findByDocumentNumber(String documentNumber);

    /**
     * Find all participants
     * @return list of all participants
     */
    List<Participant> findAll();

    /**
     * Find participants by status
     * @param status the participant status
     * @return list of participants with the given status
     */
    List<Participant> findByStatus(ParticipantStatus status);

    /**
     * Check if a participant exists by ID
     * @param id the participant ID
     * @return true if the participant exists
     */
    boolean existsById(Long id);

    /**
     * Check if a participant exists by email
     * @param email the participant email
     * @return true if a participant with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if a participant exists by document number
     * @param documentNumber the participant document number
     * @return true if a participant with this document number exists
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Delete a participant by ID
     * @param id the participant ID
     */
    void deleteById(Long id);

    /**
     * Count total participants
     * @return total number of participants
     */
    long count();
}
