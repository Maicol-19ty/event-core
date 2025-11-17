package cue.edu.co.eventcore.domain.services;

import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import cue.edu.co.eventcore.domain.exceptions.BusinessRuleException;
import cue.edu.co.eventcore.domain.exceptions.DuplicateResourceException;
import cue.edu.co.eventcore.domain.exceptions.ResourceNotFoundException;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain service for Participant business logic
 * Contains complex business rules and orchestrates operations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final AttendanceRepository attendanceRepository;

    /**
     * Create a new participant
     * @param participant the participant to create
     * @return the created participant
     */
    public Participant createParticipant(Participant participant) {
        validateUniqueEmail(participant.getEmail());
        validateUniqueDocumentNumber(participant.getDocumentNumber());

        participant.setStatus(ParticipantStatus.ACTIVE);
        participant.setCreatedAt(LocalDateTime.now());
        participant.setUpdatedAt(LocalDateTime.now());

        return participantRepository.save(participant);
    }

    /**
     * Update an existing participant
     * @param id the participant ID
     * @param updatedParticipant the updated participant data
     * @return the updated participant
     */
    public Participant updateParticipant(Long id, Participant updatedParticipant) {
        Participant existingParticipant = participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", id));

        // Validate email uniqueness if changed
        if (!existingParticipant.getEmail().equals(updatedParticipant.getEmail())) {
            validateUniqueEmail(updatedParticipant.getEmail());
        }

        // Validate document number uniqueness if changed
        if (!existingParticipant.getDocumentNumber().equals(updatedParticipant.getDocumentNumber())) {
            validateUniqueDocumentNumber(updatedParticipant.getDocumentNumber());
        }

        existingParticipant.setFirstName(updatedParticipant.getFirstName());
        existingParticipant.setLastName(updatedParticipant.getLastName());
        existingParticipant.setEmail(updatedParticipant.getEmail());
        existingParticipant.setPhone(updatedParticipant.getPhone());
        existingParticipant.setDocumentNumber(updatedParticipant.getDocumentNumber());
        existingParticipant.setUpdatedAt(LocalDateTime.now());

        return participantRepository.save(existingParticipant);
    }

    /**
     * Get participant by ID
     * @param id the participant ID
     * @return the participant
     */
    @Transactional(readOnly = true)
    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", id));
    }

    /**
     * Get participant by email
     * @param email the participant email
     * @return the participant
     */
    @Transactional(readOnly = true)
    public Participant getParticipantByEmail(String email) {
        return participantRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Participant", email));
    }

    /**
     * Get all participants
     * @return list of all participants
     */
    @Transactional(readOnly = true)
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    /**
     * Get participants by status
     * @param status the participant status
     * @return list of participants with the given status
     */
    @Transactional(readOnly = true)
    public List<Participant> getParticipantsByStatus(ParticipantStatus status) {
        return participantRepository.findByStatus(status);
    }

    /**
     * Delete a participant
     * @param id the participant ID
     */
    public void deleteParticipant(Long id) {
        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Participant", id);
        }

        long attendanceCount = attendanceRepository.findByParticipantId(id).size();
        if (attendanceCount > 0) {
            throw new BusinessRuleException("Cannot delete a participant with registered attendances");
        }

        participantRepository.deleteById(id);
    }

    /**
     * Validate unique email
     */
    private void validateUniqueEmail(String email) {
        if (participantRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Participant", email);
        }
    }

    /**
     * Validate unique document number
     */
    private void validateUniqueDocumentNumber(String documentNumber) {
        if (participantRepository.existsByDocumentNumber(documentNumber)) {
            throw new DuplicateResourceException("Participant with document number", documentNumber);
        }
    }
}
