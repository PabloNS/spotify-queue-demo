package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientService {

    private Logger logger = LoggerFactory.getLogger(ClientService.class);

    private UserRepository userRepository;

    public ClientService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //@Cacheable(value = "clientCache")
    public User getClient(String id){
        return userRepository.findById(id).orElseGet(() -> {
            logger.error("Client with id {} doesn't exist", id);
            return null; });
    }
}
