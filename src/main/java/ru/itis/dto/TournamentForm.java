package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TournamentForm {

    private int seasonYear;
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}