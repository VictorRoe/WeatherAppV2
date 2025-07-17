package dev.weather.app.domain.ports.in;

import dev.weather.app.domain.models.CurrentWeather;
import reactor.core.publisher.Mono;

public interface GetCurrentWeatherUseCase {

    Mono<CurrentWeather>getCurrentWeather(Double lon, Double lat);
}
