package com.example.TourneyManager.requests;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AddPlayerRequest {
    private String id;
    private ArrayList<String> playerList;
}
