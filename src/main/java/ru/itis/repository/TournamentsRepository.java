package ru.itis.repository;

import ru.itis.dto.MatchForm;
import ru.itis.dto.TournamentForm;
import ru.itis.models.Tournament;

import java.sql.SQLException;
import java.util.List;

public interface TournamentsRepository {
    List<Tournament> findBySeasonYear(int seasonYear) throws SQLException;
    Tournament findById(int tournamentId) throws SQLException;
    boolean save(TournamentForm form) throws SQLException;
    boolean update(int tournamentId, TournamentForm form) throws SQLException;
    List<Tournament> findByTeamIdAndSeasonYear(int teamId, int seasonYear) throws SQLException;
}
