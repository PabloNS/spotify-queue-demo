package com.example.spotifyqueuedemo.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpotifyTrackDto {

    private List<SpotifyItemTrackDto> items;
}
