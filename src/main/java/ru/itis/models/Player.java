package ru.itis.models;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class Player {

    private int playerId;
    private String lastName;
    private String name;
    private String middleName;
    private LocalDate birthDate;
    private Integer heightCm;
    private BigDecimal weightKg;
    private String position;
    private Long photoId;
    private Integer shirtNumber;
}