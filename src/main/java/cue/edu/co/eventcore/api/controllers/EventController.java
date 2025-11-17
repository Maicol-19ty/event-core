package cue.edu.co.eventcore.api.controllers;

import cue.edu.co.eventcore.application.dtos.event.EventRequestDto;
import cue.edu.co.eventcore.application.dtos.event.EventResponseDto;
import cue.edu.co.eventcore.application.mappers.EventDtoMapper;
import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.EventStatus;
import cue.edu.co.eventcore.domain.services.EventService;
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
 * REST Controller for Event management
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management endpoints")
@Slf4j
public class EventController {

    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;
    private final CacheService cacheService;

    @PostMapping
    @Operation(summary = "Create a new event")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto requestDto) {
        log.info("Creating new event: {}", requestDto.getName());

        Event event = eventDtoMapper.toEntity(requestDto);
        Event createdEvent = eventService.createEvent(event);
        EventResponseDto responseDto = eventDtoMapper.toResponseDto(createdEvent);

        // Invalidate upcoming events cache
        cacheService.delete(CacheService.upcomingEventsKey());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID")
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id) {
        log.info("Getting event by id: {}", id);

        // Try to get from cache first
        return cacheService.get(CacheService.eventKey(id), EventResponseDto.class)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Event event = eventService.getEventById(id);
                    EventResponseDto responseDto = eventDtoMapper.toResponseDto(event);

                    // Store in cache
                    cacheService.put(CacheService.eventKey(id), responseDto);

                    return ResponseEntity.ok(responseDto);
                });
    }

    @GetMapping
    @Operation(summary = "Get all events")
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        log.info("Getting all events");

        List<Event> events = eventService.getAllEvents();
        List<EventResponseDto> responseDtos = events.stream()
                .map(eventDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events")
    public ResponseEntity<List<EventResponseDto>> getUpcomingEvents() {
        log.info("Getting upcoming events");

        List<Event> events = eventService.getUpcomingEvents();
        List<EventResponseDto> responseDtos = events.stream()
                .map(eventDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get events by status")
    public ResponseEntity<List<EventResponseDto>> getEventsByStatus(@PathVariable EventStatus status) {
        log.info("Getting events by status: {}", status);

        List<Event> events = eventService.getEventsByStatus(status);
        List<EventResponseDto> responseDtos = events.stream()
                .map(eventDtoMapper::toResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event")
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDto requestDto) {

        log.info("Updating event with id: {}", id);

        Event event = eventDtoMapper.toEntity(requestDto);
        Event updatedEvent = eventService.updateEvent(id, event);
        EventResponseDto responseDto = eventDtoMapper.toResponseDto(updatedEvent);

        // Invalidate cache
        cacheService.delete(CacheService.eventKey(id));
        cacheService.delete(CacheService.upcomingEventsKey());
        cacheService.delete(CacheService.eventStatsKey(id));

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an event")
    public ResponseEntity<EventResponseDto> cancelEvent(@PathVariable Long id) {
        log.info("Cancelling event with id: {}", id);

        Event cancelledEvent = eventService.cancelEvent(id);
        EventResponseDto responseDto = eventDtoMapper.toResponseDto(cancelledEvent);

        // Invalidate cache
        cacheService.delete(CacheService.eventKey(id));
        cacheService.delete(CacheService.upcomingEventsKey());

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        log.info("Deleting event with id: {}", id);

        eventService.deleteEvent(id);

        // Invalidate cache
        cacheService.delete(CacheService.eventKey(id));
        cacheService.delete(CacheService.upcomingEventsKey());
        cacheService.delete(CacheService.eventStatsKey(id));

        return ResponseEntity.noContent().build();
    }
}
