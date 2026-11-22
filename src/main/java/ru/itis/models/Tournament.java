package ru.itis.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Tournament {
    private Integer tournamentId;
    private int seasonId;
    private String name;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
