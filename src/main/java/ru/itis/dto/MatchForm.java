package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class MatchForm {
    private int tournamentId;
    private int team1Id;
    private int team2Id;
    private Integer venueId;
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
