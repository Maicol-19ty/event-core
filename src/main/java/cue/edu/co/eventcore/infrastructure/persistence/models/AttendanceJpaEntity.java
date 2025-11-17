package cue.edu.co.eventcore.infrastructure.persistence.models;

import cue.edu.co.eventcore.domain.entities.AttendanceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity for Attendance
 * This is the persistence model, separate from domain model
 */
@Entity
@Table(name = "attendances", indexes = {
        @Index(name = "idx_event_participant", columnList = "event_id, participant_id", unique = true),
        @Index(name = "idx_event_id", columnList = "event_id"),
        @Index(name = "idx_participant_id", columnList = "participant_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "participant_id", nullable = false)
    private Long participantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
