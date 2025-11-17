package cue.edu.co.eventcore.application.dtos.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Event Statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStatisticsDto {

    private Long eventId;
    private Integer totalCapacity;
    private Long totalRegistered;
    private Long totalCheckedIn;
    private Long totalCancelled;
    private Long totalNoShow;
    private Integer availableSpots;
    private Double occupancyPercentage;
}
