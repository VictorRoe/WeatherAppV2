package dev.weather.app.infraestructure.adapters;

import dev.weather.app.domain.models.CurrentWeather;
import dev.weather.app.domain.ports.out.CachePort;
import dev.weather.app.domain.ports.out.WeatherServicePort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WeatherServiceAdapter implements WeatherServicePort {

    private final WebClient webClient;

    private String apiKey;

    private final String baseUrl;
    private final CachePort<CurrentWeather> cachePort;
    private static final long CACHE_TTL_SECONDS = 900; // 15 minutes

    public WeatherServiceAdapter(WebClient.Builder webClientBuilder,
                               @Value("${weatherapi.base-url}") String baseUrl,
                               @Value("${weatherapi.api-key}") String apiKey,
                               @Qualifier("redisCachePort") CachePort<CurrentWeather> cachePort) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.cachePort = cachePort;
    }

    @Override
    public Mono<CurrentWeather> getWeatherByLatAndLon(Double lon, Double lat) {
        String cacheKey = String.format("weather:%.4f:%.4f", lat, lon);
        
        // Try to get from cache first
        return cachePort.get(cacheKey, CurrentWeather.class)
                .switchIfEmpty(
                    // If not in cache, fetch from API and cache the result
                    fetchFromApi(lon, lat)
                        .flatMap(weather -> 
                            cachePort.put(cacheKey, weather, CACHE_TTL_SECONDS)
                                    .thenReturn(weather)
                        )
                );
    }
    
    private Mono<CurrentWeather> fetchFromApi(Double lon, Double lat) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeather.class);
    }
}
