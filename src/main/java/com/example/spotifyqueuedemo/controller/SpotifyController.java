package com.example.spotifyqueuedemo.controller;

import com.example.spotifyqueuedemo.dto.QueueSongDto;
import com.example.spotifyqueuedemo.service.SpotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spotify")
public class SpotifyController {

    private SpotifyService service;

    public SpotifyController(SpotifyService service){
        this.service = service;
    }

    @PostMapping(value = "queue", produces = "application/json")
    public ResponseEntity addSongToQueue(@RequestBody QueueSongDto queueSongDto){
        return service.addSongToQueue(queueSongDto);
    }

    @GetMapping("activeDevice")
    public ResponseEntity getActiveDevice(){
        return service.getActiveDevice();
    }

    @GetMapping("authorize")
    public void authorize(){
         service.authorize();
    }

    @GetMapping("token")
    public ResponseEntity token(@RequestParam String code){
        return service.getAuthorizationCodeToken(code);
    }
}