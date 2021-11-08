package com.example.spotifyqueuedemo.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceResponseDto {

    private List<DeviceDto> devices;
}
