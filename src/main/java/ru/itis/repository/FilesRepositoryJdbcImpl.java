package ru.itis.repository;

import ru.itis.models.FileInfo;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilesRepositoryJdbcImpl implements FilesRepository{

    private final DataSource dataSource;

    private static final String SQL_INSERT_FILE =
            "INSERT INTO fileinfo " +
                    "(original_file_name, storage_file_name, type, size) " +
                    "VALUES (?, ?, ?, ?) RETURNING id";

    private static final String SQL_SELECT_FILE_BY_ID =
            "SELECT id, original_file_name, storage_file_name, type, size " +
                    "FROM fileinfo WHERE id = ?";

    private static final String SQL_DELETE_FILE_BY_ID =
            "DELETE FROM fileinfo WHERE id = ?";


    public FilesRepositoryJdbcImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long save(FileInfo fileInfo) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_FILE)){

            preparedStatement.setString(1, fileInfo.getOriginalFileName());
            preparedStatement.setString(2, fileInfo.getStorageFileName());
            preparedStatement.setString(3, fileInfo.getType());
            preparedStatement.setLong(4, fileInfo.getSize());

        try (ResultSet resultSet = preparedStatement.executeQuery()){
            if (resultSet.next()) {
                return resultSet.getLong("id");
            }
            throw new SQLException("Не удалось получить идентификатор сохранённого файла");
        }
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(SQL_DELETE_FILE_BY_ID)) {

            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public FileInfo findById(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SQL_SELECT_FILE_BY_ID)){
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return FileInfo.builder()
                            .fileId(resultSet.getLong("id"))
                            .originalFileName(resultSet.getString("original_file_name"))
                            .storageFileName(resultSet.getString("storage_file_name"))
                            .type(resultSet.getString("type"))
                            .size(resultSet.getLong("size"))
                            .build();
                }

                return null;
            }
        }
    }
}
