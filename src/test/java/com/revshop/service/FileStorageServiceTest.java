package com.revshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldReturnNullWhenFileIsEmpty() {
        FileStorageService fileStorageService = new FileStorageService(tempDir.toString());
        MockMultipartFile empty = new MockMultipartFile("imageFile", new byte[0]);

        String path = fileStorageService.storeProductImage(empty);

        assertNull(path);
    }

    @Test
    void shouldStoreFileAndReturnPublicPath() throws Exception {
        FileStorageService fileStorageService = new FileStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile(
                "imageFile",
                "phone.jpg",
                "image/jpeg",
                "abc".getBytes()
        );

        String publicPath = fileStorageService.storeProductImage(file);

        assertNotNull(publicPath);
        assertTrue(publicPath.startsWith("/uploads/"));

        String storedFile = publicPath.substring("/uploads/".length());
        assertTrue(Files.exists(tempDir.resolve(storedFile)));
    }
}
