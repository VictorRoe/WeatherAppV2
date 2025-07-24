package dev.weather.app.infraestructure.controllers;

import dev.weather.app.domain.models.CurrentWeather;
import dev.weather.app.domain.ports.out.WeatherServicePort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CurrentWeatherController {

    private final WeatherServicePort weatherService;
    private static final Logger logger = LogManager.getLogger(CurrentWeatherController.class);

    public CurrentWeatherController(WeatherServicePort weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public Mono<CurrentWeather> getCurrentWeather(@RequestParam Double lon, Double lat){
       logger.info("Solicitando clima para coordenadas: lat={}, lon={}",lat ,lon);

       return weatherService.getWeatherByLatAndLon(lon, lat)
               .doOnSuccess(weather -> logger.info("Clima obtenido exitosamente para lat={}, long={}",
                       lon, lat))
               .doOnError(error -> logger.error("Error al obtener el clima para lat={}, lon={}", lat,lon, error.getMessage(), error));

    }
}
