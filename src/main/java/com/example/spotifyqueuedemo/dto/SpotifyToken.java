package com.example.spotifyqueuedemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpotifyToken {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    private String scope;

    @JsonProperty("refresh_token")
    private String refreshToken;
}
