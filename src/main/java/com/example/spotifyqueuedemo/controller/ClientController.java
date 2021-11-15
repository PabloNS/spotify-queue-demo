package com.example.spotifyqueuedemo.controller;

import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.service.ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
public class ClientController {

    private ClientService service;

    public ClientController(ClientService service){
        this.service = service;
    }

    @GetMapping("{id}")
    public User getClient(@PathVariable String id){
        return service.getClient(id);
    }
}
