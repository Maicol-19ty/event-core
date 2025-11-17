package cue.edu.co.eventcore.infrastructure.persistence.jpa;

import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import cue.edu.co.eventcore.infrastructure.persistence.models.ParticipantJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for ParticipantJpaEntity
 */
@Repository
public interface ParticipantJpaRepository extends JpaRepository<ParticipantJpaEntity, Long> {

    Optional<ParticipantJpaEntity> findByEmail(String email);

    Optional<ParticipantJpaEntity> findByDocumentNumber(String documentNumber);

    List<ParticipantJpaEntity> findByStatus(ParticipantStatus status);

    boolean existsByEmail(String email);

    boolean existsByDocumentNumber(String documentNumber);
}
