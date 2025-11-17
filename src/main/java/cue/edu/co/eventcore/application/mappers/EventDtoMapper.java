package cue.edu.co.eventcore.application.mappers;

import cue.edu.co.eventcore.application.dtos.event.EventRequestDto;
import cue.edu.co.eventcore.application.dtos.event.EventResponseDto;
import cue.edu.co.eventcore.domain.entities.Event;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Event domain entity and DTOs
 */
@Component
public class EventDtoMapper {

    public Event toEntity(EventRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .capacity(dto.getCapacity())
                .build();
    }

    public EventResponseDto toResponseDto(Event entity) {
        if (entity == null) {
            return null;
        }

        return EventResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .location(entity.getLocation())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .capacity(entity.getCapacity())
                .currentAttendees(entity.getCurrentAttendees())
                .availableSpots(entity.getRemainingCapacity())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
