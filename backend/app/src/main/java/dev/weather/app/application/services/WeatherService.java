package dev.weather.app.application.services;

import dev.weather.app.domain.models.CurrentWeather;
import dev.weather.app.domain.ports.in.GetCurrentWeatherUseCase;
import reactor.core.publisher.Mono;

public class WeatherService implements GetCurrentWeatherUseCase {

    private final GetCurrentWeatherUseCase getCurrentWeatherUseCase;

    public WeatherService(GetCurrentWeatherUseCase getCurrentWeatherUseCase) {
        this.getCurrentWeatherUseCase = getCurrentWeatherUseCase;
    }

    @Override
    public Mono<CurrentWeather> getCurrentWeather(Double lon, Double lat) {
        return getCurrentWeatherUseCase.getCurrentWeather(lon, lat);
    }
}
