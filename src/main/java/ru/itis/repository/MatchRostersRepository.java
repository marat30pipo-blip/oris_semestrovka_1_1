package ru.itis.repository;

import ru.itis.models.Player;

import java.sql.SQLException;
import java.util.List;

public interface MatchRostersRepository {

    List<Player> findByMatchIdAndTeamId(int matchId, int teamId) throws SQLException;
    boolean save(int matchId, int teamId, int playerId) throws SQLException;
    boolean delete(int matchId, int teamId, int playerId) throws SQLException;
}
