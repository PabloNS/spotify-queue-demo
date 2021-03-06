package com.example.spotifyqueuedemo.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Data
@Builder
public class User {

    @Id
    private String id;

    private String accessToken;

    private String refreshToken;

    private String spotifyId;

    private String spotifyDisplayName;

    private String spotifyEmail;

    private Float positionLatitude;

    private Float positionLongitude;

    private Float positionAccuracy;

    private boolean withOpenQueue;

    private int queuedSongs;

    private Instant openQueueTime;
}
