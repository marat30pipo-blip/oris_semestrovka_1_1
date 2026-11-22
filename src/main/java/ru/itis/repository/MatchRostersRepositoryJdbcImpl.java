package ru.itis.repository;

import ru.itis.models.Player;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatchRostersRepositoryJdbcImpl
        implements MatchRostersRepository {

    private final DataSource dataSource;

    private static final String SQL_SELECT_ROSTER =
            "SELECT p.player_id, p.lastname, p.name, p.middle_name, " +
                    "p.birthdate, p.height_cm, p.weight_kg, p.position, " +
                    "p.photo_id, p.shirt_number " +
                    "FROM match_rosters AS r " +
                    "JOIN players AS p ON p.player_id = r.player_id " +
                    "WHERE r.match_id = ? AND r.team_id = ? " +
                    "ORDER BY p.lastname, p.name, p.middle_name, p.player_id";

    private static final String SQL_INSERT_ROSTER_PLAYER =
            "INSERT INTO match_rosters (match_id, team_id, player_id) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT (match_id, player_id) DO NOTHING";

    private static final String SQL_DELETE_ROSTER_PLAYER =
            "DELETE FROM match_rosters AS r " +
                    "WHERE r.match_id = ? AND r.team_id = ? AND r.player_id = ? " +
                    "AND NOT EXISTS (" +
                    "    SELECT 1 FROM goals AS g " +
                    "    WHERE g.match_id = r.match_id " +
                    "      AND g.player_id = r.player_id" +
                    ")";

    public MatchRostersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Player> findByMatchIdAndTeamId(int matchId, int teamId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_ROSTER)) {

            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, teamId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Player> players = new ArrayList<>();

                while (resultSet.next()) {
                    Player player = Player.builder()
                            .playerId(resultSet.getInt("player_id"))
                            .lastName(resultSet.getString("lastname"))
                            .name(resultSet.getString("name"))
                            .middleName(resultSet.getString("middle_name"))
                            .birthDate(resultSet.getObject("birthdate", LocalDate.class))
                            .heightCm(resultSet.getObject("height_cm", Integer.class))
                            .weightKg(resultSet.getBigDecimal("weight_kg"))
                            .position(resultSet.getString("position"))
                            .photoId(resultSet.getObject("photo_id", Long.class))
                            .shirtNumber(resultSet.getObject("shirt_number", Integer.class))
                            .build();

                    players.add(player);
                }

                return players;
            }
        }
    }

    @Override
    public boolean save(int matchId, int teamId, int playerId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_ROSTER_PLAYER)) {

            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, teamId);
            preparedStatement.setInt(3, playerId);

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int matchId, int teamId, int playerId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_ROSTER_PLAYER)) {

            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, teamId);
            preparedStatement.setInt(3, playerId);

            return preparedStatement.executeUpdate() == 1;
        }
    }
}