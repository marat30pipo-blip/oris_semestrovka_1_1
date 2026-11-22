package ru.itis.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class PlayerForm {
    private String lastName;
    private String name;
    private String middleName;
    private LocalDate birthDate;
    private Integer heightCm;
    private BigDecimal weightKg;
    private String position;
    private Integer shirtNumber;
}