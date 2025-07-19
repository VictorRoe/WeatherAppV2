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

    @Value("${weatherapi.api-key}")
    private String apiKey;

    public WeatherServiceAdapter(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<CurrentWeather> getWeatherByLatAndLon(Double lon, Double lat) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/weather?lat=44.34&lon=10.99&appid={API key}")
                        .queryParam("key", apiKey)
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeather.class);
    }
}
