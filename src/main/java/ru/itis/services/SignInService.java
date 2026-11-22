package ru.itis.services;

import ru.itis.models.User;
import ru.itis.repository.UsersRepository;

import java.sql.SQLException;

public interface SignInService {
    public User signIn(String email, String password) throws SQLException;
}