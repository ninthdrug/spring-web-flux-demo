package com.example.demo.models;

import lombok.Data;

@Data
public class TimeZoneInfo {
    private int dstOffset;
    private int rawOffset;
    private String status;
    private String timeZoneId;
    private String timeZoneName;
}
