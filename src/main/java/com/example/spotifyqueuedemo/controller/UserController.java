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
    public User getUser(@PathVariable String id){
        return service.getUser(id);
    }

    @GetMapping
    public List<User> getAllUsers(){
        return service.getAllUsers();
    }

    @PostMapping
    @CrossOrigin(origins = {"http://localhost:8888", "http://localhost:3000"})
    public ResponseEntity saveUser(@RequestBody User user){
        return service.saveUser(user);
    }

    @GetMapping("withOpenQueueNearMe")
    @CrossOrigin(origins = {"http://localhost:8888"})
    public List<User> getUsersWithOpenQueueNearMe(){
        return service.getUsersWithOpenQueueNearMe();
    }
}
