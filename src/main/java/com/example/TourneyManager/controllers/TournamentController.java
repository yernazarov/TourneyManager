package com.example.TourneyManager.controllers;

import com.example.TourneyManager.requests.AddPlayerRequest;
import com.example.TourneyManager.requests.TournamentCreateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
//    @Autowired
//    private TournamentService tournamentService;
    @Value("${api_key}")
    private String api_key;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/create")
    public ResponseEntity<?> createTournament(HttpServletRequest request, @RequestBody TournamentCreateRequest tournamentCreateRequest) {
        Map<String, Object> responseMessage = new HashMap<>();
        try {
            String uri = "https://api.challonge.com/v1/tournaments";
            MultiValueMap<String,String> parameters = new LinkedMultiValueMap<String,String>();
            RestTemplate rt = new RestTemplate();
            parameters.add("api_key", api_key);
            parameters.add("tournament[name]", tournamentCreateRequest.getName());
            parameters.add("tournament[tournament_type]", tournamentCreateRequest.getType());
            parameters.add("tournament[open_signup]", "false");
            parameters.add("tournament[ranked_by]", tournamentCreateRequest.getRankedBy());
            parameters.add("tournament[start_at]", tournamentCreateRequest.getStartAt());
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                    .queryParams(parameters);
            UriComponents uriComponents = builder.build().encode();
            ResponseEntity<?> response = rt.postForEntity(
                    uriComponents.toUri(),
                    null, Void.class);
            if (response.getStatusCode().value() != 200){
                throw new RuntimeException();
            }
            responseMessage.put("message", "Tournament successfully created");
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.put("message", "Error, tournament was not created");
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("add-players")
    public ResponseEntity<?> addPlayers(HttpServletRequest request, @RequestBody AddPlayerRequest addPlayerRequest) {
        Map<String, Object> responseMessage = new HashMap<>();
        try {
            String uri = String.format("https://api.challonge.com/v1/tournaments/%s/participants/bulk_add", addPlayerRequest.getId());
            MultiValueMap<String,String> parameters = new LinkedMultiValueMap<String,String>();
            RestTemplate rt = new RestTemplate();
            parameters.add("api_key", api_key);
            for (String player: addPlayerRequest.getPlayerList()) {
                parameters.add("participants[][name]", player);
            }
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                    .queryParams(parameters);
            UriComponents uriComponents = builder.build().encode();
            ResponseEntity<?> response = rt.postForEntity(
                    uriComponents.toUri(),
                    null, Void.class);
            if (response.getStatusCode().value() != 200){
                throw new RuntimeException();
            }
            responseMessage.put("message", "Players were successfully added");
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.put("message", "Error, players were not added");
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
    }

}