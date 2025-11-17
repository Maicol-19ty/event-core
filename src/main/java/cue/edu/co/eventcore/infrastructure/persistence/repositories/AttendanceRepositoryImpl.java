package cue.edu.co.eventcore.infrastructure.persistence.repositories;

import cue.edu.co.eventcore.domain.entities.Attendance;
import cue.edu.co.eventcore.domain.entities.AttendanceStatus;
import cue.edu.co.eventcore.domain.repositories.AttendanceRepository;
import cue.edu.co.eventcore.infrastructure.persistence.jpa.AttendanceJpaRepository;
import cue.edu.co.eventcore.infrastructure.persistence.mappers.AttendanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of AttendanceRepository interface
 * Adapts Spring Data JPA repository to domain repository interface
 */
@Repository
@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepository {

    private final AttendanceJpaRepository jpaRepository;
    private final AttendanceMapper mapper;

    @Override
    public Attendance save(Attendance attendance) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(attendance)));
    }

    @Override
    public Optional<Attendance> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Attendance> findByEventIdAndParticipantId(Long eventId, Long participantId) {
        return jpaRepository.findByEventIdAndParticipantId(eventId, participantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Attendance> findByEventId(Long eventId) {
        return jpaRepository.findByEventId(eventId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByParticipantId(Long participantId) {
        return jpaRepository.findByParticipantId(participantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> findByEventIdAndStatus(Long eventId, AttendanceStatus status) {
        return jpaRepository.findByEventIdAndStatus(eventId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEventIdAndParticipantId(Long eventId, Long participantId) {
        return jpaRepository.existsByEventIdAndParticipantId(eventId, participantId);
    }

    @Override
    public long countByEventId(Long eventId) {
        return jpaRepository.countByEventId(eventId);
    }

    @Override
    public long countByEventIdAndStatus(Long eventId, AttendanceStatus status) {
        return jpaRepository.countByEventIdAndStatus(eventId, status);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void deleteByEventId(Long eventId) {
        jpaRepository.deleteByEventId(eventId);
    }

    @Override
    public List<Attendance> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
