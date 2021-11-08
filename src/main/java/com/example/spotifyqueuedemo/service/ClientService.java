package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.model.Client;
import com.example.spotifyqueuedemo.model.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientService {

    private Logger logger = LoggerFactory.getLogger(ClientService.class);

    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository){
        this.clientRepository = clientRepository;
    }

    //@Cacheable(value = "clientCache")
    public Client getClient(String id){
        return clientRepository.findById(id).orElseGet(() -> {
            logger.error("Client with id {} doesn't exist", id);
            return null; });
    }
}
