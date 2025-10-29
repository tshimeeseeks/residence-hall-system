package com.rhs.backend.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileUploadService {

    private final GridFsTemplate gridFsTemplate;

    /**
     * Upload a file to MongoDB GridFS
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Unknown file type");
        }

        // Validate file size (10MB max)
        if (file.getSize() > 10_000_000) {
            throw new IllegalArgumentException("File too large (max 10MB)");
        }

        // Validate file type (images and PDFs only)
        if (!contentType.startsWith("image/") && !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Unsupported file type. Only images and PDFs are allowed.");
        }

        // Generate unique filename
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;

        // Store file in GridFS
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                fileName,
                contentType);

        log.info("File uploaded to GridFS: {} with ID: {}", fileName, fileId);

        // Return the GridFS file ID (we'll use this to retrieve the file later)
        return fileId.toString();
    }

    /**
     * Get file from GridFS by ID
     */
    public GridFSFile getFile(String fileId) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(fileId)));
            GridFSFile gridFSFile = gridFsTemplate.findOne(query);

            if (gridFSFile == null) {
                throw new RuntimeException("File not found: " + fileId);
            }

            return gridFSFile;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid file ID: " + fileId);
        }
    }

    /**
     * Get file input stream from GridFS
     */
    public InputStream getFileInputStream(String fileId) throws IOException {
        GridFSFile gridFSFile = getFile(fileId);
        return gridFsTemplate.getResource(gridFSFile).getInputStream();
    }

    /**
     * Delete file from GridFS
     */
    public void deleteFile(String fileId) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(fileId)));
            gridFsTemplate.delete(query);
            log.info("File deleted from GridFS: {}", fileId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid file ID: " + fileId);
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public String uploadImage(MultipartFile file) throws IOException {
        return uploadFile(file);
    }
}