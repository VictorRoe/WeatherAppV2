package dev.weather.app.domain.ports.out;

import reactor.core.publisher.Mono;

public interface CachePort<T> {
    Mono<T> get(String key, Class<T> type);
    Mono<Boolean> put(String key, T value, long ttlInSeconds);
    Mono<Boolean> evict(String key);
}
