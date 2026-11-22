package ru.itis.repository;

import java.sql.SQLException;
import java.util.List;

public interface SeasonsRepository {
    List<Integer> findAllYears() throws SQLException;
    boolean save(int seasonYear) throws SQLException;
}
