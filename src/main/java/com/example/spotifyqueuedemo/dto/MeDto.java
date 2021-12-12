package com.example.spotifyqueuedemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MeDto {

    @JsonProperty("display_name")
    private String displayName;

    private String email;

    private String id;
}
