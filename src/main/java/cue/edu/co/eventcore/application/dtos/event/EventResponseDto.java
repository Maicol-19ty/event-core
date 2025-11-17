package cue.edu.co.eventcore.application.dtos.event;

import cue.edu.co.eventcore.domain.entities.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Event responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

    private Long id;
    private String name;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer capacity;
    private Integer currentAttendees;
    private Integer availableSpots;
    private EventStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
