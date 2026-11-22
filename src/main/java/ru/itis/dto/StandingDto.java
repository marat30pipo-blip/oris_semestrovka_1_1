package ru.itis.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandingDto {
    private int teamId;
    private String teamName;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }
}
