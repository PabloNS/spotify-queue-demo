package com.example.spotifyqueuedemo.service;

import com.example.spotifyqueuedemo.dto.*;
import com.example.spotifyqueuedemo.model.User;
import com.example.spotifyqueuedemo.model.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import static com.example.spotifyqueuedemo.utils.Constants.CLIENT_ID_TEST;

@Service
@Slf4j
public class SpotifyService {

    @Value("${spotify.clientId}")
    private String clientId;

    @Value("${spotify.clientSecret}")
    private String clientSecret;

    private Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    private RestTemplate restTemplate;

    private UserRepository userRepository;

    public SpotifyService(RestTemplate restTemplate, UserRepository userRepository){
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    public ResponseEntity addSongToQueue(QueueSongDto queueSongDto){

        User user = userRepository.findById(CLIENT_ID_TEST)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://api.spotify.com/v1/me/player/queue?");
        urlStringBuilder.append("uri=").append(queueSongDto.getTrackId());
        urlStringBuilder.append("&device_id=").append(queueSongDto.getDeviceId());

        StringBuilder bearerToken = new StringBuilder("Bearer ").append(user.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken.toString());
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(headers);

        try{
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlStringBuilder.toString(), request,
                    String.class);
            logger.error(responseEntity.toString());
            return responseEntity;
        } catch (HttpStatusCodeException e){
            if(e.getStatusCode().equals(HttpStatus.UNAUTHORIZED) && !e.getResponseBodyAsString().isEmpty()){
                refreshToken();
                bearerToken = new StringBuilder("Bearer ").append(user.getAccessToken());
                headers.add("Authorization", bearerToken.toString());
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(urlStringBuilder.toString(), request,
                        String.class);
                logger.error(responseEntity.toString());
                return responseEntity;
            }
            logger.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    public ResponseEntity getActiveDevice(){
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://api.spotify.com/v1/me/player/devices");

        User user = userRepository.findById(CLIENT_ID_TEST)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        StringBuilder bearerToken = new StringBuilder("Bearer ").append(user.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", bearerToken.toString());
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(headers);

        try{
            ResponseEntity<DeviceResponseDto> responseEntity = restTemplate.exchange(urlStringBuilder.toString(),
                    HttpMethod.GET, request, DeviceResponseDto.class);
            logger.error(responseEntity.toString());
            return new ResponseEntity<>(responseEntity.getBody().getDevices().stream().filter(
                    deviceDto -> deviceDto.isActive()), HttpStatus.OK);
        } catch (HttpStatusCodeException e){
            logger.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    public void authorize(HttpServletResponse response){
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://accounts.spotify.com/authorize?");
        urlStringBuilder.append("response_type=").append("code");
        urlStringBuilder.append("&redirect_uri=").append("http://localhost:8080/spotify/token");
        urlStringBuilder.append("&client_id=").append(clientId);
        urlStringBuilder.append("&scope=").append("user-read-playback-state user-modify-playback-state");

        try{
            response.sendRedirect(urlStringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ResponseEntity getAuthorizationCodeToken(String code){
        Optional<User> optClient = userRepository.findById(CLIENT_ID_TEST);
        User user = optClient.orElse(null);

        if(user == null){
            user = User.builder().id(CLIENT_ID_TEST).build();
        }

        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://accounts.spotify.com/api/token?");
        urlStringBuilder.append("grant_type=").append("authorization_code");
        urlStringBuilder.append("&code=").append(code);
        urlStringBuilder.append("&redirect_uri=").append("http://localhost:8888/callback");

        StringBuilder basicAuthentication = new StringBuilder(clientId).append(":").append(clientSecret);
        String encodedBasicAuthentication = Base64.getEncoder()
                .encodeToString((basicAuthentication.toString()).getBytes());
        StringBuilder encodedBasicAuthenticationHeader = new StringBuilder( "Basic ")
                .append(encodedBasicAuthentication);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" ,encodedBasicAuthenticationHeader.toString());
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<SpotifyTokenDto> responseEntity;

        try{
            responseEntity = restTemplate.postForEntity(urlStringBuilder.toString(), request, SpotifyTokenDto.class);
            logger.info(responseEntity.toString());
            SpotifyTokenDto body = responseEntity.getBody();
            user.setAccessToken(body.getAccessToken());
            user.setRefreshToken(body.getRefreshToken());
            userRepository.save(user);
            return responseEntity;
        } catch (HttpStatusCodeException e){
            logger.error(e.getLocalizedMessage());
            return new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode());
        }
    }

    public String searchTrack(String query){
        String token = getToken();

        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://api.spotify.com/v1/search?");
        urlStringBuilder.append("q=").append(query);
        urlStringBuilder.append("&type=").append("track");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<SpotifySearchResultDto> responseEntity;

        try {
            responseEntity = restTemplate.exchange(urlStringBuilder.toString(),
                    HttpMethod.GET, request, SpotifySearchResultDto.class);

            return responseEntity.getBody().getTracks().getItems().get(0).getUri();
        } catch (HttpStatusCodeException e){
            logger.error(e.getLocalizedMessage());
            return "Not Found";
        }
    }

    private String getToken(){
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://accounts.spotify.com/api/token?");
        urlStringBuilder.append("grant_type=").append("client_credentials");
        urlStringBuilder.append("&redirect_uri=").append("http://localhost:8888/callback");

        StringBuilder basicAuthentication = new StringBuilder(clientId).append(":").append(clientSecret);
        String encodedBasicAuthentication = Base64.getEncoder()
                .encodeToString((basicAuthentication.toString()).getBytes());
        StringBuilder encodedBasicAuthenticationHeader = new StringBuilder( "Basic ")
                .append(encodedBasicAuthentication);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" ,encodedBasicAuthenticationHeader.toString());
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<SpotifyTokenDto> responseEntity;

        try{
            responseEntity = restTemplate.postForEntity(urlStringBuilder.toString(), request, SpotifyTokenDto.class);
            logger.info(responseEntity.toString());
            SpotifyTokenDto body = responseEntity.getBody();
            StringBuilder token = new StringBuilder("Bearer ");
            return token.append(body.getAccessToken()).toString();
        } catch (HttpStatusCodeException e){
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    private void refreshToken(){
        User user = userRepository.findById(CLIENT_ID_TEST).
                orElseThrow(() -> new RuntimeException("Client not found"));

        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://accounts.spotify.com/api/token?");
        urlStringBuilder.append("grant_type=").append("refresh_token");
        urlStringBuilder.append("&refresh_token=").append(user.getRefreshToken());

        StringBuilder basicAuthentication = new StringBuilder(clientId).append(":").append(clientSecret);
        String encodedBasicAuthentication = Base64.getEncoder()
                .encodeToString((basicAuthentication.toString()).getBytes());
        StringBuilder encodedBasicAuthenticationHeader = new StringBuilder( "Basic ")
                .append(encodedBasicAuthentication);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization" ,encodedBasicAuthenticationHeader.toString());
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<SpotifyTokenDto> responseEntity;

        try{
            responseEntity = restTemplate.postForEntity(urlStringBuilder.toString(), request, SpotifyTokenDto.class);
            logger.info(responseEntity.toString());
            SpotifyTokenDto body = responseEntity.getBody();
            user.setAccessToken(body.getAccessToken());
            user.setRefreshToken(body.getRefreshToken());
            userRepository.save(user);
        } catch (HttpStatusCodeException e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
