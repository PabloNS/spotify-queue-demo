package com.example.spotifyqueuedemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeviceDto {

    private String id;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("is_private_session")
    private boolean isPrivateSession;

    @JsonProperty("is_restricted")
    private boolean isRestricted;

    private String name;

    private String type;

    @JsonProperty("volume_percent")
    int volumePercent;
}
