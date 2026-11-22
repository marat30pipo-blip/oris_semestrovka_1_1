package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class MatchDetailsDto {

    private int matchId;
    private int tournamentId;
    private String tournamentName;
    private String tournamentType;
    private int seasonYear;
    private int team1Id;
    private String team1Name;
    private int team2Id;
    private String team2Name;
    private Integer venueId;
    private String venueName;
    private LocalDate matchDate;
    private LocalTime startTime;
    private Integer roundNumber;
    private String status;
    private Integer score1;
    private Integer score2;
    private Integer penalty1;
    private Integer penalty2;
    private String stage;
}