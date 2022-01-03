package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.dto.UserPositionDto;
import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    private QueueService queueService;

    public UserService(UserRepository userRepository,  QueueService queueService){
        this.userRepository = userRepository;
        this.queueService = queueService;
    }

    public User getUser(String id){
        return userRepository.findById(id).orElseGet(() -> {
            log.error("User with id {} doesn't exist", id);
            return null; });
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public ResponseEntity saveUser(User user){
        User userSaved = userRepository.findBySpotifyId(user.getSpotifyId());
        if(userSaved!=null){
            log.error("User already exist", userSaved.getSpotifyId());
            return new ResponseEntity("User already exists", HttpStatus.OK);
        }
        userRepository.save(user);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    public List<User> getUsersWithOpenQueueNearMe(){
        //Get user asking from token / authentication
        User userAsking = User.builder().build();
        List<User> usersWithOpenQueue = userRepository.findByWithOpenQueueTrueAndQueuedSongsLessThan(10);
        return usersWithOpenQueue.stream().filter(user ->
                Math.abs(userAsking.getPositionLatitude() - user.getPositionLatitude()) <= 10
                && Math.abs(userAsking.getPositionLongitude() - user.getPositionLongitude()) <= 10
                && user.getOpenQueueTime().plusSeconds(120).isAfter(Instant.now())).collect(Collectors.toList());
    }

    public void recommendMe(UserPositionDto userPositionDto) {
        log.info(userPositionDto.toString());
        User user = userRepository.findBySpotifyId("pablonuñezserra");
        log.info(user.toString());
        user.setPositionAccuracy(userPositionDto.getPositionAccuracy());
        user.setPositionLatitude(userPositionDto.getPositionLatitude());
        user.setPositionLongitude(userPositionDto.getPositionLongitude());
        user.setWithOpenQueue(true);
        user.setOpenQueueTime(Instant.now());
        userRepository.save(user);
        try {
            queueService.closeUserQueue(user);
        } catch (InterruptedException e) {
            //log
        }
    }
}
