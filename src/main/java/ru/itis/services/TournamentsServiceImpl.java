package ru.itis.services;

import ru.itis.dto.TournamentForm;
import ru.itis.models.Tournament;
import ru.itis.repository.TournamentsRepository;

import java.sql.SQLException;
import java.util.List;

public class TournamentsServiceImpl implements TournamentsService {

    private final TournamentsRepository tournamentsRepository;

    public TournamentsServiceImpl(TournamentsRepository tournamentsRepository) {
        this.tournamentsRepository = tournamentsRepository;
    }

    @Override
    public List<Tournament> findBySeasonYear(int season)
            throws SQLException {
        return tournamentsRepository.findBySeasonYear(season);
    }

    @Override
    public Tournament findById(int tournamentId) throws SQLException {
        return tournamentsRepository.findById(tournamentId);
    }

    @Override
    public boolean save(TournamentForm form) throws SQLException {
        TournamentForm preparedForm = validateAndPrepareForm(form);

        if (preparedForm.getSeasonYear() < 1000 || preparedForm.getSeasonYear() > 9999) {
            throw new IllegalArgumentException("Выберите корректный сезон");
        }

        return tournamentsRepository.save(preparedForm);
    }

    @Override
    public boolean update(int tournamentId, TournamentForm form)
            throws SQLException {

        if (tournamentId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор турнира");
        }

        TournamentForm preparedForm = validateAndPrepareForm(form);

        return tournamentsRepository.update(tournamentId, preparedForm);
    }

    @Override
    public List<Tournament> findByTeamIdAndSeasonYear(
            int teamId, int seasonYear) throws SQLException {

        if (teamId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор команды");
        }

        if (seasonYear < 1000 || seasonYear > 9999) {
            throw new IllegalArgumentException("Некорректный год сезона");
        }

        return tournamentsRepository.findByTeamIdAndSeasonYear(teamId, seasonYear);
    }

    private TournamentForm validateAndPrepareForm(TournamentForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Данные турнира не переданы");
        }



        String name = form.getName();

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Введите название турнира");
        }

        name = name.trim();

        if (name.length() > 150) {
            throw new IllegalArgumentException("Название должно содержать не более 150 символов");
        }

        String type = form.getType();

        if (!"Чемпионат".equals(type) && !"Кубок".equals(type)) {
            throw new IllegalArgumentException("Выберите тип: Чемпионат или Кубок");
        }

        if (form.getStartDate() == null || form.getEndDate() == null) {
            throw new IllegalArgumentException("Укажите даты начала и окончания турнира");
        }

        if (form.getEndDate().isBefore(form.getStartDate())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

        String description = form.getDescription();

        if (description != null) {
            description = description.trim();

            if (description.length() > 1000) {
                throw new IllegalArgumentException("Описание должно содержать не более 1000 символов");
            }
        }

        return TournamentForm.builder()
                .seasonYear(form.getSeasonYear())
                .name(name)
                .type(type)
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .description(description)
                .build();
    }
}