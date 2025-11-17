package cue.edu.co.eventcore.e2e;

import cue.edu.co.eventcore.application.dtos.participant.ParticipantRequestDto;
import cue.edu.co.eventcore.config.TestConfig;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@DisplayName("Participant E2E Tests")
@Tag("e2e")
class ParticipantE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        participantRepository.findAll().forEach(p -> participantRepository.deleteById(p.getId()));
    }

    @Test
    @DisplayName("Should create participant via API")
    void shouldCreateParticipantViaApi() throws Exception {
        // Given
        ParticipantRequestDto requestDto = ParticipantRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("1234567890")
                .documentNumber("12345678")
                .build();

        // When & Then
        mockMvc.perform(post("/api/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should get participant by ID via API")
    void shouldGetParticipantByIdViaApi() throws Exception {
        // Given
        ParticipantRequestDto requestDto = createTestParticipantRequest("test@example.com", "11111111");

        String createResponse = mockMvc.perform(post("/api/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long participantId = objectMapper.readTree(createResponse).get("id").asLong();

        // When & Then
        mockMvc.perform(get("/api/participants/" + participantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(participantId))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should get all participants via API")
    void shouldGetAllParticipantsViaApi() throws Exception {
        // Given
        mockMvc.perform(post("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        createTestParticipantRequest("user1@example.com", "11111111"))));

        mockMvc.perform(post("/api/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        createTestParticipantRequest("user2@example.com", "22222222"))));

        // When & Then
        mockMvc.perform(get("/api/participants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("Should validate participant request")
    void shouldValidateParticipantRequest() throws Exception {
        // Given - Invalid email
        ParticipantRequestDto invalidRequest = ParticipantRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .email("invalid-email")
                .phone("1234567890")
                .documentNumber("12345678")
                .build();

        // When & Then
        mockMvc.perform(post("/api/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("Should not allow duplicate email")
    void shouldNotAllowDuplicateEmail() throws Exception {
        // Given
        ParticipantRequestDto participant1 = createTestParticipantRequest("duplicate@example.com", "11111111");
        ParticipantRequestDto participant2 = createTestParticipantRequest("duplicate@example.com", "22222222");

        mockMvc.perform(post("/api/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participant1)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(post("/api/participants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(participant2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    private ParticipantRequestDto createTestParticipantRequest(String email, String documentNumber) {
        return ParticipantRequestDto.builder()
                .firstName("Test")
                .lastName("User")
                .email(email)
                .phone("1234567890")
                .documentNumber(documentNumber)
                .build();
    }
}
