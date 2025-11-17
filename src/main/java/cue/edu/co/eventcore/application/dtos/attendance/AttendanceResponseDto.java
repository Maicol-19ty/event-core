package cue.edu.co.eventcore.application.dtos.attendance;

import cue.edu.co.eventcore.domain.entities.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Attendance responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDto {

    private Long id;
    private Long eventId;
    private Long participantId;
    private AttendanceStatus status;
    private LocalDateTime registrationDate;
    private LocalDateTime checkInDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
