package ru.itis.repository;

import ru.itis.models.User;

import java.sql.SQLException;

public interface UsersRepository {
    User findByEmail(String email) throws SQLException;
    boolean save(String lastName, String firstName, String middleName, String email,
    String passwordHash, Integer roleId) throws SQLException;
}
