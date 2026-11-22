package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalDto {

    private int goalId;
    private int matchId;
    private int playerId;
    private String lastName;
    private String name;
    private String middleName;
    private int teamId;
    private String teamName;
    private int minute;
    private boolean ownGoal;
}