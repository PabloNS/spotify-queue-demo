package com.example.spotifyqueuedemo.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {

    User findBySpotifyId(String spotifyId);
}
