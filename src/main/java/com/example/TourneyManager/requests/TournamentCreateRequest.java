package com.example.TourneyManager.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TournamentCreateRequest {
        private String name;
        private String type;
        private String rankedBy;
        private String startAt;
}
