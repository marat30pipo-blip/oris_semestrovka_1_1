package ru.itis.services;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.models.Player;
import ru.itis.repository.MatchRostersRepository;
import ru.itis.repository.MatchesRepository;
import ru.itis.repository.PlayersRepository;

import java.sql.SQLException;
import java.util.List;

public class MatchRostersServiceImpl implements MatchRostersService {

    private final MatchRostersRepository rostersRepository;
    private final MatchesRepository matchesRepository;
    private final PlayersRepository playersRepository;

    public MatchRostersServiceImpl(
            MatchRostersRepository rostersRepository,
            MatchesRepository matchesRepository,
            PlayersRepository playersRepository) {

        this.rostersRepository = rostersRepository;
        this.matchesRepository = matchesRepository;
        this.playersRepository = playersRepository;
    }

    @Override
    public List<Player> findByMatchIdAndTeamId(int matchId, int teamId) throws SQLException {

        if (matchId <= 0 || teamId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча или команды");
        }

        return rostersRepository.findByMatchIdAndTeamId(matchId, teamId);
    }

    @Override
    public List<Player> findEligiblePlayers(int matchId, int teamId) throws SQLException {

        if (matchId <= 0 || teamId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча или команды");
        }

        MatchDetailsDto match = matchesRepository.findDetailsById(matchId);

        if (match == null) {
            throw new IllegalArgumentException("Матч не найден");
        }

        if (teamId != match.getTeam1Id() && teamId != match.getTeam2Id()) {
            throw new IllegalArgumentException("Команда не участвует в этом матче");
        }

        if (match.getMatchDate() == null) {
            throw new IllegalArgumentException("Сначала укажите дату матча");
        }

        return playersRepository.findByTeamIdAndSeasonYearAndDate(teamId, match.getSeasonYear(), match.getMatchDate());
    }

    @Override
    public boolean save(int matchId, int teamId, int playerId) throws SQLException {

        if (playerId <= 0) {
            throw new IllegalArgumentException("Выберите игрока");
        }

        List<Player> eligiblePlayers = findEligiblePlayers(matchId, teamId);

        boolean playerFound = false;

        for (Player player : eligiblePlayers) {
            if (player.getPlayerId() == playerId) {
                playerFound = true;
                break;
            }
        }

        if (!playerFound) {
            throw new IllegalArgumentException("Игрок не выступает за выбранную команду на дату матча в этом сезоне");
        }

        return rostersRepository.save(matchId, teamId, playerId);
    }

    @Override
    public boolean delete(int matchId, int teamId, int playerId) throws SQLException {

        if (matchId <= 0 || teamId <= 0 || playerId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча, команды или игрока");
        }

        MatchDetailsDto match = matchesRepository.findDetailsById(matchId);

        if (match == null) {
            throw new IllegalArgumentException("Матч не найден");
        }

        if (teamId != match.getTeam1Id() && teamId != match.getTeam2Id()) {
            throw new IllegalArgumentException("Команда не участвует в этом матче");
        }

        return rostersRepository.delete(matchId, teamId, playerId);
    }


}