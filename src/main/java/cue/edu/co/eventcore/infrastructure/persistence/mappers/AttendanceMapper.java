package cue.edu.co.eventcore.infrastructure.persistence.mappers;

import cue.edu.co.eventcore.domain.entities.Attendance;
import cue.edu.co.eventcore.infrastructure.persistence.models.AttendanceJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Attendance domain entity and AttendanceJpaEntity
 */
@Component
public class AttendanceMapper {

    public Attendance toDomain(AttendanceJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Attendance.builder()
                .id(jpaEntity.getId())
                .eventId(jpaEntity.getEventId())
                .participantId(jpaEntity.getParticipantId())
                .status(jpaEntity.getStatus())
                .registrationDate(jpaEntity.getRegistrationDate())
                .checkInDate(jpaEntity.getCheckInDate())
                .notes(jpaEntity.getNotes())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }

    public AttendanceJpaEntity toJpaEntity(Attendance domain) {
        if (domain == null) {
            return null;
        }

        return AttendanceJpaEntity.builder()
                .id(domain.getId())
                .eventId(domain.getEventId())
                .participantId(domain.getParticipantId())
                .status(domain.getStatus())
                .registrationDate(domain.getRegistrationDate())
                .checkInDate(domain.getCheckInDate())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
