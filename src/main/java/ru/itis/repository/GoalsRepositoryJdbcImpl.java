package ru.itis.repository;

import ru.itis.dto.GoalDto;
import ru.itis.dto.GoalForm;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoalsRepositoryJdbcImpl implements GoalsRepository {

    private final DataSource dataSource;

    private static final String SQL_SELECT_GOALS_BY_MATCH_ID =
            "SELECT g.goal_id, g.match_id, g.player_id, " +
                    "p.lastname, p.name, p.middle_name, " +
                    "g.team_id, t.name AS team_name, g.minute, " +
                    "(r.team_id <> g.team_id) AS own_goal " +
                    "FROM goals AS g " +
                    "JOIN players AS p ON p.player_id = g.player_id " +
                    "JOIN teams AS t ON t.team_id = g.team_id " +
                    "JOIN match_rosters AS r " +
                    "ON r.match_id = g.match_id AND r.player_id = g.player_id " +
                    "WHERE g.match_id = ? " +
                    "ORDER BY g.minute, g.goal_id";

    private static final String SQL_INSERT_GOAL =
            "INSERT INTO goals (match_id, player_id, team_id, minute) " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_COUNT_GOALS_BY_MATCH_AND_TEAM =
            "SELECT COUNT(*) AS goals_count " +
                    "FROM goals WHERE match_id = ? AND team_id = ?";

    private static final String SQL_DELETE_GOAL = "DELETE FROM goals WHERE goal_id = ? AND match_id = ?";

    public GoalsRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<GoalDto> findByMatchId(int matchId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_GOALS_BY_MATCH_ID)) {

            preparedStatement.setInt(1, matchId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<GoalDto> goals = new ArrayList<>();

                while (resultSet.next()) {
                    GoalDto goal = GoalDto.builder()
                            .goalId(resultSet.getInt("goal_id"))
                            .matchId(resultSet.getInt("match_id"))
                            .playerId(resultSet.getInt("player_id"))
                            .lastName(resultSet.getString("lastname"))
                            .name(resultSet.getString("name"))
                            .middleName(resultSet.getString("middle_name"))
                            .teamId(resultSet.getInt("team_id"))
                            .teamName(resultSet.getString("team_name"))
                            .minute(resultSet.getInt("minute"))
                            .ownGoal(resultSet.getBoolean("own_goal"))
                            .build();

                    goals.add(goal);
                }

                return goals;
            }
        }
    }

    @Override
    public void save(GoalForm form) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_INSERT_GOAL)) {

            preparedStatement.setInt(1, form.getMatchId());
            preparedStatement.setInt(2, form.getPlayerId());
            preparedStatement.setInt(3, form.getTeamId());
            preparedStatement.setInt(4, form.getMinute());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public int countByMatchIdAndTeamId(int matchId, int teamId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_COUNT_GOALS_BY_MATCH_AND_TEAM)) {

            preparedStatement.setInt(1, matchId);
            preparedStatement.setInt(2, teamId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("goals_count");
                }

                throw new SQLException("Не удалось получить количество голов");
            }
        }
    }

    @Override
    public boolean delete(int goalId, int matchId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_GOAL)) {

            preparedStatement.setInt(1, goalId);
            preparedStatement.setInt(2, matchId);

            return preparedStatement.executeUpdate() == 1;
        }
    }
}