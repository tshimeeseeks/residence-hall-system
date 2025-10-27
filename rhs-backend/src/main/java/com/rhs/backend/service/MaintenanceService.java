package com.rhs.backend.service;

import com.rhs.backend.dto.request.CreateMaintenanceQueryRequest;
import com.rhs.backend.dto.request.MaintenanceQueryFilter;
import com.rhs.backend.dto.request.UpdateMaintenanceStatusRequest;
import com.rhs.backend.dto.response.MaintenanceResponse;
import com.rhs.backend.dto.response.MaintenanceStatsResponse;
import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.Room;
import com.rhs.backend.repository.MaintenanceRepository;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final FileStorageService fileStorageService;

    /**
     * Create a new maintenance request
     */
    @Transactional
    public MaintenanceResponse createMaintenanceRequest(CreateMaintenanceQueryRequest request,
            List<MultipartFile> photos) {
        // Get current user
        String firebaseUid = getCurrentUserFirebaseUid();
        Student student = studentRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Get room information
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // Upload photos if any
        List<String> photoUrls = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String photoUrl = fileStorageService.storeFile(photo);
                photoUrls.add(photoUrl);
            }
        }

        // Create maintenance query
        MaintenanceQuery query = MaintenanceQuery.builder()
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .studentEmail(student.getEmail())
                .roomId(room.getId())
                .queryDescription(request.getQueryDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status("PENDING")
                .photoUrls(photoUrls)
                .createdAt(LocalDateTime.now())
                .build();

        query = maintenanceRepository.save(query);

        log.info("Maintenance request created: id={}, studentId={}, category={}",
                query.getId(), student.getId(), query.getCategory());

        return MaintenanceResponse.fromEntity(query);
    }

    /**
     * Get maintenance requests with filters
     */
    @Transactional(readOnly = true)
    public Page<MaintenanceResponse> getMaintenanceRequests(MaintenanceQueryFilter filter, Pageable pageable) {
        String firebaseUid = getCurrentUserFirebaseUid();
        Student student = studentRepository.findByFirebaseUid(firebaseUid)
                .orElse(null);

        Page<MaintenanceQuery> queryPage;

        // If student, only show their requests
        if (student != null) {
            queryPage = maintenanceRepository.findByStudentId(student.getId(), pageable);
        } else {
            // Admin can see all, with optional filters
            if (filter != null && filter.getStatus() != null) {
                queryPage = maintenanceRepository.findByStatus(filter.getStatus(), pageable);
            } else if (filter != null && filter.getRoomId() != null) {
                queryPage = maintenanceRepository.findByRoomId(filter.getRoomId(), pageable);
            } else {
                queryPage = maintenanceRepository.findAll(pageable);
            }
        }

        return queryPage.map(MaintenanceResponse::fromEntity);
    }

    /**
     * Get maintenance request by ID
     */
    @Transactional(readOnly = true)
    public MaintenanceResponse getMaintenanceById(String id) {
        String firebaseUid = getCurrentUserFirebaseUid();
        Student student = studentRepository.findByFirebaseUid(firebaseUid)
                .orElse(null);

        MaintenanceQuery query = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        // Students can only view their own requests
        if (student != null && !query.getStudentId().equals(student.getId())) {
            throw new RuntimeException("Access denied");
        }

        return MaintenanceResponse.fromEntity(query);
    }

    /**
     * Update maintenance request status (Admin only)
     */
    @Transactional
    public MaintenanceResponse updateStatus(String id, UpdateMaintenanceStatusRequest request) {
        MaintenanceQuery query = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        query.setStatus(request.getStatus());
        query.setUpdatedAt(LocalDateTime.now());

        if (request.getNotes() != null) {
            query.setResolutionNotes(request.getNotes());
        }

        if ("RESOLVED".equals(request.getStatus())) {
            query.setResolvedAt(LocalDateTime.now());
        }

        query = maintenanceRepository.save(query);

        log.info("Maintenance request updated: id={}, status={}", id, request.getStatus());

        return MaintenanceResponse.fromEntity(query);
    }

    /**
     * Delete maintenance request
     */
    @Transactional
    public void deleteMaintenance(String id) {
        String firebaseUid = getCurrentUserFirebaseUid();
        Student student = studentRepository.findByFirebaseUid(firebaseUid)
                .orElse(null);

        MaintenanceQuery query = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        // Students can only delete their own pending requests
        if (student != null) {
            if (!query.getStudentId().equals(student.getId())) {
                throw new RuntimeException("Access denied");
            }
            if (!"PENDING".equals(query.getStatus())) {
                throw new RuntimeException("Can only delete pending requests");
            }
        }

        // Delete photos from storage
        if (query.getPhotoUrls() != null) {
            for (String photoUrl : query.getPhotoUrls()) {
                fileStorageService.deleteFile(photoUrl);
            }
        }

        maintenanceRepository.delete(query);
        log.info("Maintenance request deleted: id={}", id);
    }

    /**
     * Add photos to existing maintenance request
     */
    @Transactional
    public MaintenanceResponse addPhotos(String id, List<MultipartFile> photos) {
        String firebaseUid = getCurrentUserFirebaseUid();
        Student student = studentRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        MaintenanceQuery query = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        // Students can only add photos to their own requests
        if (!query.getStudentId().equals(student.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Upload new photos
        List<String> currentUrls = query.getPhotoUrls();
        if (currentUrls == null) {
            currentUrls = new ArrayList<>();
        }

        for (MultipartFile photo : photos) {
            String photoUrl = fileStorageService.storeFile(photo);
            currentUrls.add(photoUrl);
        }

        query.setPhotoUrls(currentUrls);
        query = maintenanceRepository.save(query);

        log.info("Photos added to maintenance request: id={}, photoCount={}", id, photos.size());

        return MaintenanceResponse.fromEntity(query);
    }

    /**
     * Get maintenance statistics (Admin only)
     */
    @Transactional(readOnly = true)
    public MaintenanceStatsResponse getStatistics() {
        List<MaintenanceQuery> allQueries = maintenanceRepository.findAll();

        long total = allQueries.size();
        long pending = maintenanceRepository.countByStatus("PENDING");
        long inProgress = maintenanceRepository.countByStatus("IN_PROGRESS");
        long resolved = maintenanceRepository.countByStatus("RESOLVED");
        long cancelled = maintenanceRepository.countByStatus("CANCELLED");

        // Calculate average resolution time
        List<MaintenanceQuery> resolvedQueries = allQueries.stream()
                .filter(q -> "RESOLVED".equals(q.getStatus())
                        && q.getResolvedAt() != null
                        && q.getCreatedAt() != null)
                .collect(Collectors.toList());

        double averageResolutionHours = 0.0;
        if (!resolvedQueries.isEmpty()) {
            long totalHours = resolvedQueries.stream()
                    .mapToLong(q -> java.time.Duration.between(
                            q.getCreatedAt(), q.getResolvedAt()).toHours())
                    .sum();
            averageResolutionHours = (double) totalHours / resolvedQueries.size();
        }

        return MaintenanceStatsResponse.builder()
                .total(total)
                .pending(pending)
                .inProgress(inProgress)
                .resolved(resolved)
                .cancelled(cancelled)
                .averageResolutionTimeInHours(averageResolutionHours)
                .build();
    }

    /**
     * Get current user's Firebase UID from security context
     */
    private String getCurrentUserFirebaseUid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return authentication.getName();
    }
}