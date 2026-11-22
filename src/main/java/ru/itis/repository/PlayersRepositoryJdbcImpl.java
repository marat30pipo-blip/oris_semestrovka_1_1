package ru.itis.repository;

import ru.itis.dto.PlayerForm;
import ru.itis.models.Player;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlayersRepositoryJdbcImpl implements PlayersRepository {

    private final DataSource dataSource;

    private static final String SQL_SELECT_ALL_PLAYERS =
            "SELECT player_id, lastname, name, middle_name, " +
                    "birthdate, height_cm, weight_kg, position, photo_id, shirt_number " +
                    "FROM players " +
                    "ORDER BY lastname, name, middle_name, player_id";

    private static final String SQL_INSERT_PLAYER =
            "INSERT INTO players " +
                    "(lastname, name, middle_name, birthdate, " +
                    "height_cm, weight_kg, position, photo_id, shirt_number) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_PLAYER_BY_ID =
            "SELECT player_id, lastname, name, middle_name, " +
                    "birthdate, height_cm, weight_kg, position, photo_id, shirt_number " +
                    "FROM players WHERE player_id = ?";

    private static final String SQL_UPDATE_PLAYER =
            "UPDATE players SET " +
                    "lastname = ?, name = ?, middle_name = ?, birthdate = ?, " +
                    "height_cm = ?, weight_kg = ?, position = ?, shirt_number = ? " +
                    "WHERE player_id = ?";

    private static final String SQL_UPDATE_PLAYER_PHOTO =
            "UPDATE players SET photo_id = ? WHERE player_id = ?";

    private static final String SQL_SELECT_PLAYERS_BY_TEAM_AND_SEASON =
            "SELECT p.player_id, p.lastname, p.name, p.middle_name, " +
                    "p.birthdate, p.height_cm, p.weight_kg, p.position, " +
                    "p.photo_id, p.shirt_number " +
                    "FROM players AS p " +
                    "WHERE EXISTS (" +
                    "    SELECT 1 FROM player_team_periods AS pt " +
                    "    JOIN seasons AS s ON s.season_id = pt.season_id " +
                    "    WHERE pt.player_id = p.player_id " +
                    "      AND pt.team_id = ? " +
                    "      AND s.year = ?" +
                    ") " +
                    "ORDER BY p.lastname, p.name, p.middle_name, p.player_id";

    private static final String SQL_SELECT_PLAYERS_BY_TEAM_SEASON_AND_DATE =
            "SELECT p.player_id, p.lastname, p.name, p.middle_name, " +
                    "p.birthdate, p.height_cm, p.weight_kg, p.position, " +
                    "p.photo_id, p.shirt_number " +
                    "FROM players AS p " +
                    "WHERE EXISTS (" +
                    "    SELECT 1 FROM player_team_periods AS pt " +
                    "    JOIN seasons AS s ON s.season_id = pt.season_id " +
                    "    WHERE pt.player_id = p.player_id " +
                    "      AND pt.team_id = ? " +
                    "      AND s.year = ? " +
                    "      AND pt.start_date <= ? " +
                    "      AND (pt.end_date IS NULL OR pt.end_date >= ?)" +
                    ") " +
                    "ORDER BY p.lastname, p.name, p.middle_name, p.player_id";

    public PlayersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Player> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_ALL_PLAYERS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

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

    @Override
    public void save(PlayerForm form, Long photoId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_INSERT_PLAYER)) {

            preparedStatement.setString(1, form.getLastName());
            preparedStatement.setString(2, form.getName());
            preparedStatement.setString(3, form.getMiddleName());
            preparedStatement.setObject(4, form.getBirthDate(), Types.DATE);
            preparedStatement.setObject(5, form.getHeightCm(), Types.INTEGER);
            preparedStatement.setBigDecimal(6, form.getWeightKg());
            preparedStatement.setString(7, form.getPosition());
            preparedStatement.setObject(8, photoId, Types.BIGINT);
            preparedStatement.setObject(9, form.getShirtNumber(), Types.INTEGER);

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public Player findById(int playerId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_PLAYER_BY_ID)) {

            preparedStatement.setInt(1, playerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Player.builder()
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
                }

                return null;
            }
        }
    }

    @Override
    public boolean update(int playerId, PlayerForm form) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_UPDATE_PLAYER)) {

            preparedStatement.setString(1, form.getLastName());
            preparedStatement.setString(2, form.getName());
            preparedStatement.setString(3, form.getMiddleName());
            preparedStatement.setObject(4, form.getBirthDate(), Types.DATE);
            preparedStatement.setObject(5, form.getHeightCm(), Types.INTEGER);
            preparedStatement.setBigDecimal(6, form.getWeightKg());
            preparedStatement.setString(7, form.getPosition());
            preparedStatement.setObject(8, form.getShirtNumber(), Types.INTEGER);
            preparedStatement.setInt(9, playerId);

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updatePhoto(int playerId, Long photoId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_UPDATE_PLAYER_PHOTO)) {

            preparedStatement.setObject(1, photoId, Types.BIGINT);
            preparedStatement.setInt(2, playerId);

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public List<Player> findByTeamIdAndSeasonYear(
            int teamId, int seasonYear) throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_PLAYERS_BY_TEAM_AND_SEASON)) {

            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, seasonYear);

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
    public List<Player> findByTeamIdAndSeasonYearAndDate(int teamId, int seasonYear, LocalDate matchDate) throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_PLAYERS_BY_TEAM_SEASON_AND_DATE)) {

            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, seasonYear);
            preparedStatement.setObject(3, matchDate, Types.DATE);
            preparedStatement.setObject(4, matchDate, Types.DATE);

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
}