package ru.itis.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeasonsRepositoryJdbcImpl implements SeasonsRepository{

    private final DataSource dataSource;

    private static final String SQL_SELECT_FROM_SEASONS = "SELECT year\n" +
            "FROM seasons\n" +
            "ORDER BY year DESC";

    private static final String SQL_INSERT_SEASON =
            "INSERT INTO seasons (year) VALUES (?) " +
                    "ON CONFLICT (year) DO NOTHING";

    public SeasonsRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Integer> findAllYears() throws SQLException {
        List<Integer> years = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FROM_SEASONS)){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                years.add(resultSet.getInt("year"));
            }
            return years;
        }
    }

    @Override
    public boolean save(int seasonYear) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_INSERT_SEASON)) {

            preparedStatement.setInt(1, seasonYear);

            return preparedStatement.executeUpdate() == 1;
        }
    }


}
