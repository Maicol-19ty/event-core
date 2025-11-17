package cue.edu.co.eventcore.infrastructure.persistence.jpa;

import cue.edu.co.eventcore.domain.entities.AttendanceStatus;
import cue.edu.co.eventcore.infrastructure.persistence.models.AttendanceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for AttendanceJpaEntity
 */
@Repository
public interface AttendanceJpaRepository extends JpaRepository<AttendanceJpaEntity, Long> {

    Optional<AttendanceJpaEntity> findByEventIdAndParticipantId(Long eventId, Long participantId);

    List<AttendanceJpaEntity> findByEventId(Long eventId);

    List<AttendanceJpaEntity> findByParticipantId(Long participantId);

    List<AttendanceJpaEntity> findByEventIdAndStatus(Long eventId, AttendanceStatus status);

    boolean existsByEventIdAndParticipantId(Long eventId, Long participantId);

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, AttendanceStatus status);

    void deleteByEventId(Long eventId);
}
