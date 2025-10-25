package com.rhs.backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.rhs.backend.dto.request.StudentSignUpRequest;
import com.rhs.backend.dto.request.AdminCreateRequest;
import com.rhs.backend.dto.request.ApprovalRequest;
import com.rhs.backend.dto.response.AuthResponse;
import com.rhs.backend.exception.DuplicateResourceException;
import com.rhs.backend.exception.ResourceNotFoundException;
import com.rhs.backend.model.Admin;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.embedded.AdminPermissions;
import com.rhs.backend.model.embedded.RoomDetails;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.enums.UserType;
import com.rhs.backend.repository.AdminRepository;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public AuthResponse studentSignup(StudentSignUpRequest request) throws FirebaseAuthException {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Check for duplicate student number
        if (studentRepository.existsByStudentNumber(request.getStudentNumber())) {
            throw new DuplicateResourceException("Student number already exists");
        }

        // Create user in Firebase
        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getFirstName() + " " + request.getLastName())
                .setDisabled(true); // Disabled until approved

        UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(firebaseRequest);

        // Create room details
        RoomDetails roomDetails = RoomDetails.builder()
                .roomId(request.getRoomId())
                .building(request.getBuilding())
                .floor(request.getFloor())
                .build();

        // Create student in MongoDB
        Student student = Student.builder()
                .firebaseUid(firebaseUser.getUid()) // Add this field to your User model
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(UserType.STUDENT)
                .accountStatus(AccountStatus.PENDING_APPROVAL)
                .isEnabled(false)
                .studentNumber(request.getStudentNumber())
                .roomDetails(roomDetails)
                .course(request.getCourse())
                .yearOfStudy(request.getYearOfStudy())
                .build();

        studentRepository.save(student);

        return AuthResponse.builder()
                .message("Student registration successful. Please wait for admin approval.")
                .userId(student.getId())
                .email(student.getEmail()) // This should work now
                .userType(student.getUserType().name())
                .accountStatus(student.getAccountStatus().name())
                .build();
    }

    @Transactional
    public AuthResponse createAdmin(AdminCreateRequest request, String creatorAdminId) throws FirebaseAuthException {
        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create user in Firebase
        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getFirstName() + " " + request.getLastName());

        UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(firebaseRequest);

        // Set custom claims for role
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        FirebaseAuth.getInstance().setCustomUserClaims(firebaseUser.getUid(), claims);

        // Create admin permissions
        AdminPermissions permissions = AdminPermissions.builder()
                .role(request.getRole())
                .permissions(request.getPermissions())
                .canManageUsers(request.getCanManageUsers() != null ? request.getCanManageUsers() : false)
                .canManageQueries(request.getCanManageQueries() != null ? request.getCanManageQueries() : false)
                .canApprovePasses(request.getCanApprovePasses() != null ? request.getCanApprovePasses() : false)
                .canManageRooms(request.getCanManageRooms() != null ? request.getCanManageRooms() : false)
                .build();

        // Create admin in MongoDB
        Admin admin = Admin.builder()
                .firebaseUid(firebaseUser.getUid()) // Add this field to your User model
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .userType(UserType.ADMIN)
                .accountStatus(AccountStatus.APPROVED)
                .isEnabled(true)
                .department(request.getDepartment())
                .adminPermissions(permissions)
                .approvedByAdminId(creatorAdminId)
                .approvalDate(LocalDateTime.now())
                .build();

        adminRepository.save(admin);

        return AuthResponse.builder()
                .message("Admin created successfully")
                .userId(admin.getId())
                .email(admin.getEmail())
                .userType(admin.getUserType().name())
                .accountStatus(admin.getAccountStatus().name())
                .build();
    }

    @Transactional
    public String approveStudent(ApprovalRequest request, String adminId) throws FirebaseAuthException {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (student.getAccountStatus() != AccountStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Student is not in pending approval state");
        }

        if (request.getApproved()) {
            // Enable user in Firebase
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(student.getFirebaseUid())
                    .setDisabled(false);
            FirebaseAuth.getInstance().updateUser(updateRequest);

            // Set custom claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "STUDENT");
            FirebaseAuth.getInstance().setCustomUserClaims(student.getFirebaseUid(), claims);

            // Update MongoDB
            student.setAccountStatus(AccountStatus.APPROVED);
            student.setIsEnabled(true);
            student.setApprovedByAdminId(adminId);
            student.setApprovalDate(LocalDateTime.now());
            studentRepository.save(student);

            return "Student approved successfully";
        } else {
            // Keep Firebase user disabled
            student.setAccountStatus(AccountStatus.REJECTED);
            student.setIsEnabled(false);
            student.setRejectionReason(request.getRejectionReason());
            studentRepository.save(student);

            return "Student rejected";
        }
    }
}