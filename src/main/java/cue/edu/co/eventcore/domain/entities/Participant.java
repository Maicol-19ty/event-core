package cue.edu.co.eventcore.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Domain entity representing a Participant
 * This is a pure domain object with no framework dependencies
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String documentNumber;
    private ParticipantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Get full name
     * @return full name of participant
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Business logic: Check if participant is active
     * @return true if participant is active
     */
    public boolean isActive() {
        return ParticipantStatus.ACTIVE.equals(status);
    }

    /**
     * Business logic: Activate participant
     */
    public void activate() {
        this.status = ParticipantStatus.ACTIVE;
    }

    /**
     * Business logic: Deactivate participant
     */
    public void deactivate() {
        this.status = ParticipantStatus.INACTIVE;
    }
}
