package com.example.demo.services;

import com.example.demo.models.ElevationInfo;
import com.example.demo.models.TimeZoneInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GoogleApiService {
    private static Logger logger = LoggerFactory.getLogger(GoogleApiService.class);
    private WebClient webClient = WebClient.create("https://maps.googleapis.com");
    private final String timezone_uri =
            "/maps/api/timezone/json?location=%f,%f&timestamp=%d&key=%s";
    private final String elevation_uri =
            "/maps/api/elevation/json?locations=%f,%f&key=%s";
    private String google_api_key = System.getenv("GOOGLE_API_KEY");

    public Mono<TimeZoneInfo> getTimeZone(double lat, double lon) {
        long timestamp = System.currentTimeMillis() / 1000;

        try {
            String uri = String.format(
                    timezone_uri,
                    lat,
                    lon,
                    timestamp,
                    google_api_key
            );
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(TimeZoneInfo.class);
        } catch (Exception e) {
            String msg = String.format("error getting timezone info for %f,%f", lat, lon);
            logger.error(msg, e);
            throw new GoogleApiServiceException(msg, e);
        }
    }

    public Mono<ElevationInfo> getElevation(double lat, double lon) {
        try {
            String uri = String.format(
                    elevation_uri,
                    lat,
                    lon,
                    google_api_key
            );
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(ElevationInfo.class);
        } catch (Exception e) {
            String msg = String.format("error getting elevation info for %f,%f", lat, lon);
            logger.error(msg, e);
            throw new GoogleApiServiceException(msg, e);
        }
    }
}
