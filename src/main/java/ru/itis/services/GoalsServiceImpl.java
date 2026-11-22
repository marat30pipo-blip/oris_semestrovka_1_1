package ru.itis.services;

import ru.itis.dto.GoalDto;
import ru.itis.dto.GoalForm;
import ru.itis.dto.MatchDetailsDto;
import ru.itis.models.Player;
import ru.itis.repository.GoalsRepository;
import ru.itis.repository.MatchRostersRepository;
import ru.itis.repository.MatchesRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoalsServiceImpl implements GoalsService {

    private final GoalsRepository goalsRepository;
    private final MatchesRepository matchesRepository;
    private final MatchRostersRepository rostersRepository;

    public GoalsServiceImpl(
            GoalsRepository goalsRepository,
            MatchesRepository matchesRepository,
            MatchRostersRepository rostersRepository) {

        this.goalsRepository = goalsRepository;
        this.matchesRepository = matchesRepository;
        this.rostersRepository = rostersRepository;
    }

    @Override
    public List<GoalDto> findByMatchId(int matchId) throws SQLException {

        if (matchId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча");
        }

        return goalsRepository.findByMatchId(matchId);
    }

    @Override
    public void save(GoalForm form) throws SQLException {
        if (form == null) {
            throw new IllegalArgumentException("Данные гола не переданы");
        }

        if (form.getMatchId() <= 0 || form.getPlayerId() <= 0 || form.getTeamId() <= 0) {
            throw new IllegalArgumentException("Выберите матч, игрока и команду");
        }

        if (form.getMinute() <= 0) {
            throw new IllegalArgumentException("Минута гола должна быть положительным числом");
        }

        MatchDetailsDto match = matchesRepository.findDetailsById(form.getMatchId());

        if (match == null) {
            throw new IllegalArgumentException("Матч не найден");
        }

        if (!"Завершён".equals(match.getStatus())) {
            throw new IllegalArgumentException("Голы можно добавлять только в завершённый матч");
        }

        if (match.getScore1() == null || match.getScore2() == null) {
            throw new IllegalArgumentException("Сначала укажите официальный счёт матча");
        }

        if (form.getTeamId() != match.getTeam1Id() && form.getTeamId() != match.getTeam2Id()) {
            throw new IllegalArgumentException("Команда не участвует в этом матче");
        }

        List<Player> players = new ArrayList<>();

        players.addAll(rostersRepository.findByMatchIdAndTeamId(form.getMatchId(), match.getTeam1Id()));

        players.addAll(rostersRepository.findByMatchIdAndTeamId(form.getMatchId(), match.getTeam2Id()));

        boolean playerFound = false;

        for (Player player : players) {
            if (player.getPlayerId() == form.getPlayerId()) {
                playerFound = true;
                break;
            }
        }

        if (!playerFound) {
            throw new IllegalArgumentException("Автор гола должен входить в состав одной из команд матча");
        }

        int officialScore;

        if (form.getTeamId() == match.getTeam1Id()) {
            officialScore = match.getScore1();
        } else {
            officialScore = match.getScore2();
        }

        int recordedGoals = goalsRepository.countByMatchIdAndTeamId(form.getMatchId(), form.getTeamId());

        if (recordedGoals >= officialScore) {
            throw new IllegalArgumentException("Для этой команды уже внесены все голы по официальному счёту");
        }

        goalsRepository.save(form);
    }

    @Override
    public boolean delete(int goalId, int matchId) throws SQLException {
        if (goalId <= 0 || matchId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор гола или матча");
        }
        return goalsRepository.delete(goalId, matchId);
    }
}