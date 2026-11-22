package ru.itis.services;

import ru.itis.dto.MatchDto;
import ru.itis.dto.StandingDto;
import ru.itis.models.Team;

import java.util.List;

public interface StandingsService {
    List<StandingDto> calculate(List<Team> teams, List<MatchDto> matches);
}
