package ru.itis.services;

import ru.itis.dto.PlayerTeamPeriodDto;
import ru.itis.dto.PlayerTeamPeriodForm;
import ru.itis.repository.PlayerTeamPeriodsRepository;
import ru.itis.repository.PlayersRepository;
import ru.itis.repository.TeamsRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PlayerTeamPeriodsServiceImpl implements PlayerTeamPeriodsService {

    private final PlayerTeamPeriodsRepository periodsRepository;
    private final PlayersRepository playersRepository;
    private final TeamsRepository teamsRepository;

    public PlayerTeamPeriodsServiceImpl(
            PlayerTeamPeriodsRepository periodsRepository,
            PlayersRepository playersRepository,
            TeamsRepository teamsRepository) {

        this.periodsRepository = periodsRepository;
        this.playersRepository = playersRepository;
        this.teamsRepository = teamsRepository;
    }

    @Override
    public List<PlayerTeamPeriodDto> findByPlayerId(int playerId)
            throws SQLException {

        if (playerId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор игрока");
        }

        return periodsRepository.findByPlayerId(playerId);
    }

    @Override
    public boolean save(PlayerTeamPeriodForm form) throws SQLException {
        if (form == null) {
            throw new IllegalArgumentException("Данные периода не переданы");
        }

        if (form.getPlayerId() <= 0 || form.getTeamId() <= 0) {
            throw new IllegalArgumentException("Выберите игрока и команду");
        }

        if (form.getSeasonYear() < 1000
                || form.getSeasonYear() > 9999) {
            throw new IllegalArgumentException("Выберите корректный сезон");
        }

        if (form.getStartDate() == null) {
            throw new IllegalArgumentException("Укажите дату начала выступления");
        }

        if (form.getEndDate() != null
                && form.getEndDate().isBefore(form.getStartDate())) {
            throw new IllegalArgumentException("Окончание периода не может быть раньше начала");
        }

        if (playersRepository.findById(form.getPlayerId()) == null) {
            throw new IllegalArgumentException("Игрок не найден");
        }

        if (teamsRepository.findById(form.getTeamId()) == null) {
            throw new IllegalArgumentException("Команда не найдена");
        }

        List<PlayerTeamPeriodDto> periods = periodsRepository.findByPlayerId(form.getPlayerId());

        for (PlayerTeamPeriodDto period : periods) {


            boolean startsBeforeOldPeriodEnds = period.getEndDate() == null || !form.getStartDate().isAfter(period.getEndDate());

            boolean endsAfterOldPeriodStarts = form.getEndDate() == null || !form.getEndDate().isBefore(period.getStartDate());

            if (startsBeforeOldPeriodEnds && endsAfterOldPeriodStarts) {
                throw new IllegalArgumentException("У игрока уже есть пересекающийся период выступлений");
            }
        }

        return periodsRepository.save(form);
    }

    @Override
    public PlayerTeamPeriodDto findById(int appearanceId)
            throws SQLException {

        if (appearanceId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор периода");
        }

        return periodsRepository.findById(appearanceId);
    }

    @Override
    public boolean closePeriod(int appearanceId, LocalDate endDate)
            throws SQLException {

        if (appearanceId <= 0) {
            throw new IllegalArgumentException("Некорректный идентификатор периода");
        }

        if (endDate == null) {
            throw new IllegalArgumentException("Укажите дату окончания периода");
        }

        PlayerTeamPeriodDto period = periodsRepository.findById(appearanceId);

        if (period == null) {
            return false;
        }

        if (period.getEndDate() != null) {
            throw new IllegalArgumentException("Этот период уже закрыт");
        }

        if (endDate.isBefore(period.getStartDate())) {
            throw new IllegalArgumentException("Окончание периода не может быть раньше начала");
        }


        if (periodsRepository.hasRosterEntriesAfterDate(appearanceId, endDate)) {
            throw new IllegalArgumentException("Игрок включён в состав матча после выбранной даты. Исправьте состав или укажите более позднее окончание периода");
        }

        return periodsRepository.closePeriod(appearanceId, endDate);
    }
}