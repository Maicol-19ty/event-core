package cue.edu.co.eventcore.infrastructure.persistence.mappers;

import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.infrastructure.persistence.models.ParticipantJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Participant domain entity and ParticipantJpaEntity
 */
@Component
public class ParticipantMapper {

    public Participant toDomain(ParticipantJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Participant.builder()
                .id(jpaEntity.getId())
                .firstName(jpaEntity.getFirstName())
                .lastName(jpaEntity.getLastName())
                .email(jpaEntity.getEmail())
                .phone(jpaEntity.getPhone())
                .documentNumber(jpaEntity.getDocumentNumber())
                .status(jpaEntity.getStatus())
                .createdAt(jpaEntity.getCreatedAt())
                .updatedAt(jpaEntity.getUpdatedAt())
                .build();
    }

    public ParticipantJpaEntity toJpaEntity(Participant domain) {
        if (domain == null) {
            return null;
        }

        return ParticipantJpaEntity.builder()
                .id(domain.getId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .documentNumber(domain.getDocumentNumber())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
