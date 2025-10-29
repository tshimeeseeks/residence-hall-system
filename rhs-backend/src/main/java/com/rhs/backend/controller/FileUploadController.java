package com.rhs.backend.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.rhs.backend.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * Upload a file to MongoDB GridFS
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("Uploading file: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "File is empty"));
            }

            // Validate file size (10MB max)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "File size must be less than 10MB"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "File must be an image or PDF"));
            }

            String fileId = fileUploadService.uploadFile(file);

            log.info("File uploaded successfully to GridFS with ID: {}", fileId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "fileId", fileId,
                    "fileUrl", "/api/files/" + fileId,
                    "message", "File uploaded successfully to MongoDB GridFS"));

        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    /**
     * Download/view a file from MongoDB GridFS
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable String fileId) {
        try {
            log.info("Retrieving file from GridFS: {}", fileId);

            GridFSFile gridFSFile = fileUploadService.getFile(fileId);
            InputStream inputStream = fileUploadService.getFileInputStream(fileId);

            // Get content type from GridFS metadata
            String contentType = gridFSFile.getMetadata() != null &&
                    gridFSFile.getMetadata().get("_contentType") != null
                            ? gridFSFile.getMetadata().get("_contentType").toString()
                            : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(gridFSFile.getLength());

            // Optional: Set filename for download
            // headers.setContentDispositionFormData("attachment",
            // gridFSFile.getFilename());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            log.error("Error retrieving file from GridFS", e);
            return ResponseEntity.status(404).body(
                    Map.of("error", "File not found: " + e.getMessage()));
        }
    }

    /**
     * Delete a file from MongoDB GridFS
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) {
        try {
            log.info("Deleting file from GridFS: {}", fileId);

            fileUploadService.deleteFile(fileId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File deleted successfully"));

        } catch (Exception e) {
            log.error("Error deleting file from GridFS", e);
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to delete file: " + e.getMessage()));
        }
    }
}