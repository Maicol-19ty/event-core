package cue.edu.co.eventcore.api.controllers;

import cue.edu.co.eventcore.application.dtos.attendance.AttendanceRequestDto;
import cue.edu.co.eventcore.application.dtos.attendance.AttendanceResponseDto;
import cue.edu.co.eventcore.application.dtos.statistics.EventStatisticsDto;
import cue.edu.co.eventcore.application.mappers.AttendanceDtoMapper;
import cue.edu.co.eventcore.application.mappers.StatisticsDtoMapper;
import cue.edu.co.eventcore.domain.entities.Attendance;
import cue.edu.co.eventcore.domain.services.AttendanceService;
import cue.edu.co.eventcore.infrastructure.cache.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for Attendance management
 */
@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
@Tag(name = "Attendances", description = "Attendance management endpoints")
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceDtoMapper attendanceDtoMapper;
    private final StatisticsDtoMapper statisticsDtoMapper;
    private final CacheService cacheService;

    @PostMapping
    @Operation(summary = "Register participant to event")
    public ResponseEntity<AttendanceResponseDto> registerAttendance(
            @Valid @RequestBody AttendanceRequestDto requestDto) {

        log.info("Registering attendance for event {} and participant {}",
                requestDto.getEventId(), requestDto.getParticipantId());

        Attendance attendance = attendanceService.registerAttendance(
                requestDto.getEventId(),
                requestDto.getParticipantId()
        );

        AttendanceResponseDto responseDto = attendanceDtoMapper.toResponseDto(attendance);

        // Invalidate caches
        cacheService.delete(CacheService.eventKey(requestDto.getEventId()));
        cacheService.delete(CacheService.eventStatsKey(requestDto.getEventId()));
        cacheService.delete(CacheService.eventAvailabilityKey(requestDto.getEventId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/{id}/check-in")
    @Operation(summary = "Check in participant to event")
    public ResponseEntity<AttendanceResponseDto> checkInAttendance(@PathVariable Long id) {
        log.info("Checking in attendance with id: {}", id);

        Attendance attendance = attendanceService.checkInAttendance(id);
        AttendanceResponseDto responseDto = attendanceDtoMapper.toResponseDto(attendance);

        // Invalidate event statistics cache
        cacheService.delete(CacheService.eventStatsKey(attendance.getEventId()));

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel attendance")
    public ResponseEntity<AttendanceResponseDto> cancelAttendance(@PathVariable Long id) {
        log.info("Cancelling attendance with id: {}", id);

        Attendance attendance = attendanceService.cancelAttendance(id);
        AttendanceResponseDto responseDto = attendanceDtoMapper.toResponseDto(attendance);

        // Invalidate caches
        cacheService.delete(CacheService.eventKey(attendance.getEventId()));
        cacheService.delete(CacheService.eventStatsKey(attendance.getEventId()));
        cacheService.delete(CacheService.eventAvailabilityKey(attendance.getEventId()));

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get attendance by ID")
    public ResponseEntity<AttendanceResponseDto> getAttendanceById(@PathVariable Long id) {
        log.info("Getting attendance by id: {}", id);

        Attendance attendance = attendanceService.getAttendanceById(id);
        AttendanceResponseDto responseDto = attendanceDtoMapper.toResponseDto(attendance);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/event/{eventId}")
    @Operation(summary = "Get all attendances for an event")
    public ResponseEntity<List<AttendanceResponseDto>> getAttendancesByEvent(@PathVariable Long eventId) {
        log.info("Getting attendances for event: {}", eventId);

        List<Attendance> attendances = attendanceService.getAttendancesByEvent(eventId);
        List<AttendanceResponseDto> responseDtos = attendances.stream()
                .map(attendanceDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/participant/{participantId}")
    @Operation(summary = "Get all attendances for a participant")
    public ResponseEntity<List<AttendanceResponseDto>> getAttendancesByParticipant(
            @PathVariable Long participantId) {

        log.info("Getting attendances for participant: {}", participantId);

        List<Attendance> attendances = attendanceService.getAttendancesByParticipant(participantId);
        List<AttendanceResponseDto> responseDtos = attendances.stream()
                .map(attendanceDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/event/{eventId}/statistics")
    @Operation(summary = "Get event statistics")
    public ResponseEntity<EventStatisticsDto> getEventStatistics(@PathVariable Long eventId) {
        log.info("Getting statistics for event: {}", eventId);

        // Try to get from cache first
        return cacheService.get(CacheService.eventStatsKey(eventId), EventStatisticsDto.class)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    AttendanceService.EventStatistics statistics = attendanceService.getEventStatistics(eventId);
                    EventStatisticsDto responseDto = statisticsDtoMapper.toDto(statistics);

                    // Store in cache with shorter TTL (5 minutes) since statistics change frequently
                    cacheService.put(CacheService.eventStatsKey(eventId), responseDto, Duration.ofMinutes(5));

                    return ResponseEntity.ok(responseDto);
                });
    }
}
