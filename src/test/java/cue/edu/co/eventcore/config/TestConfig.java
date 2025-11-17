package cue.edu.co.eventcore.config;

import cue.edu.co.eventcore.infrastructure.cache.CacheService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

/**
 * Test configuration to provide mock beans for testing
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> mock = Mockito.mock(RedisTemplate.class);
        return mock;
    }

    @Bean
    @Primary
    public CacheService cacheService() {
        CacheService mock = Mockito.mock(CacheService.class);

        // Configure mock to do nothing on put operations
        Mockito.doNothing().when(mock).put(anyString(), any());
        Mockito.doNothing().when(mock).put(anyString(), any(), any(Duration.class));
        Mockito.doNothing().when(mock).delete(anyString());
        Mockito.doNothing().when(mock).deletePattern(anyString());

        // Configure mock to return empty Optional on get operations
        Mockito.when(mock.get(anyString(), any())).thenReturn(Optional.empty());
        Mockito.when(mock.exists(anyString())).thenReturn(false);

        return mock;
    }
}
