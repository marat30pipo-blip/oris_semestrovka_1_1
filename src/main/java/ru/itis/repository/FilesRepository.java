package ru.itis.repository;

import ru.itis.models.FileInfo;

import java.sql.SQLException;

public interface FilesRepository {
    Long save(FileInfo fileInfo) throws SQLException;
    void deleteById(Long id) throws SQLException;
    FileInfo findById(Long id) throws SQLException;
}
