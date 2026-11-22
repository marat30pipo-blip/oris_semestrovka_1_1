package ru.itis.services;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.dto.MatchDto;
import ru.itis.dto.MatchForm;

import java.sql.SQLException;
import java.util.List;

public interface MatchesService {
    List<MatchDto> findBySeasonYear(int seasonYear) throws SQLException;
    List<MatchDto> findResults(List<MatchDto> matches) throws SQLException;
    List<MatchDto> findByTournamentId(int tournamentId) throws SQLException;
    void save(MatchForm form) throws SQLException;
    MatchForm findById(int matchId) throws SQLException;
    boolean update(int matchId, MatchForm form) throws SQLException;
    boolean deleteById(int matchId) throws SQLException;
    List<MatchDto> findByTournamentIdAndStatus(int tournamentId, String status) throws SQLException;
    MatchDetailsDto findDetailsById(int matchId) throws SQLException;
}
