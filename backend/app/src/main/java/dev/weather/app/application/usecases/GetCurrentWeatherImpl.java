package dev.weather.app.application.usecases;

import dev.weather.app.domain.models.CurrentWeather;
import dev.weather.app.domain.ports.in.GetCurrentWeatherUseCase;
import dev.weather.app.domain.ports.out.WeatherServicePort;
import reactor.core.publisher.Mono;

public class GetCurrentWeatherImpl implements GetCurrentWeatherUseCase {

    private final WeatherServicePort weatherServicePort;

    public GetCurrentWeatherImpl(WeatherServicePort weatherServicePort) {
        this.weatherServicePort = weatherServicePort;
    }

    @Override
    public Mono<CurrentWeather> getCurrentWeather(Double lon, Double lat) {
        return weatherServicePort.getWeatherByLatAndLon(lon, lat);
    }
}
