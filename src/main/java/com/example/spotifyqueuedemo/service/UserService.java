package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

    private QueueService queueService;

    public UserService(UserRepository userRepository, QueueService queueService){
        this.userRepository = userRepository;
        this.queueService = queueService;
    }

    public User getUser(String id){
        return userRepository.findById(id).orElseGet(() -> {
            logger.error("User with id {} doesn't exist", id);
            return null; });
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public ResponseEntity saveUser(User user){
        User userSaved = userRepository.findBySpotifyId(user.getSpotifyId());
        if(userSaved!=null){
            logger.error("User already exist", userSaved.getSpotifyId());
            return new ResponseEntity("User already exists", HttpStatus.OK);
        }
        user.setWithOpenQueue(true);
        user.setOpenQueueTime(Instant.now());
        userRepository.save(user);
        try {
            queueService.closeUserQueue(user);
        } catch (InterruptedException e) {
            //log
        }
        return new ResponseEntity(user, HttpStatus.OK);
    }

    public List<User> getUsersWithOpenQueueNearMe(){
        //Get user asking from token / authentication
        User userAsking = User.builder().build();
        List<User> usersWithOpenQueue = userRepository.findByWithOpenQueueTrueAndQueuedSongsLessThan(10);
        return usersWithOpenQueue.stream().filter(user ->
                Math.abs(userAsking.getPositionLatitude() - user.getPositionLatitude()) <= 10
                && Math.abs(userAsking.getPositionLongitude() - user.getPositionLongitude()) <= 10
                && user.getOpenQueueTime().plusSeconds(60).isAfter(Instant.now())).collect(Collectors.toList());
    }
}
