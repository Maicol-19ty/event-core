package cue.edu.co.eventcore.infrastructure.persistence.repositories;

import cue.edu.co.eventcore.domain.entities.Participant;
import cue.edu.co.eventcore.domain.entities.ParticipantStatus;
import cue.edu.co.eventcore.domain.repositories.ParticipantRepository;
import cue.edu.co.eventcore.infrastructure.persistence.jpa.ParticipantJpaRepository;
import cue.edu.co.eventcore.infrastructure.persistence.mappers.ParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ParticipantRepository interface
 * Adapts Spring Data JPA repository to domain repository interface
 */
@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepository {

    private final ParticipantJpaRepository jpaRepository;
    private final ParticipantMapper mapper;

    @Override
    public Participant save(Participant participant) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(participant)));
    }

    @Override
    public Optional<Participant> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Participant> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<Participant> findByDocumentNumber(String documentNumber) {
        return jpaRepository.findByDocumentNumber(documentNumber).map(mapper::toDomain);
    }

    @Override
    public List<Participant> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Participant> findByStatus(ParticipantStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return jpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
