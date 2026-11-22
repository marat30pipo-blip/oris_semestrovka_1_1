package ru.itis.services;

import ru.itis.dto.MatchDetailsDto;
import ru.itis.dto.MatchDto;
import ru.itis.dto.MatchForm;
import ru.itis.models.Team;
import ru.itis.models.Tournament;
import ru.itis.repository.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchesServiceImpl implements MatchesService{

    private final MatchesRepository matchesRepository;
    private final TeamsRepository teamsRepository;
    private final TournamentsRepository tournamentsRepository;
    private final VenuesRepository venuesRepository;
    private final GoalsRepository goalsRepository;
    private final MatchRostersRepository rostersRepository;

    public MatchesServiceImpl(
            MatchesRepository matchesRepository,
            TeamsRepository teamsRepository,
            TournamentsRepository tournamentsRepository,
            VenuesRepository venuesRepository,
            GoalsRepository goalsRepository,
            MatchRostersRepository rostersRepository) {

        this.matchesRepository = matchesRepository;
        this.teamsRepository = teamsRepository;
        this.tournamentsRepository = tournamentsRepository;
        this.venuesRepository = venuesRepository;
        this.goalsRepository = goalsRepository;
        this.rostersRepository = rostersRepository;
    }

    @Override
    public List<MatchDto> findResults(List<MatchDto> matches) throws SQLException {
        List<MatchDto> matchesByStatus = new ArrayList<>();
        for (MatchDto match: matches) {
            if (match.getStatus().equals("Завершён") || match.getStatus().equals("Технический результат")) {
                matchesByStatus.add(match);
            }
        }

        return matchesByStatus;
    }

    @Override
    public List<MatchDto> findByTournamentId(int tournamentId) throws SQLException {
        return matchesRepository.findByTournamentId(tournamentId);
    }

    @Override
    public void save(MatchForm form) throws SQLException {
        validateForm(form);
        matchesRepository.save(form);
    }

    @Override
    public MatchForm findById(int matchId) throws SQLException {
        if (matchId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча");
        }

        return matchesRepository.findById(matchId);
    }

    @Override
    public boolean update(int matchId, MatchForm form) throws SQLException {

        if (matchId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча");
        }

        MatchForm existingMatch = matchesRepository.findById(matchId);

        if (existingMatch == null) {
            return false;
        }

        validateForm(form);

        if (form.getTournamentId() != existingMatch.getTournamentId()) {
            throw new IllegalArgumentException("Нельзя переносить матч в другой турнир");
        }

        validateRosterChanges(matchId, existingMatch, form);

        int team1Goals = goalsRepository.countByMatchIdAndTeamId(matchId, existingMatch.getTeam1Id());
        int team2Goals = goalsRepository.countByMatchIdAndTeamId(matchId, existingMatch.getTeam2Id());

        if (team1Goals > 0 || team2Goals > 0) {
            if (form.getTeam1Id() != existingMatch.getTeam1Id() || form.getTeam2Id() != existingMatch.getTeam2Id()) {
                throw new IllegalArgumentException("Перед изменением команд удалите записи голов");
            }

            if (!"Завершён".equals(form.getStatus())) {
                throw new IllegalArgumentException("Перед изменением статуса удалите записи голов");
            }

            if (form.getScore1() < team1Goals || form.getScore2() < team2Goals) {
                throw new IllegalArgumentException("Официальный счёт не может быть меньше количества внесённых голов команды");
            }
        }

        return matchesRepository.update(matchId, form);
    }

    @Override
    public boolean deleteById(int matchId) throws SQLException {
        if (matchId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча");
        }

        return matchesRepository.deleteById(matchId);
    }

    @Override
    public List<MatchDto> findByTournamentIdAndStatus(
            int tournamentId, String status) throws SQLException {

        if (tournamentId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор турнира");
        }

        boolean allStatuses = status == null || status.isBlank();

        if (!allStatuses
                && !"Запланирован".equals(status)
                && !"Перенесён".equals(status)
                && !"Завершён".equals(status)
                && !"Отменён".equals(status)
                && !"Технический результат".equals(status)) {

            throw new IllegalArgumentException("Неизвестный статус матча");
        }

        List<MatchDto> matches = matchesRepository.findByTournamentId(tournamentId);

        if (allStatuses) {
            return matches;
        }

        List<MatchDto> filteredMatches = new ArrayList<>();

        for (MatchDto match : matches) {
            if (status.equals(match.getStatus())) {
                filteredMatches.add(match);
            }
        }

        return filteredMatches;
    }

    @Override
    public List<MatchDto> findBySeasonYear(int seasonYear) throws SQLException {
        return matchesRepository.findBySeasonYear(seasonYear);
    }

    private void validateTeams(MatchForm form) throws SQLException {
        if (form == null) {
            throw new IllegalArgumentException("Данные матча не переданы");
        }

        if (form.getTournamentId() <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор турнира");
        }

        if (form.getTeam1Id() <= 0 || form.getTeam2Id() <= 0) {
            throw new IllegalArgumentException("Выберите две команды");
        }

        if (form.getTeam1Id() == form.getTeam2Id()) {
            throw new IllegalArgumentException("Команда не может играть сама с собой");
        }

        List<Team> teams = teamsRepository.findByTournamentId(form.getTournamentId());

        boolean firstTeamFound = false;
        boolean secondTeamFound = false;

        for (Team team : teams) {
            if (team.getTeamId() == form.getTeam1Id()) {
                firstTeamFound = true;
            }

            if (team.getTeamId() == form.getTeam2Id()) {
                secondTeamFound = true;
            }
        }

        if (!firstTeamFound || !secondTeamFound) {
            throw new IllegalArgumentException("Обе команды должны участвовать в выбранном турнире");
        }
    }

    @Override
    public MatchDetailsDto findDetailsById(int matchId) throws SQLException {

        if (matchId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор матча");
        }

        return matchesRepository.findDetailsById(matchId);
    }

    private void validateStatusAndScore(MatchForm form) {
        String status = form.getStatus();

        if (!"Запланирован".equals(status)
                && !"Перенесён".equals(status)
                && !"Завершён".equals(status)
                && !"Отменён".equals(status)
                && !"Технический результат".equals(status)) {

            throw new IllegalArgumentException("Выберите допустимый статус матча");
        }

        Integer score1 = form.getScore1();
        Integer score2 = form.getScore2();

        if ("Завершён".equals(status) || "Технический результат".equals(status)) {

            if (score1 == null || score2 == null) {
                throw new IllegalArgumentException("Для матча с результатом укажите счёт обеих команд");
            }

            if (score1 < 0 || score2 < 0) {
                throw new IllegalArgumentException("Количество голов не может быть отрицательным");
            }

        } else {
            if (score1 != null || score2 != null) {
                throw new IllegalArgumentException("Счёт можно указать только для завершённого матча или технического результата");
            }
        }

        if (form.getRoundNumber() != null && form.getRoundNumber() <= 0) {
            throw new IllegalArgumentException("Номер тура должен быть положительным");
        }
    }

    private void validatePenaltiesAndTechnicalResult(MatchForm form, Tournament tournament) {

        String status = form.getStatus();

        Integer penalty1 = form.getPenalty1();
        Integer penalty2 = form.getPenalty2();

        if ("Технический результат".equals(status)) {
            boolean firstTeamWon = form.getScore1() == 5 && form.getScore2() == 0;

            boolean secondTeamWon = form.getScore1() == 0 && form.getScore2() == 5;

            if (!firstTeamWon && !secondTeamWon) {
                throw new IllegalArgumentException("Технический результат должен быть 5 : 0 или 0 : 5");
            }
        }

        boolean finished = "Завершён".equals(status);
        boolean cup = "Кубок".equals(tournament.getType());

        if (!finished || !cup) {
            if (penalty1 != null || penalty2 != null) {
                throw new IllegalArgumentException("Пенальти можно указать только для завершённого матча кубка");
            }

            return;
        }

        boolean draw = form.getScore1().equals(form.getScore2());

        if (!draw) {
            if (penalty1 != null || penalty2 != null) {
                throw new IllegalArgumentException("Серия пенальти возможна только при ничейном счёте");
            }

            return;
        }

        if (penalty1 == null || penalty2 == null) {
            throw new IllegalArgumentException("При ничьей в кубке укажите результат серии пенальти");
        }

        if (penalty1 < 0 || penalty2 < 0) {
            throw new IllegalArgumentException("Количество голов в серии пенальти не может быть отрицательным");
        }

        if (penalty1.equals(penalty2)) {
            throw new IllegalArgumentException("В серии пенальти должен быть победитель");
        }
    }

    private void validateVenue(Integer venueId) throws SQLException {
        if (venueId == null) {
            return;
        }

        if (venueId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор площадки");
        }

        if (!venuesRepository.existsById(venueId)) {
            throw new IllegalArgumentException("Выбранная площадка не найдена");
        }
    }

    private void validateForm(MatchForm form) throws SQLException {
        validateTeams(form);
        validateStatusAndScore(form);

        Tournament tournament = tournamentsRepository.findById(form.getTournamentId());

        if (tournament == null) {
            throw new IllegalArgumentException("Турнир не найден");
        }

        if (form.getMatchDate() != null) {
            if (form.getMatchDate().isBefore(tournament.getStartDate()) || form.getMatchDate().isAfter(tournament.getEndDate())) {
                throw new IllegalArgumentException("Дата матча должна быть в пределах турнира: с " + tournament.getStartDate() + " по " + tournament.getEndDate());
            }
        }

        validateStage(form, tournament);
        validatePenaltiesAndTechnicalResult(form, tournament);
        validateVenue(form.getVenueId());
    }

    private void validateRosterChanges(int matchId, MatchForm existingMatch, MatchForm form) throws SQLException {

        boolean hasTeam1Players = !rostersRepository.findByMatchIdAndTeamId(matchId, existingMatch.getTeam1Id()).isEmpty();
        boolean hasTeam2Players = !rostersRepository.findByMatchIdAndTeamId(matchId, existingMatch.getTeam2Id()).isEmpty();

        if (!hasTeam1Players && !hasTeam2Players) {
            return;
        }

        if (form.getTeam1Id() != existingMatch.getTeam1Id() || form.getTeam2Id() != existingMatch.getTeam2Id()) {
            throw new IllegalArgumentException("Перед изменением команд очистите составы матча");
        }

        boolean dateChanged;

        if (existingMatch.getMatchDate() == null) {
            dateChanged = form.getMatchDate() != null;
        } else {
            dateChanged = !existingMatch.getMatchDate().equals(form.getMatchDate());
        }

        if (dateChanged) {
            throw new IllegalArgumentException("Перед изменением даты очистите составы матча");
        }
    }

    private void validateStage(MatchForm form, Tournament tournament) {
        String stage = form.getStage();

        if (stage == null || stage.isBlank()) {
            return;
        }

        if (!"Кубок".equals(tournament.getType())) {
            throw new IllegalArgumentException("Стадию можно указать только для матча кубка");
        }

        if (stage.length() > 50) {
            throw new IllegalArgumentException("Название стадии должно содержать не более 50 символов");
        }
    }
}



