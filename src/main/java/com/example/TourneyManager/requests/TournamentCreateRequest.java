package com.example.TourneyManager.requests;

import lombok.Data;

import java.sql.Time;

@Data
public class TournamentCreateRequest {
        private String name;
        private String type;
        private boolean open_signup = false;
        private String rankedBy;
        private String startAt;
}
