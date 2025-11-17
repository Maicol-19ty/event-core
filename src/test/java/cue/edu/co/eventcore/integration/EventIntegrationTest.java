package cue.edu.co.eventcore.integration;

import cue.edu.co.eventcore.config.TestConfig;
import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.EventStatus;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
import cue.edu.co.eventcore.domain.services.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@Transactional
@DisplayName("Event Integration Tests")
class EventIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("Should create and retrieve event")
    void shouldCreateAndRetrieveEvent() {
        // Given
        Event event = Event.builder()
                .name("Integration Test Event")
                .description("Test Description")
                .location("Test Location")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(100)
                .build();

        // When
        Event createdEvent = eventService.createEvent(event);

        // Then
        assertThat(createdEvent).isNotNull();
        assertThat(createdEvent.getId()).isNotNull();

        Event retrievedEvent = eventService.getEventById(createdEvent.getId());
        assertThat(retrievedEvent.getName()).isEqualTo("Integration Test Event");
        assertThat(retrievedEvent.getStatus()).isEqualTo(EventStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find upcoming events")
    void shouldFindUpcomingEvents() {
        // Given
        Event event1 = createTestEvent("Event 1", 1, 2);
        Event event2 = createTestEvent("Event 2", 3, 4);

        eventService.createEvent(event1);
        eventService.createEvent(event2);

        // When
        List<Event> upcomingEvents = eventService.getUpcomingEvents();

        // Then
        assertThat(upcomingEvents).isNotEmpty();
    }

    @Test
    @DisplayName("Should update event successfully")
    void shouldUpdateEventSuccessfully() {
        // Given
        Event event = createTestEvent("Original Event", 1, 2);
        Event createdEvent = eventService.createEvent(event);

        // When
        Event updateData = Event.builder()
                .name("Updated Event")
                .description("Updated Description")
                .location("Updated Location")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .capacity(200)
                .build();

        Event updatedEvent = eventService.updateEvent(createdEvent.getId(), updateData);

        // Then
        assertThat(updatedEvent.getName()).isEqualTo("Updated Event");
        assertThat(updatedEvent.getCapacity()).isEqualTo(200);
    }

    private Event createTestEvent(String name, int startDaysFromNow, int endDaysFromNow) {
        return Event.builder()
                .name(name)
                .description("Test Description")
                .location("Test Location")
                .startDate(LocalDateTime.now().plusDays(startDaysFromNow))
                .endDate(LocalDateTime.now().plusDays(endDaysFromNow))
                .capacity(100)
                .build();
    }
}
