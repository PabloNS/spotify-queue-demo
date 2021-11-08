package com.example.spotifyqueuedemo.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class Client {

    @Id
    private String id;

    private String accessToken;

    private String refreshToken;
}
