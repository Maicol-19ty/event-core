package cue.edu.co.eventcore.integration;

import cue.edu.co.eventcore.config.TestConfig;
import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import cue.edu.co.eventcore.domain.services.ParticipantService;
import cue.edu.co.eventcore.domain.exceptions.DuplicateResourceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@DisplayName("Participant Integration Tests")
class ParticipantIntegrationTest {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    @DisplayName("Should create and retrieve participant")
    void shouldCreateAndRetrieveParticipant() {
        // Given
        Participant participant = Participant.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .documentNumber("12345678")
                .build();

        // When
        Participant createdParticipant = participantService.createParticipant(participant);

        // Then
        assertThat(createdParticipant).isNotNull();
        assertThat(createdParticipant.getId()).isNotNull();

        Participant retrievedParticipant = participantService.getParticipantById(createdParticipant.getId());
        assertThat(retrievedParticipant.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(retrievedParticipant.getStatus()).isEqualTo(ParticipantStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should not allow duplicate email")
    void shouldNotAllowDuplicateEmail() {
        // Given
        Participant participant1 = createTestParticipant("john@example.com", "12345678");
        participantService.createParticipant(participant1);

        Participant participant2 = createTestParticipant("john@example.com", "87654321");

        // When & Then
        assertThatThrownBy(() -> participantService.createParticipant(participant2))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("Should find participant by email")
    void shouldFindParticipantByEmail() {
        // Given
        Participant participant = createTestParticipant("jane@example.com", "11111111");
        participantService.createParticipant(participant);

        // When
        Participant found = participantService.getParticipantByEmail("jane@example.com");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("Should get all participants")
    void shouldGetAllParticipants() {
        // Given
        participantService.createParticipant(createTestParticipant("user1@example.com", "11111111"));
        participantService.createParticipant(createTestParticipant("user2@example.com", "22222222"));

        // When
        List<Participant> participants = participantService.getAllParticipants();

        // Then
        assertThat(participants).hasSizeGreaterThanOrEqualTo(2);
    }

    private Participant createTestParticipant(String email, String documentNumber) {
        return Participant.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .phone("1234567890")
                .documentNumber(documentNumber)
                .build();
    }
}
