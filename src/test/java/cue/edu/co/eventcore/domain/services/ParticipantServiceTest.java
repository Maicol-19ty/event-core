package cue.edu.co.eventcore.domain.services;

import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import cue.edu.co.eventcore.domain.exceptions.DuplicateResourceException;
import cue.edu.co.eventcore.domain.exceptions.ResourceNotFoundException;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParticipantService Unit Tests")
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private ParticipantService participantService;

    private Participant testParticipant;

    @BeforeEach
    void setUp() {
        testParticipant = Participant.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .documentNumber("12345678")
                .status(ParticipantStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should create participant successfully")
    void shouldCreateParticipantSuccessfully() {
        // Given
        when(participantRepository.existsByEmail(anyString())).thenReturn(false);
        when(participantRepository.existsByDocumentNumber(anyString())).thenReturn(false);
        when(participantRepository.save(any(Participant.class))).thenReturn(testParticipant);

        // When
        Participant result = participantService.createParticipant(testParticipant);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getStatus()).isEqualTo(ParticipantStatus.ACTIVE);
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(participantRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> participantService.createParticipant(testParticipant))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Participant");

        verify(participantRepository, never()).save(any(Participant.class));
    }

    @Test
    @DisplayName("Should get participant by id")
    void shouldGetParticipantById() {
        // Given
        when(participantRepository.findById(1L)).thenReturn(Optional.of(testParticipant));

        // When
        Participant result = participantService.getParticipantById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(participantRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should get participant by email")
    void shouldGetParticipantByEmail() {
        // Given
        when(participantRepository.findByEmail(anyString())).thenReturn(Optional.of(testParticipant));

        // When
        Participant result = participantService.getParticipantByEmail("john.doe@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(participantRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should get all participants")
    void shouldGetAllParticipants() {
        // Given
        List<Participant> participants = Arrays.asList(testParticipant, testParticipant);
        when(participantRepository.findAll()).thenReturn(participants);

        // When
        List<Participant> result = participantService.getAllParticipants();

        // Then
        assertThat(result).hasSize(2);
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update participant successfully")
    void shouldUpdateParticipantSuccessfully() {
        // Given
        Participant updatedData = Participant.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .phone("9876543210")
                .documentNumber("12345678")
                .build();

        when(participantRepository.findById(1L)).thenReturn(Optional.of(testParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(testParticipant);

        // When
        Participant result = participantService.updateParticipant(1L, updatedData);

        // Then
        assertThat(result).isNotNull();
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    @Test
    @DisplayName("Should delete participant when no attendances exist")
    void shouldDeleteParticipantWhenNoAttendancesExist() {
        // Given
        when(participantRepository.existsById(1L)).thenReturn(true);
        when(attendanceRepository.findByParticipantId(1L)).thenReturn(List.of());

        // When
        participantService.deleteParticipant(1L);

        // Then
        verify(participantRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when participant not found")
    void shouldThrowExceptionWhenParticipantNotFound() {
        // Given
        when(participantRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> participantService.getParticipantById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Participant");
    }
}
