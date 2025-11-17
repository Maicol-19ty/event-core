package cue.edu.co.eventcore.infrastructure.persistence.jpa;

import cue.edu.co.eventcore.domain.entities.EventStatus;
import cue.edu.co.eventcore.infrastructure.persistence.models.EventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA Repository for EventJpaEntity
 */
@Repository
public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {

    List<EventJpaEntity> findByStatus(EventStatus status);

    @Query("SELECT e FROM EventJpaEntity e WHERE e.startDate BETWEEN :startDate AND :endDate")
    List<EventJpaEntity> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM EventJpaEntity e WHERE e.startDate > :now AND e.status = 'ACTIVE' ORDER BY e.startDate ASC")
    List<EventJpaEntity> findUpcomingEvents(@Param("now") LocalDateTime now);
}
