package dev.weather.app.domain.ports.out;

import dev.weather.app.domain.models.CurrentWeather;
import reactor.core.publisher.Mono;

public interface WeatherServicePort {

    Mono<CurrentWeather> getWeatherByLatAndLon(Double lon , Double lat);
}
