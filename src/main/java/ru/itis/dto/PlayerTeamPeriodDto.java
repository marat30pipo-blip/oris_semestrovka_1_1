package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PlayerTeamPeriodDto {

    private int appearanceId;
    private int playerId;
    private int teamId;
    private String teamName;
    private int seasonYear;
    private LocalDate startDate;
    private LocalDate endDate;
}