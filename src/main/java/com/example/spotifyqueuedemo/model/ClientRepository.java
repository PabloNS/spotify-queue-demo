package com.example.spotifyqueuedemo.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client,String> {
}
