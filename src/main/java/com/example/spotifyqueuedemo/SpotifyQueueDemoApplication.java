package com.example.spotifyqueuedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpotifyQueueDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyQueueDemoApplication.class, args);
	}

}
