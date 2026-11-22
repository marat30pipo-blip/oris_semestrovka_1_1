package ru.itis.repository;

import ru.itis.dto.GoalDto;
import ru.itis.dto.GoalForm;

import java.sql.SQLException;
import java.util.List;

public interface GoalsRepository {

    List<GoalDto> findByMatchId(int matchId) throws SQLException;
    void save(GoalForm form) throws SQLException;
    int countByMatchIdAndTeamId(int matchId, int teamId) throws SQLException;
    boolean delete(int goalId, int matchId) throws SQLException;
}