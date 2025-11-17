package cue.edu.co.eventcore.domain.services;

import cue.edu.co.eventcore.domain.entities.*;
import cue.edu.co.eventcore.domain.exceptions.BusinessRuleException;
import cue.edu.co.eventcore.domain.exceptions.DuplicateResourceException;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService Unit Tests")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Event testEvent;
    private Participant testParticipant;
    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        testEvent = Event.builder()
                .id(1L)
                .name("Test Event")
                .capacity(100)
                .currentAttendees(50)
                .status(EventStatus.ACTIVE)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        testParticipant = Participant.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .status(ParticipantStatus.ACTIVE)
                .build();

        testAttendance = Attendance.builder()
                .id(1L)
                .eventId(1L)
                .participantId(1L)
                .status(AttendanceStatus.REGISTERED)
                .registrationDate(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should register attendance successfully")
    void shouldRegisterAttendanceSuccessfully() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(participantRepository.findById(1L)).thenReturn(Optional.of(testParticipant));
        when(attendanceRepository.existsByEventIdAndParticipantId(1L, 1L)).thenReturn(false);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(testAttendance);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Attendance result = attendanceService.registerAttendance(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(AttendanceStatus.REGISTERED);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should throw exception when event is full")
    void shouldThrowExceptionWhenEventIsFull() {
        // Given
        Event fullEvent = Event.builder()
                .id(1L)
                .capacity(100)
                .currentAttendees(100)
                .status(EventStatus.ACTIVE)
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(fullEvent));
        when(participantRepository.findById(1L)).thenReturn(Optional.of(testParticipant));
        when(attendanceRepository.existsByEventIdAndParticipantId(1L, 1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> attendanceService.registerAttendance(1L, 1L))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("maximum capacity");

        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw exception when participant already registered")
    void shouldThrowExceptionWhenParticipantAlreadyRegistered() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(participantRepository.findById(1L)).thenReturn(Optional.of(testParticipant));
        when(attendanceRepository.existsByEventIdAndParticipantId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> attendanceService.registerAttendance(1L, 1L))
                .isInstanceOf(DuplicateResourceException.class);

        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should check in attendance successfully")
    void shouldCheckInAttendanceSuccessfully() {
        // Given
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(testAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(testAttendance);

        // When
        Attendance result = attendanceService.checkInAttendance(1L);

        // Then
        assertThat(result).isNotNull();
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should cancel attendance successfully")
    void shouldCancelAttendanceSuccessfully() {
        // Given
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(testAttendance));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(testAttendance);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // When
        Attendance result = attendanceService.cancelAttendance(1L);

        // Then
        assertThat(result).isNotNull();
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Should get event statistics")
    void shouldGetEventStatistics() {
        // Given
        when(eventRepository.existsById(1L)).thenReturn(true);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(attendanceRepository.countByEventIdAndStatus(eq(1L), any())).thenReturn(10L);

        // When
        AttendanceService.EventStatistics result = attendanceService.getEventStatistics(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventId()).isEqualTo(1L);
        assertThat(result.getTotalCapacity()).isEqualTo(100);
    }
}
