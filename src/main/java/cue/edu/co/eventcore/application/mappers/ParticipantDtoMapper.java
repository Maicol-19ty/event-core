package cue.edu.co.eventcore.application.mappers;

import cue.edu.co.eventcore.application.dtos.participant.ParticipantRequestDto;
import cue.edu.co.eventcore.application.dtos.participant.ParticipantResponseDto;
import cue.edu.co.eventcore.domain.entities.Participant;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Participant domain entity and DTOs
 */
@Component
public class ParticipantDtoMapper {

    public Participant toEntity(ParticipantRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Participant.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .documentNumber(dto.getDocumentNumber())
                .build();
    }

    public ParticipantResponseDto toResponseDto(Participant entity) {
        if (entity == null) {
            return null;
        }

        return ParticipantResponseDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .documentNumber(entity.getDocumentNumber())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
