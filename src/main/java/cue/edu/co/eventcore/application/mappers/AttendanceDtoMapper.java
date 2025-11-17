package cue.edu.co.eventcore.application.mappers;

import cue.edu.co.eventcore.application.dtos.attendance.AttendanceResponseDto;
import cue.edu.co.eventcore.domain.entities.Attendance;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert between Attendance domain entity and DTOs
 */
@Component
public class AttendanceDtoMapper {

    public AttendanceResponseDto toResponseDto(Attendance entity) {
        if (entity == null) {
            return null;
        }

        return AttendanceResponseDto.builder()
                .id(entity.getId())
                .eventId(entity.getEventId())
                .participantId(entity.getParticipantId())
                .status(entity.getStatus())
                .registrationDate(entity.getRegistrationDate())
                .checkInDate(entity.getCheckInDate())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
