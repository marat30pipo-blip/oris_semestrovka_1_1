package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class MatchDto {
    private int matchId;
    private String team1Name;
    private String team2Name;
    private String tournamentName;
    private LocalDate matchDate;
    private LocalTime startTime;
    private String status;
    private Integer score1;
    private Integer score2;
    private int team1Id;
    private int team2Id;
    private String stage;
    private Integer penalty1;
    private Integer penalty2;
    private Integer roundNumber;
}

