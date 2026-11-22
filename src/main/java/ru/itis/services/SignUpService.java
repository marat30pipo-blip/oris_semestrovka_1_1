package ru.itis.services;

import ru.itis.dto.SignUpForm;

import java.sql.SQLException;

public interface SignUpService {
    boolean signUp(SignUpForm form) throws SQLException;
}
