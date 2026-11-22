package ru.itis.models;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Venue {
    private int venueId;
    private String name;
}