package com.revshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = resolveUploadRoot(uploadDir);
    }

    public String storeProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Files.createDirectories(uploadRoot);
            String originalName = file.getOriginalFilename() == null ? "product" : file.getOriginalFilename();
            String extension = extractExtension(originalName);
            String safeName = UUID.randomUUID() + extension;
            Path target = uploadRoot.resolve(safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + safeName;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to upload product image");
        }
    }

    private String extractExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex);
    }

    private Path resolveUploadRoot(String uploadDir) {
        Path configured = Paths.get(uploadDir);
        if (configured.isAbsolute()) {
            return configured.normalize();
        }

        Path directPath = configured.toAbsolutePath().normalize();
        Path nestedPath = Paths.get("revshop", uploadDir).toAbsolutePath().normalize();

        if (Files.exists(directPath)) {
            return directPath;
        }
        if (Files.exists(nestedPath)) {
            return nestedPath;
        }

        Path nestedPom = Paths.get("revshop", "pom.xml").toAbsolutePath().normalize();
        if (Files.exists(nestedPom)) {
            return nestedPath;
        }
        return directPath;
    }
}
