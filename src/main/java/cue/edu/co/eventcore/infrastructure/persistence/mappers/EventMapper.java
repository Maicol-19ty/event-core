package cue.edu.co.eventcore.infrastructure.persistence.mappers;

import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.infrastructure.persistence.models.EventJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Event domain entity and EventJpaEntity
 */
@Component
public class EventMapper {

    public Event toDomain(EventJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Event.builder()
                .id(jpaEntity.getId())
                .name(jpaEntity.getName())
                .description(jpaEntity.getDescription())
                .location(jpaEntity.getLocation())
                .startDate(jpaEntity.getStartDate())
                .endDate(jpaEntity.getEndDate())
                .capacity(jpaEntity.getCapacity())
                .currentAttendees(jpaEntity.getCurrentAttendees())
                .status(jpaEntity.getStatus())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }

    public EventJpaEntity toJpaEntity(Event domain) {
        if (domain == null) {
            return null;
        }

        return EventJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .location(domain.getLocation())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .capacity(domain.getCapacity())
                .currentAttendees(domain.getCurrentAttendees())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
