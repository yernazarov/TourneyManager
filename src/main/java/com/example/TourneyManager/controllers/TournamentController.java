package com.example.TourneyManager.controllers;

import com.example.TourneyManager.requests.TournamentCreateRequest;
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

import java.util.HashMap;
import java.util.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
//    @Autowired
//    private TournamentService tournamentService;
    @Value("${api_key}")
    private String api_key;

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
            parameters.add("tournament[ranked_by]", tournamentCreateRequest.getRankedBy());
            parameters.add("tournament[start_at]", tournamentCreateRequest.getStartAt());
            HttpEntity<?> entity = new HttpEntity<>(null, null);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri)
                    .queryParams(parameters);
            UriComponents uriComponents = builder.build().encode();
            ResponseEntity<String> responseEntity = rt.exchange(uriComponents.toUri(), HttpMethod.POST,
                    entity, String.class);
            System.out.println(responseEntity);
            responseMessage.put("message", "Tournament successfully created");
            return responseEntity;
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.put("message", "Error, tournament was not created");
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
    }
}