package cue.edu.co.eventcore.infrastructure.persistence.repositories;

import cue.edu.co.eventcore.domain.entities.Event;
import cue.edu.co.eventcore.domain.entities.EventStatus;
import cue.edu.co.eventcore.domain.repositories.EventRepository;
import cue.edu.co.eventcore.infrastructure.persistence.jpa.EventJpaRepository;
import cue.edu.co.eventcore.infrastructure.persistence.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of EventRepository interface
 * Adapts Spring Data JPA repository to domain repository interface
 */
@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository jpaRepository;
    private final EventMapper mapper;

    @Override
    public Event save(Event event) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(event)));
    }

    @Override
    public Optional<Event> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByStatus(EventStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByDateRange(startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> findUpcomingEvents() {
        return jpaRepository.findUpcomingEvents(LocalDateTime.now()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
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
