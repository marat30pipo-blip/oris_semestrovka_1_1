package ru.itis.services;

import ru.itis.models.Venue;

import java.sql.SQLException;
import java.util.List;

public interface VenuesService {

    List<Venue> findAll() throws SQLException;
}