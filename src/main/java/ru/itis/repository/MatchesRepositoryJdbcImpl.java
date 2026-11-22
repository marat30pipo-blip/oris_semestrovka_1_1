package ru.itis.repository;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.dto.MatchDto;
import ru.itis.dto.MatchForm;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MatchesRepositoryJdbcImpl implements MatchesRepository{

    private final DataSource dataSource;

    private static final String SQL_SELECT_FROM_MATCHES_TEAMS_SEASONS = "SELECT m.match_id, " +
            "m.team1_id, m.team2_id,\n" +
            "       t1.name AS team1_name,\n" +
            "       t2.name AS team2_name,\n" +
            "       t.name AS tournament_name,\n" +
            "       m.match_date, m.start_time, m.round_number, m.stage, m.status, m.score1, m.score2, " +
            "       m.penalty1, m.penalty2 " +
            "FROM matches AS m\n" +
            "JOIN teams AS t1 ON t1.team_id = m.team1_id\n" +
            "JOIN teams AS t2 ON t2.team_id = m.team2_id\n" +
            "JOIN tournaments AS t ON t.tournament_id = m.tournament_id\n" +
            "JOIN seasons AS s ON s.season_id = t.season_id\n" +
            "WHERE s.year = ?\n" +
            "ORDER BY m.match_date DESC NULLS LAST, " +
            "m.start_time DESC NULLS LAST, m.match_id DESC";

    private static final String SQL_SELECT_BY_TOURNAMENT_ID =
            "SELECT m.match_id, m.team1_id, m.team2_id," +
                    "t1.name AS team1_name, " +
                    "t2.name AS team2_name, " +
                    "t.name AS tournament_name, " +
                    "m.match_date, m.start_time, m.stage, m.status, m.score1, m.score2, " +
                    "m.penalty1, m.penalty2 " +
                    "FROM matches AS m " +
                    "JOIN teams AS t1 ON t1.team_id = m.team1_id " +
                    "JOIN teams AS t2 ON t2.team_id = m.team2_id " +
                    "JOIN tournaments AS t ON t.tournament_id = m.tournament_id " +
                    "WHERE m.tournament_id = ? " +
                    "ORDER BY m.match_date ASC NULLS LAST, " +
                    "m.start_time ASC NULLS LAST, m.match_id";

    private static final String SQL_INSERT_MATCH =
            "INSERT INTO matches " +
                    "(tournament_id, team1_id, team2_id, venue_id, " +
                    "match_date, start_time, round_number, status, " +
                    "score1, score2, penalty1, penalty2, stage) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_MATCH_BY_ID =
            "SELECT tournament_id, team1_id, team2_id, venue_id, " +
                    "match_date, start_time, round_number, status, " +
                    "score1, score2, penalty1, penalty2, stage " +
                    "FROM matches WHERE match_id = ?";

    private static final String SQL_UPDATE_MATCH =
            "UPDATE matches SET " +
                    "team1_id = ?, team2_id = ?, venue_id = ?, " +
                    "match_date = ?, start_time = ?, round_number = ?, " +
                    "status = ?, score1 = ?, score2 = ?, " +
                    "penalty1 = ?, penalty2 = ?, stage = ? " +
                    "WHERE match_id = ? AND tournament_id = ?";

    private static final String SQL_DELETE_GOALS_BY_MATCH_ID =
            "DELETE FROM goals WHERE match_id = ?";

    private static final String SQL_DELETE_ROSTERS_BY_MATCH_ID =
            "DELETE FROM match_rosters WHERE match_id = ?";

    private static final String SQL_DELETE_MATCH_BY_ID =
            "DELETE FROM matches WHERE match_id = ?";

    private static final String SQL_SELECT_MATCH_DETAILS_BY_ID =
            "SELECT m.match_id, m.tournament_id, " +
                    "t.name AS tournament_name, t.type AS tournament_type, " +
                    "s.year AS season_year, " +
                    "m.team1_id, t1.name AS team1_name, " +
                    "m.team2_id, t2.name AS team2_name, " +
                    "m.venue_id, v.name AS venue_name, " +
                    "m.match_date, m.start_time, m.round_number, m.stage, m.status, " +
                    "m.score1, m.score2, m.penalty1, m.penalty2 " +
                    "FROM matches AS m " +
                    "JOIN tournaments AS t ON t.tournament_id = m.tournament_id " +
                    "JOIN seasons AS s ON s.season_id = t.season_id " +
                    "JOIN teams AS t1 ON t1.team_id = m.team1_id " +
                    "JOIN teams AS t2 ON t2.team_id = m.team2_id " +
                    "LEFT JOIN venues AS v ON v.venue_id = m.venue_id " +
                    "WHERE m.match_id = ?";

    public MatchesRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<MatchDto> findBySeasonYear(int seasonYear) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_MATCHES_TEAMS_SEASONS)) {
            preparedStatement.setInt(1, seasonYear);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<MatchDto> matches = new ArrayList<>();
            while (resultSet.next()) {
                MatchDto match = MatchDto.builder()
                        .matchId(resultSet.getInt("match_id"))
                        .team1Id(resultSet.getInt("team1_id"))
                        .team2Id(resultSet.getInt("team2_id"))
                        .team1Name(resultSet.getString("team1_name"))
                        .team2Name(resultSet.getString("team2_name"))
                        .tournamentName(resultSet.getString("tournament_name"))
                        .matchDate(resultSet.getObject("match_date", LocalDate.class))
                        .startTime(resultSet.getObject("start_time", LocalTime.class))
                        .status(resultSet.getString("status"))
                        .score1(resultSet.getObject("score1", Integer.class))
                        .score2(resultSet.getObject("score2", Integer.class))
                        .stage(resultSet.getString("stage"))
                        .penalty1(resultSet.getObject("penalty1", Integer.class))
                        .penalty2(resultSet.getObject("penalty2", Integer.class))
                        .roundNumber(resultSet.getObject("round_number", Integer.class))
                        .build();
                matches.add(match);
            }
            return matches;
        }
    }

    @Override
    public List<MatchDto> findByTournamentId(int tournamentId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_BY_TOURNAMENT_ID)){
            preparedStatement.setInt(1, tournamentId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<MatchDto> matches = new ArrayList<>();
            while (resultSet.next()) {
                MatchDto matchDto = MatchDto.builder()
                        .matchId(resultSet.getInt("match_id"))
                        .team1Id(resultSet.getInt("team1_id"))
                        .team2Id(resultSet.getInt("team2_id"))
                        .team1Name(resultSet.getString("team1_name"))
                        .team2Name(resultSet.getString("team2_name"))
                        .tournamentName(resultSet.getString("tournament_name"))
                        .matchDate(resultSet.getObject("match_date", LocalDate.class))
                        .startTime(resultSet.getObject("start_time", LocalTime.class))
                        .status(resultSet.getString("status"))
                        .score1(resultSet.getObject("score1", Integer.class))
                        .score2(resultSet.getObject("score2", Integer.class))
                        .stage(resultSet.getString("stage"))
                        .penalty1(resultSet.getObject("penalty1", Integer.class))
                        .penalty2(resultSet.getObject("penalty2", Integer.class))
                        .build();

                matches.add(matchDto);
            }
            return matches;
        }
    }

    @Override
    public void save(MatchForm form) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_INSERT_MATCH)) {
            preparedStatement.setInt(1, form.getTournamentId());
            preparedStatement.setInt(2, form.getTeam1Id());
            preparedStatement.setInt(3, form.getTeam2Id());
            preparedStatement.setObject(4, form.getVenueId(), Types.INTEGER);
            preparedStatement.setObject(5, form.getMatchDate(), Types.DATE);
            preparedStatement.setObject(6, form.getStartTime(), Types.TIME);
            preparedStatement.setObject(7, form.getRoundNumber(), Types.INTEGER);
            preparedStatement.setString(8, form.getStatus());
            preparedStatement.setObject(9, form.getScore1(), Types.INTEGER);
            preparedStatement.setObject(10, form.getScore2(), Types.INTEGER);
            preparedStatement.setObject(11, form.getPenalty1(), Types.INTEGER);
            preparedStatement.setObject(12, form.getPenalty2(), Types.INTEGER);
            preparedStatement.setString(13, form.getStage());

            preparedStatement.executeUpdate();
        }
    }

    @Override
    public MatchForm findById(int matchId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_MATCH_BY_ID)) {

            preparedStatement.setInt(1, matchId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return MatchForm.builder()
                            .tournamentId(resultSet.getInt("tournament_id"))
                            .team1Id(resultSet.getInt("team1_id"))
                            .team2Id(resultSet.getInt("team2_id"))
                            .venueId(resultSet.getObject("venue_id", Integer.class))
                            .matchDate(resultSet.getObject("match_date", LocalDate.class))
                            .startTime(resultSet.getObject("start_time", LocalTime.class))
                            .roundNumber(resultSet.getObject("round_number", Integer.class))
                            .status(resultSet.getString("status"))
                            .score1(resultSet.getObject("score1", Integer.class))
                            .score2(resultSet.getObject("score2", Integer.class))
                            .penalty1(resultSet.getObject("penalty1", Integer.class))
                            .penalty2(resultSet.getObject("penalty2", Integer.class))
                            .stage(resultSet.getString("stage"))
                            .build();
                }

                return null;
            }
        }
    }

    @Override
    public boolean update(int matchId, MatchForm form) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_UPDATE_MATCH)) {

            preparedStatement.setInt(1, form.getTeam1Id());
            preparedStatement.setInt(2, form.getTeam2Id());
            preparedStatement.setObject(3, form.getVenueId(), Types.INTEGER);
            preparedStatement.setObject(4, form.getMatchDate(), Types.DATE);
            preparedStatement.setObject(5, form.getStartTime(), Types.TIME);
            preparedStatement.setObject(6, form.getRoundNumber(), Types.INTEGER);
            preparedStatement.setString(7, form.getStatus());
            preparedStatement.setObject(8, form.getScore1(), Types.INTEGER);
            preparedStatement.setObject(9, form.getScore2(), Types.INTEGER);
            preparedStatement.setObject(10, form.getPenalty1(), Types.INTEGER);
            preparedStatement.setObject(11, form.getPenalty2(), Types.INTEGER);
            preparedStatement.setString(12, form.getStage());
            preparedStatement.setInt(13, matchId);
            preparedStatement.setInt(14, form.getTournamentId());

            return preparedStatement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean deleteById(int matchId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteGoals = connection.prepareStatement(SQL_DELETE_GOALS_BY_MATCH_ID);
                 PreparedStatement deleteRosters = connection.prepareStatement(SQL_DELETE_ROSTERS_BY_MATCH_ID);
                 PreparedStatement deleteMatch = connection.prepareStatement(SQL_DELETE_MATCH_BY_ID)) {

                deleteGoals.setInt(1, matchId);
                deleteGoals.executeUpdate();

                deleteRosters.setInt(1, matchId);
                deleteRosters.executeUpdate();

                deleteMatch.setInt(1, matchId);
                int deletedMatches = deleteMatch.executeUpdate();

                connection.commit();

                return deletedMatches == 1;

            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }

    @Override
    public MatchDetailsDto findDetailsById(int matchId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_MATCH_DETAILS_BY_ID)) {

            preparedStatement.setInt(1, matchId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return MatchDetailsDto.builder()
                            .matchId(resultSet.getInt("match_id"))
                            .tournamentId(resultSet.getInt("tournament_id"))
                            .tournamentName(resultSet.getString("tournament_name"))
                            .tournamentType(resultSet.getString("tournament_type"))
                            .seasonYear(resultSet.getInt("season_year"))
                            .team1Id(resultSet.getInt("team1_id"))
                            .team1Name(resultSet.getString("team1_name"))
                            .team2Id(resultSet.getInt("team2_id"))
                            .team2Name(resultSet.getString("team2_name"))
                            .venueId(resultSet.getObject("venue_id", Integer.class))
                            .venueName(resultSet.getString("venue_name"))
                            .matchDate(resultSet.getObject("match_date", LocalDate.class))
                            .startTime(resultSet.getObject("start_time", LocalTime.class))
                            .roundNumber(resultSet.getObject("round_number", Integer.class))
                            .status(resultSet.getString("status"))
                            .score1(resultSet.getObject("score1", Integer.class))
                            .score2(resultSet.getObject("score2", Integer.class))
                            .penalty1(resultSet.getObject("penalty1", Integer.class))
                            .penalty2(resultSet.getObject("penalty2", Integer.class))
                            .stage(resultSet.getString("stage"))
                            .build();
                }

                return null;
            }
        }
    }
}
