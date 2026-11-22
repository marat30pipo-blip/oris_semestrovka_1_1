package ru.itis.services;

import ru.itis.repository.SeasonsRepository;

import java.sql.SQLException;
import java.util.List;

public class SeasonsServiceImpl implements SeasonsService {

    private final SeasonsRepository seasonsRepository;

    public SeasonsServiceImpl(SeasonsRepository seasonsRepository) {
        this.seasonsRepository = seasonsRepository;
    }

    @Override
    public List<Integer> findAllYears() throws SQLException {
        return seasonsRepository.findAllYears();
    }

    @Override
    public boolean save(int seasonYear) throws SQLException {
        if (seasonYear < 1000 || seasonYear > 9999) {
            throw new IllegalArgumentException(
                    "Год сезона должен быть четырёхзначным"
            );
        }

        return seasonsRepository.save(seasonYear);
    }
}
