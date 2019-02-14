package com.example.demo.models;

import lombok.Data;

import java.util.List;

@Data
public class ElevationInfo {
    @Data
    public static class Location {
        private double lat;
        private double lng;
    }

    @Data
    public static class Elevation {
        private double elevation;
        private Location location;
        private double resolution;
    }

    private String status;
    private List<ElevationInfo.Elevation> results;
}
