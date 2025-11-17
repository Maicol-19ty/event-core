package cue.edu.co.eventcore.api.controllers;

import cue.edu.co.eventcore.application.dtos.participant.ParticipantRequestDto;
import cue.edu.co.eventcore.application.dtos.participant.ParticipantResponseDto;
import cue.edu.co.eventcore.application.mappers.ParticipantDtoMapper;
import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import cue.edu.co.eventcore.domain.services.ParticipantService;
import cue.edu.co.eventcore.infrastructure.cache.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Participant management
 */
@RestController
@RequestMapping("/participants")
@RequiredArgsConstructor
@Tag(name = "Participants", description = "Participant management endpoints")
@Slf4j
public class ParticipantController {

    private final ParticipantService participantService;
    private final ParticipantDtoMapper participantDtoMapper;
    private final CacheService cacheService;

    @PostMapping
    @Operation(summary = "Create a new participant")
    public ResponseEntity<ParticipantResponseDto> createParticipant(
            @Valid @RequestBody ParticipantRequestDto requestDto) {

        log.info("Creating new participant: {}", requestDto.getEmail());

        Participant participant = participantDtoMapper.toEntity(requestDto);
        Participant createdParticipant = participantService.createParticipant(participant);
        ParticipantResponseDto responseDto = participantDtoMapper.toResponseDto(createdParticipant);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get participant by ID")
    public ResponseEntity<ParticipantResponseDto> getParticipantById(@PathVariable Long id) {
        log.info("Getting participant by id: {}", id);

        // Try to get from cache first
        return cacheService.get(CacheService.participantKey(id), ParticipantResponseDto.class)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Participant participant = participantService.getParticipantById(id);
                    ParticipantResponseDto responseDto = participantDtoMapper.toResponseDto(participant);

                    // Store in cache
                    cacheService.put(CacheService.participantKey(id), responseDto);

                    return ResponseEntity.ok(responseDto);
                });
    }

    @GetMapping
    @Operation(summary = "Get all participants")
    public ResponseEntity<List<ParticipantResponseDto>> getAllParticipants() {
        log.info("Getting all participants");

        List<Participant> participants = participantService.getAllParticipants();
        List<ParticipantResponseDto> responseDtos = participants.stream()
                .map(participantDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get participants by status")
    public ResponseEntity<List<ParticipantResponseDto>> getParticipantsByStatus(
            @PathVariable ParticipantStatus status) {

        log.info("Getting participants by status: {}", status);

        List<Participant> participants = participantService.getParticipantsByStatus(status);
        List<ParticipantResponseDto> responseDtos = participants.stream()
                .map(participantDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get participant by email")
    public ResponseEntity<ParticipantResponseDto> getParticipantByEmail(@PathVariable String email) {
        log.info("Getting participant by email: {}", email);

        Participant participant = participantService.getParticipantByEmail(email);
        ParticipantResponseDto responseDto = participantDtoMapper.toResponseDto(participant);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a participant")
    public ResponseEntity<ParticipantResponseDto> updateParticipant(
            @PathVariable Long id,
            @Valid @RequestBody ParticipantRequestDto requestDto) {

        log.info("Updating participant with id: {}", id);

        Participant participant = participantDtoMapper.toEntity(requestDto);
        Participant updatedParticipant = participantService.updateParticipant(id, participant);
        ParticipantResponseDto responseDto = participantDtoMapper.toResponseDto(updatedParticipant);

        // Invalidate cache
        cacheService.delete(CacheService.participantKey(id));

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a participant")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        log.info("Deleting participant with id: {}", id);

        participantService.deleteParticipant(id);

        // Invalidate cache
        cacheService.delete(CacheService.participantKey(id));

        return ResponseEntity.noContent().build();
    }
}
