package ru.itis.services;

import ru.itis.models.Team;

import java.sql.SQLException;
import java.util.List;

public interface TeamsService {
    List<Team> findByTournamentId(int tournamentId) throws SQLException;
    void save(String name, String description, Long emblemId) throws SQLException;
    List<Team> findAll() throws SQLException;
    void validate(String name, String description);
    Team findById(int teamId) throws SQLException;
    boolean update(int teamId, String name, String description) throws SQLException;
    boolean updateEmblem(int teamId, Long emblemId) throws SQLException;
    boolean addToTournament(int tournamentId, int teamId) throws SQLException;
}
