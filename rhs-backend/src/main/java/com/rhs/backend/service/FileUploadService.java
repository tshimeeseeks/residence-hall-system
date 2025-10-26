package com.rhs.backend.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileUploadService {

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type: must be image");
        }

        if (file.getSize() > 5_000_000) {
            throw new IllegalArgumentException("File too large (max 5MB)");
        }

        Files.createDirectories(Paths.get(uploadDir));
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);

        Thumbnails.of(file.getInputStream())
                .scale(1.0)
                .outputQuality(0.8)
                .toFile(filePath.toFile());

        log.info("File uploaded: {}", filePath);
        return filePath.toString();
    }
}
