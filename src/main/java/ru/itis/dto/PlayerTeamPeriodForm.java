package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PlayerTeamPeriodForm {

    private int playerId;
    private int teamId;
    private int seasonYear;
    private LocalDate startDate;
    private LocalDate endDate;
}