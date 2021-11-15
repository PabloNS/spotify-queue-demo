package com.example.spotifyqueuedemo.controller;

import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private UserService service;

    public UserController(UserService service){
        this.service = service;
    }

    @GetMapping("{id}")
    public User getClient(@PathVariable String id){
        return service.getClient(id);
    }

    @GetMapping
    public List<User> getAllClients(){
        return service.getAllClients();
    }

    @PostMapping
    @CrossOrigin(origins = {"http://localhost:8888"})
    public ResponseEntity saveUser(@RequestBody User user){
        return service.saveUser(user);
    }
}
