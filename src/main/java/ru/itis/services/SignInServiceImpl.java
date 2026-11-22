package ru.itis.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itis.models.User;
import ru.itis.repository.UsersRepository;

import java.sql.SQLException;

public class SignInServiceImpl implements SignInService{
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SignInServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public User signIn(String email, String password) throws SQLException{

        User user = usersRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;

    }
}
