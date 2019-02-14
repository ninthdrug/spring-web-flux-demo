package com.example.demo.controllers;

import com.example.demo.models.ElevationInfo;
import com.example.demo.models.TimeZoneInfo;
import com.example.demo.models.WeatherInfo;
import com.example.demo.services.GoogleApiService;
import com.example.demo.services.OpenWeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


@RestController
public class DemoController {
    private final Logger logger = LoggerFactory.getLogger(DemoController.class);
    private final String format = "At the location %s, the temperature is %f, the timezone is %s, and the elevation is %f";
    @Autowired
    private OpenWeatherService openWeatherService;
    @Autowired
    private GoogleApiService googleApiService;

    @RequestMapping(path = "/demo")
    public Mono<String> demo(@RequestParam(value = "zipcode") String zipcode) {
        if (!isZipcode(zipcode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid zipcode");
        }
        try {
            Mono<WeatherInfo> weatherInfoMono = openWeatherService.getWeatherInfo(zipcode);
            Mono<String> result = weatherInfoMono.flatMap(weatherInfo -> {
                String location = weatherInfo.getName();
                double temperature = weatherInfo.getMain().getTemp();
                double lat = weatherInfo.getCoord().getLat();
                double lon = weatherInfo.getCoord().getLon();
                Mono<TimeZoneInfo> timeZoneInfoMono = googleApiService.getTimeZone(lat, lon);
                Mono<ElevationInfo> elevationInfoMono = googleApiService.getElevation(lat, lon);
                return Mono.zip(timeZoneInfoMono, elevationInfoMono, (t, e) -> {
                    String timezone = t.getTimeZoneName();
                    double elevation = e.getResults().get(0).getElevation();
                    return String.format(format, location, temperature, timezone, elevation);
                });
            });
            return result;
        } catch (Exception e) {
            logger.error("error in /demo for zipcode:{}", zipcode, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }

    public static boolean isZipcode(String zipcode) {
        if (zipcode == null || zipcode.length() != 5) {
            return false;
        }
        for (int i = 0; i < zipcode.length(); i++) {
            if (!Character.isDigit(zipcode.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
