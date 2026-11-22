package ru.itis.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileInfo {
    private Long fileId;
    private String originalFileName;
    private String storageFileName;
    private Long size;
    private String type;
}
