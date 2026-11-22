package ru.itis.services;

import ru.itis.models.Team;
import ru.itis.repository.TeamsRepository;

import java.sql.SQLException;
import java.util.List;

public class TeamsServiceImpl implements TeamsService{

    private final TeamsRepository teamsRepository;

    public TeamsServiceImpl(TeamsRepository teamsRepository) {
        this.teamsRepository = teamsRepository;
    }

    @Override
    public List<Team> findAll() throws SQLException {
        return teamsRepository.findAll();
    }

    @Override
    public void validate(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Введите название команды");
        }

        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("Название команды должно содержать не более 100 символов");
        }

        if (description != null && description.trim().length() > 1000) {
            throw new IllegalArgumentException(
                    "Описание должно содержать не более 1000 символов"
            );
        }
    }

    @Override
    public Team findById(int teamId) throws SQLException {
        return teamsRepository.findById(teamId);
    }

    @Override
    public boolean update(int teamId, String name, String description) throws SQLException {
        validate(name, description);

        name = name.trim();

        if (description != null) {
            description = description.trim();
        }

        return teamsRepository.update(teamId, name, description);
    }

    @Override
    public boolean updateEmblem(int teamId, Long emblemId) throws SQLException {

        if (teamId <= 0) {
            throw new IllegalArgumentException(
                    "Некорректный идентификатор команды"
            );
        }

        if (emblemId != null && emblemId <= 0) {
            throw new IllegalArgumentException(
                    "Некорректный идентификатор эмблемы"
            );
        }

        return teamsRepository.updateEmblem(teamId, emblemId);
    }

    @Override
    public boolean addToTournament(int tournamentId, int teamId)
            throws SQLException {

        if (tournamentId <= 0 || teamId <= 0) {
            throw new IllegalArgumentException(
                    "Некорректный идентификатор турнира или команды"
            );
        }

        if (teamsRepository.findById(teamId) == null) {
            throw new IllegalArgumentException("Команда не найдена");
        }

        return teamsRepository.addToTournament(tournamentId, teamId);
    }

    @Override
    public void save(String name, String description, Long emblemId) throws SQLException {

        validate(name, description);

        name = name.trim();

        if (description != null) {
            description = description.trim();
        }
        teamsRepository.save(name, description, emblemId);
    }

    @Override
    public List<Team> findByTournamentId(int tournamentId) throws SQLException {
        return teamsRepository.findByTournamentId(tournamentId);
    }
}
