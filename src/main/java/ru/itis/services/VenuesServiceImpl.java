package ru.itis.services;

import ru.itis.models.Venue;
import ru.itis.repository.VenuesRepository;

import java.sql.SQLException;
import java.util.List;

public class VenuesServiceImpl implements VenuesService {

    private final VenuesRepository venuesRepository;

    public VenuesServiceImpl(VenuesRepository venuesRepository) {
        this.venuesRepository = venuesRepository;
    }

    @Override
    public List<Venue> findAll() throws SQLException {
        return venuesRepository.findAll();
    }
}