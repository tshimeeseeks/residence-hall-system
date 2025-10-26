package com.rhs.backend.mapper;

import com.rhs.backend.dto.MaintenanceQueryDTO;
import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.enums.QueryStatus;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between MaintenanceQuery entities and DTOs
 */
@Component
@RequiredArgsConstructor
public class MaintenanceQueryMapper {

    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

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
                .photoUrls(dto.getPhotoUrls())
                .status(dto.getStatus() != null ? dto.getStatus() : QueryStatus.PENDING)
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
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + dto.getStudentId()));
            builder.student(student);
        }

        // Fetch and set admin reference if assigned
        if (dto.getAssignedToId() != null) {
            Admin admin = adminRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + dto.getAssignedToId()));
            builder.assignedTo(admin);
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

        // Update student reference if provided
        if (dto.getStudentId() != null) {
            Student student = studentRepository.findById(dto.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + dto.getStudentId()));
            existingEntity.setStudent(student);
        }

        // Update admin reference if provided
        if (dto.getAssignedToId() != null) {
            Admin admin = adminRepository.findById(dto.getAssignedToId())
                    .orElseThrow(() -> new RuntimeException("Admin not found with ID: " + dto.getAssignedToId()));
            existingEntity.setAssignedTo(admin);
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
                .photoUrls(entity.getPhotoUrls())
                .status(entity.getStatus())
                .priority(entity.getPriority())
                .resolutionNotes(entity.getResolutionNotes())
                .resolvedAt(entity.getResolvedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        // Extract student information
        if (entity.getStudent() != null) {
            builder.studentId(entity.getStudent().getId())
                    .studentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName())
                    .studentEmail(entity.getStudent().getEmail());
        }

        // Extract admin information
        if (entity.getAssignedTo() != null) {
            builder.assignedToId(entity.getAssignedTo().getId())
                    .assignedToName(entity.getAssignedTo().getFirstName() + " " + entity.getAssignedTo().getLastName());
        }

        return builder.build();
    }
}