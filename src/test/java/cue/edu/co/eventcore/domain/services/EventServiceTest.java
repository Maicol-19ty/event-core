package cue.edu.co.eventcore.domain.services;

import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.EventStatus;
import cue.edu.co.eventcore.domain.exceptions.BusinessRuleException;
import cue.edu.co.eventcore.domain.exceptions.ResourceNotFoundException;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Unit Tests")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
                .id(1L)
                .name("Test Event")
                .description("Test Description")
                .location("Test Location")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .currentAttendees(0)
                .status(EventStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should create event successfully")
    void shouldCreateEventSuccessfully() {
        // Given
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.createEvent(testEvent);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Event");
        assertThat(result.getStatus()).isEqualTo(EventStatus.ACTIVE);
        assertThat(result.getCurrentAttendees()).isZero();
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should throw exception when event dates are invalid")
    void shouldThrowExceptionWhenEventDatesAreInvalid() {
        // Given
        Event invalidEvent = Event.builder()
                .name("Invalid Event")
                .startDate(LocalDateTime.now().plusDays(2))
                .endDate(LocalDateTime.now().plusDays(1)) // End before start
                .capacity(100)
                .build();

        // When & Then
        assertThatThrownBy(() -> eventService.createEvent(invalidEvent))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("end date must be after start date");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Should get event by id")
    void shouldGetEventById() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        Event result = eventService.getEventById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when event not found")
    void shouldThrowExceptionWhenEventNotFound() {
        // Given
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventService.getEventById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event");

        verify(eventRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get all events")
    void shouldGetAllEvents() {
        // Given
        List<Event> events = Arrays.asList(testEvent, testEvent);
        when(eventRepository.findAll()).thenReturn(events);

        // When
        List<Event> result = eventService.getAllEvents();

        // Then
        assertThat(result).hasSize(2);
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update event successfully")
    void shouldUpdateEventSuccessfully() {
        // Given
        Event updatedData = Event.builder()
                .name("Updated Event")
                .description("Updated Description")
                .location("Updated Location")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(150)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.updateEvent(1L, updatedData);

        // Then
        assertThat(result).isNotNull();
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should cancel event successfully")
    void shouldCancelEventSuccessfully() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Event result = eventService.cancelEvent(1L);

        // Then
        assertThat(result).isNotNull();
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should delete event when no attendances exist")
    void shouldDeleteEventWhenNoAttendancesExist() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(attendanceRepository.countByEventId(1L)).thenReturn(0L);

        // When
        eventService.deleteEvent(1L);

        // Then
        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting event with attendances")
    void shouldThrowExceptionWhenDeletingEventWithAttendances() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(attendanceRepository.countByEventId(1L)).thenReturn(5L);

        // When & Then
        assertThatThrownBy(() -> eventService.deleteEvent(1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot delete an event with registered attendances");

        verify(eventRepository, never()).deleteById(anyLong());
    }
}
