package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.model.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class QueueService {

    private static final long OPEN_QUEUE_TIME = 180000;

    private UserRepository userRepository;

    public QueueService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Async
    public void closeUserQueue(User user) throws InterruptedException {
        Thread.sleep(10000);
        user.setWithOpenQueue(false);
        userRepository.save(user);
    }
}
