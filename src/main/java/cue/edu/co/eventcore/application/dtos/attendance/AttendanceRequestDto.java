package cue.edu.co.eventcore.application.dtos.attendance;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Attendance registration requests
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestDto {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Participant ID is required")
    private Long participantId;

    private String notes;
}
