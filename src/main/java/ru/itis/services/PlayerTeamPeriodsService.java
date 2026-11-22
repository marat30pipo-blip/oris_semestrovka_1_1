package ru.itis.services;

import ru.itis.dto.PlayerTeamPeriodDto;
import ru.itis.dto.PlayerTeamPeriodForm;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface PlayerTeamPeriodsService {

    List<PlayerTeamPeriodDto> findByPlayerId(int playerId) throws SQLException;
    boolean save(PlayerTeamPeriodForm form) throws SQLException;
    PlayerTeamPeriodDto findById(int appearanceId) throws SQLException;
    boolean closePeriod(int appearanceId, LocalDate endDate) throws SQLException;
}
