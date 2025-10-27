package com.rhs.backend.mapper;

import com.rhs.backend.dto.request.MaintenanceQueryDTO;
import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.User;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between MaintenanceQuery entities and DTOs
 */
@Component
public class MaintenanceQueryMapper {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public MaintenanceQueryMapper(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Convert DTO to Entity (for creating/updating)
     *
     * @param dto the MaintenanceQueryDTO
     * @return MaintenanceQuery entity
     */
    public MaintenanceQuery toEntity(MaintenanceQueryDTO dto) {
        if (dto == null) {
            return null;
        }

        MaintenanceQuery.MaintenanceQueryBuilder builder = MaintenanceQuery.builder()
                .roomId(dto.getRoomId())
                .queryTitle(dto.getQueryTitle())
                .queryDescription(dto.getQueryDescription())
                .category(dto.getCategory())
                .photoUrls(dto.getPhotoUrls())
                .status(dto.getStatus() != null ? dto.getStatus() : "PENDING")
                .priority(dto.getPriority())
                .resolutionNotes(dto.getResolutionNotes())
                .resolvedAt(dto.getResolvedAt());

        // Set ID if updating existing entity
        if (dto.getId() != null) {
            builder.id(dto.getId());
        }

        // Fetch and set student reference
        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + dto.getStudentId()));
            builder.studentId(student.getId());
            builder.studentName(student.getFirstName() + " " + student.getLastName());
            builder.studentEmail(student.getEmail());
        }

        // Fetch and set admin reference if assigned
        if (dto.getAssignedToId() != null) {
            User admin = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Admin not found with id: " + dto.getAssignedToId()));
            builder.assignedToId(admin.getId());
            builder.assignedToName(admin.getFirstName() + " " + admin.getLastName());
        }

        return builder.build();
    }

    /**
     * Convert DTO to Entity without fetching references (for partial updates)
     *
     * @param dto            the MaintenanceQueryDTO
     * @param existingEntity the existing MaintenanceQuery entity
     * @return updated MaintenanceQuery entity
     */
    public MaintenanceQuery updateEntity(MaintenanceQueryDTO dto, MaintenanceQuery existingEntity) {
        if (dto == null || existingEntity == null) {
            return existingEntity;
        }

        // Update only provided fields
        if (dto.getRoomId() != null) {
            existingEntity.setRoomId(dto.getRoomId());
        }

        if (dto.getQueryTitle() != null) {
            existingEntity.setQueryTitle(dto.getQueryTitle());
        }

        if (dto.getQueryDescription() != null) {
            existingEntity.setQueryDescription(dto.getQueryDescription());
        }

        if (dto.getCategory() != null) {
            existingEntity.setCategory(dto.getCategory());
        }

        if (dto.getPhotoUrls() != null) {
            existingEntity.setPhotoUrls(dto.getPhotoUrls());
        }

        if (dto.getStatus() != null) {
            existingEntity.setStatus(dto.getStatus());
        }

        if (dto.getPriority() != null) {
            existingEntity.setPriority(dto.getPriority());
        }

        if (dto.getResolutionNotes() != null) {
            existingEntity.setResolutionNotes(dto.getResolutionNotes());
        }

        if (dto.getResolvedAt() != null) {
            existingEntity.setResolvedAt(dto.getResolvedAt());
        }

        existingEntity.setUpdatedAt(LocalDateTime.now());

        // Update student reference if provided
        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + dto.getStudentId()));
            existingEntity.setStudentId(student.getId());
            existingEntity.setStudentName(student.getFirstName() + " " + student.getLastName());
            existingEntity.setStudentEmail(student.getEmail());
        }

        // Update admin reference if provided
        if (dto.getAssignedToId() != null) {
            User admin = userRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Admin not found with id: " + dto.getAssignedToId()));
            existingEntity.setAssignedToId(admin.getId());
            existingEntity.setAssignedToName(admin.getFirstName() + " " + admin.getLastName());
        }

        return existingEntity;
    }

    /**
     * Convert Entity to DTO (for reading/returning)
     *
     * @param entity the MaintenanceQuery entity
     * @return MaintenanceQueryDTO
     */
    public MaintenanceQueryDTO toDTO(MaintenanceQuery entity) {
        if (entity == null) {
            return null;
        }

        MaintenanceQueryDTO.MaintenanceQueryDTOBuilder builder = MaintenanceQueryDTO.builder()
                .id(entity.getId())
                .roomId(entity.getRoomId())
                .queryTitle(entity.getQueryTitle())
                .queryDescription(entity.getQueryDescription())
                .category(entity.getCategory())
                .photoUrls(entity.getPhotoUrls())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .resolutionNotes(entity.getResolutionNotes())
                .resolvedAt(entity.getResolvedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Extract student information
        if (entity.getStudentId() != null) {
            builder.studentId(entity.getStudentId())
                    .studentName(entity.getStudentName())
                    .studentEmail(entity.getStudentEmail());
        }

        // Extract admin information
        if (entity.getAssignedToId() != null) {
            builder.assignedToId(entity.getAssignedToId())
                    .assignedToName(entity.getAssignedToName());
        }

        return builder.build();
    }
}