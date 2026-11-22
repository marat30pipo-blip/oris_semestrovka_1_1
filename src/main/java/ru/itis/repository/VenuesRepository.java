package ru.itis.repository;

import ru.itis.models.Venue;

import java.sql.SQLException;
import java.util.List;

public interface VenuesRepository {
    boolean existsById(int venueId) throws SQLException;
    List<Venue> findAll() throws SQLException;
}