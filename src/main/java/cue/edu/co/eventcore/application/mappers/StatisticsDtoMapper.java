package cue.edu.co.eventcore.application.mappers;

import cue.edu.co.eventcore.application.dtos.statistics.EventStatisticsDto;
import cue.edu.co.eventcore.domain.services.AttendanceService;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert statistics to DTOs
 */
@Component
public class StatisticsDtoMapper {

    public EventStatisticsDto toDto(AttendanceService.EventStatistics statistics) {
        if (statistics == null) {
            return null;
        }

        return EventStatisticsDto.builder()
                .eventId(statistics.getEventId())
                .totalCapacity(statistics.getTotalCapacity())
                .totalRegistered(statistics.getTotalRegistered())
                .totalCheckedIn(statistics.getTotalCheckedIn())
                .totalCancelled(statistics.getTotalCancelled())
                .totalNoShow(statistics.getTotalNoShow())
                .availableSpots(statistics.getAvailableSpots())
                .occupancyPercentage(statistics.getOccupancyPercentage())
                .build();
    }
}
