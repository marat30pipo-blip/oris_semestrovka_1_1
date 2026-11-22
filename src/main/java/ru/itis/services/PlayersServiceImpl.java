package ru.itis.services;

import ru.itis.dto.PlayerForm;
import ru.itis.models.Player;
import ru.itis.repository.PlayersRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PlayersServiceImpl implements PlayersService {

    private final PlayersRepository playersRepository;

    public PlayersServiceImpl(PlayersRepository playersRepository) {
        this.playersRepository = playersRepository;
    }

    @Override
    public List<Player> findAll() throws SQLException {
        return playersRepository.findAll();
    }

    @Override
    public void validate(PlayerForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Данные игрока не переданы");
        }

        if (form.getLastName() == null || form.getLastName().isBlank()) {
            throw new IllegalArgumentException("Введите фамилию игрока");
        }

        if (form.getLastName().trim().length() > 200) {
            throw new IllegalArgumentException("Фамилия должна содержать не более 200 символов");
        }

        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Введите имя игрока");
        }

        if (form.getName().trim().length() > 150) {
            throw new IllegalArgumentException("Имя должно содержать не более 150 символов");
        }

        if (form.getMiddleName() != null && form.getMiddleName().trim().length() > 200) {
            throw new IllegalArgumentException("Отчество должно содержать не более 200 символов");
        }

        if (form.getBirthDate() != null && form.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Дата рождения не может быть в будущем");
        }

        if (form.getHeightCm() != null && form.getHeightCm() <= 0) {
            throw new IllegalArgumentException("Рост должен быть положительным числом");
        }

        if (form.getWeightKg() != null && form.getWeightKg().signum() <= 0) {
            throw new IllegalArgumentException("Вес должен быть положительным числом");
        }

        if (form.getPosition() != null && form.getPosition().trim().length() > 50) {
            throw new IllegalArgumentException("Позиция должна содержать не более 50 символов");
        }

        if (form.getShirtNumber() != null && (form.getShirtNumber() < 1
                || form.getShirtNumber() > 99)) {
            throw new IllegalArgumentException("Номер игрока должен быть от 1 до 99");
        }
    }

    @Override
    public void save(PlayerForm form, Long photoId) throws SQLException {
        PlayerForm preparedForm = validateAndPrepareForm(form);

        if (photoId != null && photoId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор фотографии");
        }

        playersRepository.save(preparedForm, photoId);
    }

    @Override
    public Player findById(int playerId) throws SQLException {
        if (playerId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор игрока");
        }

        return playersRepository.findById(playerId);
    }

    @Override
    public boolean update(int playerId, PlayerForm form) throws SQLException {
        if (playerId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор игрока");
        }

        PlayerForm preparedForm = validateAndPrepareForm(form);

        return playersRepository.update(playerId, preparedForm);
    }

    @Override
    public boolean updatePhoto(int playerId, Long photoId)
            throws SQLException {

        if (playerId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор игрока");
        }

        if (photoId != null && photoId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор фотографии");
        }

        return playersRepository.updatePhoto(playerId, photoId);
    }

    private PlayerForm validateAndPrepareForm(PlayerForm form) {
        validate(form);

        String middleName = form.getMiddleName();
        String position = form.getPosition();

        if (middleName != null) {
            middleName = middleName.trim();
        }

        if (position != null) {
            position = position.trim();
        }

        return PlayerForm.builder()
                .lastName(form.getLastName().trim())
                .name(form.getName().trim())
                .middleName(middleName)
                .birthDate(form.getBirthDate())
                .heightCm(form.getHeightCm())
                .weightKg(form.getWeightKg())
                .position(position)
                .shirtNumber(form.getShirtNumber())
                .build();
    }

    @Override
    public List<Player> findByTeamIdAndSeasonYear(int teamId, int seasonYear) throws SQLException {

        if (teamId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор команды");
        }

        if (seasonYear < 1000 || seasonYear > 9999) {
            throw new IllegalArgumentException("Некорректный год сезона");
        }

        return playersRepository.findByTeamIdAndSeasonYear(teamId, seasonYear);
    }
}