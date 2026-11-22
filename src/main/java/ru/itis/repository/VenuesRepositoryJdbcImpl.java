package ru.itis.repository;

import ru.itis.models.Venue;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VenuesRepositoryJdbcImpl implements VenuesRepository {

    private final DataSource dataSource;

    private static final String SQL_SELECT_ALL_VENUES =
            "SELECT venue_id, name FROM venues ORDER BY name, venue_id";

    private static final String SQL_SELECT_VENUE_BY_ID =
            "SELECT venue_id FROM venues WHERE venue_id = ?";

    public VenuesRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean existsById(int venueId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_VENUE_BY_ID)) {

            preparedStatement.setInt(1, venueId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    @Override
    public List<Venue> findAll() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_ALL_VENUES);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<Venue> venues = new ArrayList<>();

            while (resultSet.next()) {
                Venue venue = Venue.builder()
                        .venueId(resultSet.getInt("venue_id"))
                        .name(resultSet.getString("name"))
                        .build();

                venues.add(venue);
            }

            return venues;
        }
    }
}