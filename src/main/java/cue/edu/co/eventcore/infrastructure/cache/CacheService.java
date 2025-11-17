package cue.edu.co.eventcore.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Service for caching operations using Redis
 * Provides methods to store and retrieve cached data
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    /**
     * Store a value in cache
     * @param key the cache key
     * @param value the value to cache
     * @param ttl time to live
     */
    public <T> void put(String key, T value, Duration ttl) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl);
            log.debug("Cached value with key: {}", key);
        } catch (JsonProcessingException e) {
            log.error("Error serializing value for cache key: {}", key, e);
        }
    }

    /**
     * Store a value in cache with default TTL
     * @param key the cache key
     * @param value the value to cache
     */
    public <T> void put(String key, T value) {
        put(key, value, DEFAULT_TTL);
    }

    /**
     * Get a value from cache
     * @param key the cache key
     * @param clazz the class type of the cached value
     * @return Optional containing the value if found
     */
    public <T> Optional<T> get(String key, Class<T> clazz) {
        try {
            String jsonValue = redisTemplate.opsForValue().get(key);
            if (jsonValue == null) {
                log.debug("Cache miss for key: {}", key);
                return Optional.empty();
            }
            T value = objectMapper.readValue(jsonValue, clazz);
            log.debug("Cache hit for key: {}", key);
            return Optional.of(value);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing value for cache key: {}", key, e);
            return Optional.empty();
        }
    }

    /**
     * Delete a value from cache
     * @param key the cache key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("Deleted cache key: {}", key);
    }

    /**
     * Delete all keys matching a pattern
     * @param pattern the key pattern (e.g., "event:*")
     */
    public void deletePattern(String pattern) {
        var keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("Deleted {} keys matching pattern: {}", keys.size(), pattern);
        }
    }

    /**
     * Check if a key exists in cache
     * @param key the cache key
     * @return true if key exists
     */
    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }

    /**
     * Generate cache key for event
     */
    public static String eventKey(Long eventId) {
        return "event:" + eventId;
    }

    /**
     * Generate cache key for event statistics
     */
    public static String eventStatsKey(Long eventId) {
        return "event:stats:" + eventId;
    }

    /**
     * Generate cache key for participant
     */
    public static String participantKey(Long participantId) {
        return "participant:" + participantId;
    }

    /**
     * Generate cache key for event availability check
     */
    public static String eventAvailabilityKey(Long eventId) {
        return "event:availability:" + eventId;
    }

    /**
     * Generate cache key for upcoming events
     */
    public static String upcomingEventsKey() {
        return "events:upcoming";
    }
}
