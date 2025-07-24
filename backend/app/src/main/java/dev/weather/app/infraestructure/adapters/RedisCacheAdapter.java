package dev.weather.app.infraestructure.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.weather.app.domain.ports.out.CachePort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

@Component
public class RedisCacheAdapter<T> implements CachePort<T> {
    private static final Logger log = LoggerFactory.getLogger(RedisCacheAdapter.class);

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheAdapter(
            @Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate, 
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<T> get(String key, Class<T> type) {
        log.debug("Fetching from cache with key: {}", key);
        return redisTemplate.opsForValue().get(key)
                .doOnNext(value -> log.debug("Cache hit for key: {}", key))
                .doOnError(e -> log.error("Error fetching from cache for key: " + key, e))
                .flatMap(value -> {
                    try {
                        T result = objectMapper.readValue(value, type);
                        return Mono.just(result);
                    } catch (JsonProcessingException e) {
                        log.error("Error deserializing cached value for key: " + key, e);
                        return Mono.error(e);
                    }
                })
                .doOnError(e -> log.debug("Cache miss for key: {}", key));
    }

    @Override
    public Mono<Boolean> put(String key, T value, long ttlInSeconds) {
        log.debug("Caching value with key: {} for {} seconds", key, ttlInSeconds);
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            return redisTemplate.opsForValue()
                    .set(key, jsonValue, Duration.ofSeconds(ttlInSeconds))
                    .doOnSuccess(success -> {
                        if (success) {
                            log.debug("Successfully cached value for key: {}", key);
                        } else {
                            log.warn("Failed to cache value for key: {}", key);
                        }
                    })
                    .doOnError(e -> log.error("Error caching value for key: " + key, e));
        } catch (JsonProcessingException e) {
            log.error("Error serializing value for cache with key: " + key, e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Boolean> evict(String key) {
        return redisTemplate.delete(key).map(count -> count > 0);
    }
}
