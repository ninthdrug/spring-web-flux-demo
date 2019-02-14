package com.example.demo.services;

import com.example.demo.models.WeatherInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class OpenWeatherService {
    private static Logger logger = LoggerFactory.getLogger(OpenWeatherService.class);
    private WebClient webClient = WebClient.create("http://api.openweathermap.org");
    private final String urlFormat = "http://api.openweathermap.org/data/2.5/weather?zip=%s&APPID=%s";
    private final String appId = System.getenv("OPENWEATHER_APPID");
    private final Gson gson = new GsonBuilder().create();

    public Mono<WeatherInfo> getWeatherInfo(String zipcode) {
        String uri = "/data/2.5/weather?zip=" + zipcode + "&APPID=" + appId;
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(WeatherInfo.class);
    }
}
