package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GoalForm {

    private int matchId;
    private int playerId;
    private int teamId;
    private int minute;
}