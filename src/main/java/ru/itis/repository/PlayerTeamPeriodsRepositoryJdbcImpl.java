package ru.itis.repository;

import ru.itis.dto.PlayerTeamPeriodDto;
import ru.itis.dto.PlayerTeamPeriodForm;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlayerTeamPeriodsRepositoryJdbcImpl implements PlayerTeamPeriodsRepository {

    private final DataSource dataSource;

    private static final String SQL_SELECT_PERIODS_BY_PLAYER_ID =
            "SELECT p.appearance_id, p.player_id, p.team_id, " +
                    "t.name AS team_name, s.year AS season_year, " +
                    "p.start_date, p.end_date " +
                    "FROM player_team_periods AS p " +
                    "JOIN teams AS t ON t.team_id = p.team_id " +
                    "JOIN seasons AS s ON s.season_id = p.season_id " +
                    "WHERE p.player_id = ? " +
                    "ORDER BY s.year DESC, p.start_date DESC, p.appearance_id DESC";

    private static final String SQL_INSERT_PERIOD =
            "INSERT INTO player_team_periods " +
                    "(player_id, team_id, season_id, start_date, end_date) " +
                    "SELECT ?, ?, season_id, ?, ? " +
                    "FROM seasons WHERE year = ?";

    private static final String SQL_SELECT_PERIOD_BY_ID =
            "SELECT p.appearance_id, p.player_id, p.team_id, " +
                    "t.name AS team_name, s.year AS season_year, " +
                    "p.start_date, p.end_date " +
                    "FROM player_team_periods AS p " +
                    "JOIN teams AS t ON t.team_id = p.team_id " +
                    "JOIN seasons AS s ON s.season_id = p.season_id " +
                    "WHERE p.appearance_id = ?";

    private static final String SQL_CLOSE_PERIOD =
            "UPDATE player_team_periods SET end_date = ? " +
                    "WHERE appearance_id = ? AND end_date IS NULL";

    private static final String SQL_SELECT_ROSTER_ENTRIES_AFTER_DATE =
            "SELECT 1 " +
                    "FROM player_team_periods AS p " +
                    "JOIN match_rosters AS r " +
                    "ON r.player_id = p.player_id AND r.team_id = p.team_id " +
                    "JOIN matches AS m ON m.match_id = r.match_id " +
                    "JOIN tournaments AS t ON t.tournament_id = m.tournament_id " +
                    "WHERE p.appearance_id = ? " +
                    "AND t.season_id = p.season_id " +
                    "AND m.match_date >= p.start_date " +
                    "AND m.match_date > ? " +
                    "LIMIT 1";

    public PlayerTeamPeriodsRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<PlayerTeamPeriodDto> findByPlayerId(int playerId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_PERIODS_BY_PLAYER_ID)) {

            preparedStatement.setInt(1, playerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<PlayerTeamPeriodDto> periods = new ArrayList<>();

                while (resultSet.next()) {
                    PlayerTeamPeriodDto period =
                            PlayerTeamPeriodDto.builder()
                                    .appearanceId(resultSet.getInt("appearance_id"))
                                    .playerId(resultSet.getInt("player_id"))
                                    .teamId(resultSet.getInt("team_id"))
                                    .teamName(resultSet.getString("team_name"))
                                    .seasonYear(resultSet.getInt("season_year"))
                                    .startDate(resultSet.getObject("start_date", LocalDate.class))
                                    .endDate(resultSet.getObject("end_date", LocalDate.class)).build();

                    periods.add(period);
                }

                return periods;
            }
        }
    }

    @Override
    public boolean save(PlayerTeamPeriodForm form) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_INSERT_PERIOD)) {

            preparedStatement.setInt(1, form.getPlayerId());
            preparedStatement.setInt(2, form.getTeamId());

            preparedStatement.setObject(
                    3, form.getStartDate(), Types.DATE
            );

            preparedStatement.setObject(
                    4, form.getEndDate(), Types.DATE
            );

            preparedStatement.setInt(5, form.getSeasonYear());

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public PlayerTeamPeriodDto findById(int appearanceId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_PERIOD_BY_ID)) {

            preparedStatement.setInt(1, appearanceId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return PlayerTeamPeriodDto.builder()
                            .appearanceId(resultSet.getInt("appearance_id"))
                            .playerId(resultSet.getInt("player_id"))
                            .teamId(resultSet.getInt("team_id"))
                            .teamName(resultSet.getString("team_name"))
                            .seasonYear(resultSet.getInt("season_year"))
                            .startDate(resultSet.getObject("start_date", LocalDate.class))
                            .endDate(resultSet.getObject("end_date", LocalDate.class))
                            .build();
                }

                return null;
            }
        }
    }

    @Override
    public boolean closePeriod(int appearanceId, LocalDate endDate)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_CLOSE_PERIOD)) {

            preparedStatement.setObject(1, endDate, Types.DATE);
            preparedStatement.setInt(2, appearanceId);

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean hasRosterEntriesAfterDate(int appearanceId, LocalDate endDate) throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_ROSTER_ENTRIES_AFTER_DATE)) {

            preparedStatement.setInt(1, appearanceId);
            preparedStatement.setObject(2, endDate, Types.DATE);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
