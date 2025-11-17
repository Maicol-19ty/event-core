package cue.edu.co.eventcore.e2e;

import cue.edu.co.eventcore.application.dtos.event.EventRequestDto;
import cue.edu.co.eventcore.config.TestConfig;
import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@DisplayName("Event E2E Tests")
@Tag("e2e")
class EventE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        eventRepository.findAll().forEach(event -> eventRepository.deleteById(event.getId()));
    }

    @Test
    @DisplayName("Should create event via API")
    void shouldCreateEventViaApi() throws Exception {
        // Given
        EventRequestDto requestDto = EventRequestDto.builder()
                .name("API Test Event")
                .description("Test Description")
                .location("Test Location")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .build();

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("API Test Event"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.currentAttendees").value(0));
    }

    @Test
    @DisplayName("Should get event by ID via API")
    void shouldGetEventByIdViaApi() throws Exception {
        // Given - Create event first
        EventRequestDto requestDto = createTestEventRequest("Get Test Event");

        String createResponse = mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long eventId = objectMapper.readTree(createResponse).get("id").asLong();

        // When & Then
        mockMvc.perform(get("/api/events/" + eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.name").value("Get Test Event"));
    }

    @Test
    @DisplayName("Should get all events via API")
    void shouldGetAllEventsViaApi() throws Exception {
        // Given - Create two events
        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTestEventRequest("Event 1"))));

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTestEventRequest("Event 2"))));

        // When & Then
        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("Should return 404 when event not found")
    void shouldReturn404WhenEventNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/events/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Should validate event request")
    void shouldValidateEventRequest() throws Exception {
        // Given - Invalid request (no name)
        EventRequestDto invalidRequest = EventRequestDto.builder()
                .description("Test")
                .location("Test")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .build();

        // When & Then
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    private EventRequestDto createTestEventRequest(String name) {
        return EventRequestDto.builder()
                .name(name)
                .description("Test Description")
                .location("Test Location")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .build();
    }
}
