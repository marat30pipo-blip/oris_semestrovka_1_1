package ru.itis.services;

import ru.itis.models.FileInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

public interface FileService {
    Long saveFileToStorage(InputStream file, String originalFileName, String contentType, Long size) throws IOException, SQLException;
    FileInfo getFileInfo(Long fileId) throws SQLException;
    void writeFileFromStorage(FileInfo fileInfo, OutputStream outputStream) throws IOException;
    void deleteFile(Long fileId) throws IOException, SQLException;
}
