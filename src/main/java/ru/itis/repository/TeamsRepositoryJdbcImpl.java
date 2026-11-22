package ru.itis.repository;

import ru.itis.models.Team;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeamsRepositoryJdbcImpl implements TeamsRepository{

    private DataSource dataSource;

    private static final String SQL_SELECT_FROM_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID = "SELECT t.team_id, t.name, t.founded_on, t.emblem_id, t.description\n" +
            "FROM teams AS t\n" +
            "JOIN tournament_teams AS tt ON tt.team_id = t.team_id\n" +
            "WHERE tt.tournament_id = ?\n" +
            "ORDER BY t.name, t.team_id";

    private static final String SQL_INSERT_INTO_TEAMS =
            "INSERT INTO teams (name, description, emblem_id) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_ALL_TEAMS =
            "SELECT team_id, name, founded_on, emblem_id, description " +
                    "FROM teams ORDER BY name, team_id";

    private static final String SQL_SELECT_TEAM_BY_ID =
            "SELECT team_id, name, founded_on, emblem_id, description " +
                    "FROM teams WHERE team_id = ?";

    private static final String SQL_UPDATE_TEAM =
            "UPDATE teams SET name = ?, description = ? WHERE team_id = ?";

    private static final String SQL_UPDATE_TEAM_EMBLEM =
            "UPDATE teams SET emblem_id = ? WHERE team_id = ?";

    private static final String SQL_INSERT_TOURNAMENT_TEAM =
            "INSERT INTO tournament_teams (tournament_id, team_id) " +
                    "VALUES (?, ?) ON CONFLICT (tournament_id, team_id) DO NOTHING";

    public TeamsRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean addToTournament(int tournamentId, int teamId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_TOURNAMENT_TEAM)){
            preparedStatement.setInt(1, tournamentId);
            preparedStatement.setInt(2, teamId);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public List<Team> findByTournamentId(int tournamentId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_TOURNAMENTS_TEAMS_BY_TOURNAMENT_ID)){
            preparedStatement.setInt(1, tournamentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Team> teams = new ArrayList<>();
            while (resultSet.next()) {
                Team team = Team.builder()
                        .teamId(resultSet.getInt("team_id"))
                        .name(resultSet.getString("name"))
                        .foundedOn(resultSet.getObject("founded_on", LocalDate.class))
                        .emblemId(resultSet.getObject("emblem_id", Long.class))
                        .description(resultSet.getString("description"))
                        .build();
                teams.add(team);
            }
            return teams;
        }
    }

    @Override
    public void save(String name, String description, Long emblemId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_INTO_TEAMS)){
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);

            if (emblemId == null) {
                preparedStatement.setNull(3, Types.BIGINT);
            }
            else {
                preparedStatement.setLong(3, emblemId);
            }

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Team> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ALL_TEAMS)){
            List<Team> teams = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Team team = Team.builder()
                        .teamId(resultSet.getInt("team_id"))
                        .name(resultSet.getString("name"))
                        .foundedOn(resultSet.getObject("founded_on", LocalDate.class))
                        .emblemId(resultSet.getObject("emblem_id", Long.class))
                        .description(resultSet.getString("description"))
                        .build();
                teams.add(team);
            }
            return teams;
        }


    }

    @Override
    public Team findById(int teamId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_TEAM_BY_ID)){
            preparedStatement.setInt(1, teamId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Team team = Team.builder()
                        .teamId(resultSet.getInt("team_id"))
                        .name(resultSet.getString("name"))
                        .foundedOn(resultSet.getObject("founded_on", LocalDate.class))
                        .emblemId(resultSet.getObject("emblem_id", Long.class))
                        .description(resultSet.getString("description"))
                        .build();
                return team;
            }
            return null;

        }
    }

    @Override
    public boolean update(int teamId, String name, String description) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TEAM)){
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, teamId);
            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean updateEmblem(int teamId, Long emblemId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TEAM_EMBLEM)){
            if (emblemId == null) {
                preparedStatement.setNull(1, Types.BIGINT);
            }
            else {
                preparedStatement.setLong(1, emblemId);
            }
            preparedStatement.setInt(2, teamId);

            return preparedStatement.executeUpdate() == 1;
        }
    }


}
