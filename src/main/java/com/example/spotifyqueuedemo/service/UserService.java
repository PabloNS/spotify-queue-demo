package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //@Cacheable(value = "clientCache")
    public User getClient(String id){
        return userRepository.findById(id).orElseGet(() -> {
            logger.error("User with id {} doesn't exist", id);
            return null; });
    }

    public List<User> getAllClients(){
        return userRepository.findAll();
    }

    public ResponseEntity saveUser(User user){
        User userSaved = userRepository.findBySpotifyId(user.getSpotifyId());
        if(userSaved!=null){
            logger.error("User already exist", userSaved.getSpotifyId());
            return new ResponseEntity("User already exists", HttpStatus.OK);
        }
        userRepository.save(user);
        return new ResponseEntity(user, HttpStatus.OK);
    }
}
