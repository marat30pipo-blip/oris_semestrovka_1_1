package ru.itis.repository;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.dto.MatchDto;
import ru.itis.dto.MatchForm;

import java.sql.SQLException;
import java.util.List;

public interface MatchesRepository {
    List<MatchDto> findBySeasonYear(int seasonYear) throws SQLException;
    List<MatchDto> findByTournamentId(int tournamentId) throws SQLException;
    void save(MatchForm form) throws SQLException;
    MatchForm findById(int matchId) throws SQLException;
    boolean update(int matchId, MatchForm form) throws SQLException;
    boolean deleteById(int matchId) throws SQLException;
    MatchDetailsDto findDetailsById(int matchId) throws SQLException;
}
