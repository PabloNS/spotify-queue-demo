package com.example.spotifyqueuedemo.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User,String> {

    User findBySpotifyId(String spotifyId);

    List<User> findByWithOpenQueueTrueAndQueuedSongsLessThan(int queuedSongs);
}
