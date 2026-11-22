package ru.itis.services;

import java.sql.SQLException;
import java.util.List;

public interface SeasonsService {
    List<Integer> findAllYears() throws SQLException;
    boolean save(int seasonYear) throws SQLException;
}
