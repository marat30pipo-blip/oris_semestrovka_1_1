package ru.itis.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Team {
    private Integer teamId;
    private String name;
    private LocalDate foundedOn;
    private Long emblemId;
    private String description;
}
