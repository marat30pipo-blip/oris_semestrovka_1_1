package ru.itis.repository;

import ru.itis.dto.TournamentForm;
import ru.itis.models.Tournament;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TournamentsRepositoryJdbcImpl implements TournamentsRepository {

    private final DataSource dataSource;
    private static final String SQL_SELECT_FROM_TOURNAMENTS_SEASONS = "SELECT t.tournament_id, t.season_id, t.name, t.type,\n" +
            "       t.start_date, t.end_date, t.description\n" +
            "FROM tournaments AS t\n" +
            "JOIN seasons AS s ON s.season_id = t.season_id\n" +
            "WHERE s.year = ?\n" +
            "ORDER BY t.start_date, t.tournament_id";
    private static final String SQL_SELECT_FROM_TOURNAMENTS_BY_ID =
            "SELECT tournament_id, season_id, name, type, " +
                    "start_date, end_date, description " +
                    "FROM tournaments WHERE tournament_id = ?";

    private static final String SQL_INSERT_TOURNAMENT =
            "INSERT INTO tournaments " +
                    "(season_id, name, type, start_date, end_date, description) " +
                    "SELECT season_id, ?, ?, ?, ?, ? " +
                    "FROM seasons WHERE year = ?";

    private static final String SQL_UPDATE_TOURNAMENT =
            "UPDATE tournaments SET " +
                    "name = ?, start_date = ?, end_date = ?, description = ? " +
                    "WHERE tournament_id = ?";

    private static final String SQL_SELECT_TOURNAMENTS_BY_TEAM_AND_SEASON =
            "SELECT t.tournament_id, t.season_id, t.name, t.type, " +
                    "t.start_date, t.end_date, t.description " +
                    "FROM tournaments AS t " +
                    "JOIN tournament_teams AS tt " +
                    "ON tt.tournament_id = t.tournament_id " +
                    "JOIN seasons AS s ON s.season_id = t.season_id " +
                    "WHERE tt.team_id = ? AND s.year = ? " +
                    "ORDER BY t.start_date, t.tournament_id";

    private static final String SQL_SELECT_MATCH_OUTSIDE_DATES =
            "SELECT 1 FROM matches " +
                    "WHERE tournament_id = ? " +
                    "AND (match_date < ? OR match_date > ?) " +
                    "LIMIT 1";

    public TournamentsRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Tournament> findBySeasonYear(int seasonYear) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =  connection.prepareStatement(SQL_SELECT_FROM_TOURNAMENTS_SEASONS )){
            List<Tournament> tournaments = new ArrayList<>();
            preparedStatement.setInt(1, seasonYear);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Tournament tournament = Tournament.builder()
                        .tournamentId(resultSet.getInt("tournament_id"))
                        .seasonId(resultSet.getInt("season_id"))
                        .name(resultSet.getString("name"))
                        .type(resultSet.getString("type"))
                        .startDate(resultSet.getObject("start_date", LocalDate.class))
                        .endDate(resultSet.getObject("end_date", LocalDate.class))
                        .description(resultSet.getString("description"))
                        .build();
                tournaments.add(tournament);
            }
            return tournaments;
        }
    }

    @Override
    public Tournament findById(int tournamentId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_TOURNAMENTS_BY_ID)){
            preparedStatement.setInt(1, tournamentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Tournament tournament = Tournament.builder()
                        .tournamentId(resultSet.getInt("tournament_id"))
                        .seasonId(resultSet.getInt("season_id"))
                        .name(resultSet.getString("name"))
                        .type(resultSet.getString("type"))
                        .startDate(resultSet.getObject("start_date", LocalDate.class))
                        .endDate(resultSet.getObject("end_date", LocalDate.class))
                        .description(resultSet.getString("description"))
                        .build();
                return tournament;
            }
            return null;
        }
    }

    @Override
    public boolean save(TournamentForm form) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_INSERT_TOURNAMENT)) {

            preparedStatement.setString(1, form.getName());
            preparedStatement.setString(2, form.getType());
            preparedStatement.setObject(3, form.getStartDate(), Types.DATE);
            preparedStatement.setObject(4, form.getEndDate(), Types.DATE);
            preparedStatement.setString(5, form.getDescription());
            preparedStatement.setInt(6, form.getSeasonYear());

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean update(int tournamentId, TournamentForm form)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_TOURNAMENT)) {

            preparedStatement.setString(1, form.getName());
            preparedStatement.setObject(2, form.getStartDate(), Types.DATE);
            preparedStatement.setObject(3, form.getEndDate(), Types.DATE);
            preparedStatement.setString(4, form.getDescription());
            preparedStatement.setInt(5, tournamentId);

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public List<Tournament> findByTeamIdAndSeasonYear(
            int teamId, int seasonYear) throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             SQL_SELECT_TOURNAMENTS_BY_TEAM_AND_SEASON)) {

            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, seasonYear);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Tournament> tournaments = new ArrayList<>();

                while (resultSet.next()) {
                    Tournament tournament = Tournament.builder()
                            .tournamentId(resultSet.getInt("tournament_id"))
                            .seasonId(resultSet.getInt("season_id"))
                            .name(resultSet.getString("name"))
                            .type(resultSet.getString("type"))
                            .startDate(resultSet.getObject(
                                    "start_date", LocalDate.class))
                            .endDate(resultSet.getObject(
                                    "end_date", LocalDate.class))
                            .description(resultSet.getString("description"))
                            .build();

                    tournaments.add(tournament);
                }

                return tournaments;
            }
        }
    }

    @Override
    public boolean hasMatchesOutsideDates(int tournamentId, LocalDate startDate, LocalDate endDate) throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MATCH_OUTSIDE_DATES)) {

            preparedStatement.setInt(1, tournamentId);
            preparedStatement.setObject(2, startDate, Types.DATE);
            preparedStatement.setObject(3, endDate, Types.DATE);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
