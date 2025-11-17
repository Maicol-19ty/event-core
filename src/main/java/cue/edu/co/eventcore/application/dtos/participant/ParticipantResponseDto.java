package cue.edu.co.eventcore.application.dtos.participant;

import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Participant responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String documentNumber;
    private ParticipantStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
