package com.example.spotifyqueuedemo.dto;

import lombok.Data;

@Data
public class UserPositionDto {

    private Float positionLatitude;

    private Float positionLongitude;

    private Float positionAccuracy;
}
