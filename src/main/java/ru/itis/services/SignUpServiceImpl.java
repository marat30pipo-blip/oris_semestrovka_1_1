package ru.itis.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itis.dto.SignUpForm;
import ru.itis.models.User;
import ru.itis.repository.UsersRepository;

import java.sql.SQLException;

public class SignUpServiceImpl implements SignUpService{

    private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UsersRepository usersRepository;
    private Integer userRoleId;

    public SignUpServiceImpl(UsersRepository usersRepository, Integer userRoleId) {
        this.usersRepository = usersRepository;
        this.userRoleId = userRoleId;
    }


    @Override
    public boolean signUp(SignUpForm form) throws SQLException {
        if (form == null) {
            throw new IllegalArgumentException("Данные регистрации не переданы");
        }

        String lastName = validateText(form.getLastName(), "Фамилия", 200);

        String firstName = validateText(form.getFirstName(), "Имя", 150);

        String middleName = validateText(form.getMiddleName(), "Отчество", 150);

        String email = validateText(form.getEmail(), "Email", 200);

        String password = form.getPassword();

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Введите пароль");
        }

        if (password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Пароль слишком длинный");
        }

        String passwordHash = passwordEncoder.encode(password);

        return usersRepository.save(
                lastName,
                firstName,
                middleName,
                email,
                passwordHash,
                userRoleId
        );
    }

    private String validateText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Заполните поле «" + fieldName + "»");
        }

        value = value.trim();

        if (value.length() > maxLength) {
            throw new IllegalArgumentException("Поле «" + fieldName + "» должно содержать не более " + maxLength + " символов");
        }

        return value;
    }
}
