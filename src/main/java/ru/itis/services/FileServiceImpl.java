package ru.itis.services;

import ru.itis.models.FileInfo;
import ru.itis.repository.FilesRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

public class FileServiceImpl implements FileService{

    private final FilesRepository filesRepository;
    private final Path storageDirectory;
    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    public FileServiceImpl(FilesRepository filesRepository, Path storageDirectory) {
        this.filesRepository = filesRepository;
        this.storageDirectory = storageDirectory;
    }

    @Override
    public Long saveFileToStorage(InputStream file, String originalFileName, String contentType, Long size) throws IOException, SQLException {

        if (file == null || size == null || size <= 0) {
            throw new IllegalArgumentException("Выберите непустой файл");
        }

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Отсутствует название файла");
        }

        if (originalFileName.length() > 255) {
            throw new IllegalArgumentException(
                    "Название файла должно содержать не больше 255 символов"
            );
        }

        if (size > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Размер эмблемы не должен превышать 5 МБ");
        }

        if (!"image/png".equals(contentType)
                && !"image/jpeg".equals(contentType)) {
            throw new IllegalArgumentException("Можно загрузить только PNG или JPEG");
        }

        String storageFileName = UUID.randomUUID().toString();

        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(originalFileName)
                .storageFileName(storageFileName)
                .type(contentType)
                .size(size)
                .build();

        Files.createDirectories(storageDirectory);

        Path filePath = Paths.get(storageDirectory.toString() + "/" + storageFileName);
        Files.copy(file, filePath);

        try {
            return filesRepository.save(fileInfo);
        } catch (SQLException e) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException cleanupException) {
                e.addSuppressed(cleanupException);
            }

            throw e;
        }

    }

    @Override
    public FileInfo getFileInfo(Long fileId) throws SQLException {
        return filesRepository.findById(fileId);
    }

    @Override
    public void writeFileFromStorage(FileInfo fileInfo, OutputStream outputStream) throws IOException {
        Path filePath = Paths.get(storageDirectory.toString() + "/" + fileInfo.getStorageFileName());
        Files.copy(filePath, outputStream);
    }

    @Override
    public void deleteFile(Long fileId) throws IOException, SQLException {
        FileInfo fileInfo = filesRepository.findById(fileId);

        if (fileInfo == null) {
            return;
        }

        Path filePath = Paths.get(storageDirectory.toString() + "/" + fileInfo.getStorageFileName());

        filesRepository.deleteById(fileId);
        Files.deleteIfExists(filePath);
    }
}
