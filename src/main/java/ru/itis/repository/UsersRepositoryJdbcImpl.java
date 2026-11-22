package ru.itis.repository;

import ru.itis.models.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersRepositoryJdbcImpl implements UsersRepository{

    private final DataSource dataSource;

    public UsersRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String SQL_SELECT_FROM_USERS_WHERE_EMAIL = "SELECT u.user_id, u.email, u.password_hash, r.name AS role\n" +
            "FROM users AS u\n" +
            "JOIN roles AS r ON r.role_id = u.role_id\n" +
            "WHERE u.email = ?";

    private static final String SQL_INSERT_INTO_USERS = "INSERT INTO users\n" +
            "    (last_name, first_name, middle_name, email, password_hash, registered_on, role_id)\n" +
            "VALUES (?, ?, ?, ?, ?, CURRENT_DATE, ?) ON CONFLICT (email) DO NOTHING";

    @Override
    public User findByEmail(String email) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_SELECT_FROM_USERS_WHERE_EMAIL)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return User.builder()
                            .userId(resultSet.getLong("user_id"))
                            .email(resultSet.getString("email"))
                            .passwordHash(resultSet.getString("password_hash"))
                            .role(resultSet.getString("role"))
                            .build();
                }

                return null;
            }
        }
    }

    @Override
    public boolean save(String lastName, String firstName, String middleName, String email, String passwordHash, Integer roleId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_INTO_USERS)){
            preparedStatement.setString(1, lastName);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, middleName);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, passwordHash);
            preparedStatement.setInt(6, roleId);
            return preparedStatement.executeUpdate() == 1;
        }
    }
}
