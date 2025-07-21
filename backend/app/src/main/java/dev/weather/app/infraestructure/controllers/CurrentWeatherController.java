package dev.weather.app.infraestructure.controllers;

import dev.weather.app.domain.models.CurrentWeather;
import dev.weather.app.domain.ports.out.WeatherServicePort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CurrentWeatherController {

    private final WeatherServicePort weatherService;

    public CurrentWeatherController(WeatherServicePort weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public Mono<CurrentWeather> getCurrentWeather(@RequestParam Double lon, Double lat){
        return weatherService.getWeatherByLatAndLon(lon,lat);
    }
}
