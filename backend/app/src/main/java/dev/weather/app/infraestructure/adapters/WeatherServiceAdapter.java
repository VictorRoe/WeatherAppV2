package dev.weather.app.infraestructure.adapters;

import dev.weather.app.domain.models.CurrentWeather;
import dev.weather.app.domain.ports.out.WeatherServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WeatherServiceAdapter implements WeatherServicePort {

    private final WebClient webClient;

    private String apiKey;

    private String baseUrl;

    public WeatherServiceAdapter(WebClient.Builder webClientBuilder,
                                 @Value("${weatherapi.base-url}") String baseUrl,
                                 @Value("${weatherapi.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public Mono<CurrentWeather> getWeatherByLatAndLon(Double lon, Double lat) {
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
