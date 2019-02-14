package com.example.demo.models;

import lombok.Data;

import java.util.List;

@Data
public class WeatherInfo {
    @Data
    public static class Main {
        private double temp;
        private int pressure;
        private int humidity;
        private double temp_min;
        private double temp_max;
    }

    private long id;
    private String name;
    private Coord coord;
    private WeatherInfo.Main main;
    private List<Weather> weather;
}