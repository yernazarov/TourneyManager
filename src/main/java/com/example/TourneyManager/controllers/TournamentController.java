package com.example.TourneyManager.controllers;

import com.example.TourneyManager.requests.AddPlayerRequest;
import com.example.TourneyManager.requests.TournamentCreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {
//    @Autowired
//    private TournamentService tournamentService;
    @Value("${api_key}")
    private String api_key;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createTournament(@RequestBody TournamentCreateRequest tournamentCreateRequest) {
        Map<String, Object> responseMessage = new HashMap<>();
        try {
            String uri = "https://api.challonge.com/v1/tournaments";
            MultiValueMap<String,String> parameters = new LinkedMultiValueMap<>();
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
            Map<String, Object> map = new HashMap<>();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = rt.postForEntity(
                    uriComponents.toUri(),
                    entity, String.class);
            StringReader reader = new StringReader(Objects.requireNonNull(response.getBody()));
            InputSource inputSource = new InputSource( reader );
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.parse(inputSource);
            doc.getDocumentElement().normalize();
            long id = Integer.parseInt(doc.getElementsByTagName("id").item(0).getTextContent());
            reader.close();
            responseMessage.put("id", id);
            responseMessage.put("message", "Tournament successfully created with id: "+ id);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            responseMessage.put("message", "Error, tournament was not created");
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("add-players")
    public ResponseEntity<?> addPlayers(@RequestBody AddPlayerRequest addPlayerRequest) {
        Map<String, Object> responseMessage = new HashMap<>();
        try {
            String uri = String.format("https://api.challonge.com/v1/tournaments/%s/participants/bulk_add", addPlayerRequest.getId());
            MultiValueMap<String,String> parameters = new LinkedMultiValueMap<>();
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