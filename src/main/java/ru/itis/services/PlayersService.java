package ru.itis.services;

import ru.itis.dto.PlayerForm;
import ru.itis.models.Player;

import java.sql.SQLException;
import java.util.List;

public interface PlayersService {

    List<Player> findAll() throws SQLException;
    void validate(PlayerForm form);
    void save(PlayerForm form, Long photoId) throws SQLException;
    Player findById(int playerId) throws SQLException;
    boolean update(int playerId, PlayerForm form) throws SQLException;
    boolean updatePhoto(int playerId, Long photoId) throws SQLException;
    List<Player> findByTeamIdAndSeasonYear(int teamId, int seasonYear) throws SQLException;
}