package ru.itis.services;

import ru.itis.models.Player;

import java.sql.SQLException;
import java.util.List;

public interface MatchRostersService {

    List<Player> findByMatchIdAndTeamId(int matchId, int teamId) throws SQLException;
    List<Player> findEligiblePlayers(int matchId, int teamId) throws SQLException;
    boolean save(int matchId, int teamId, int playerId) throws SQLException;
    boolean delete(int matchId, int teamId, int playerId) throws SQLException;

}