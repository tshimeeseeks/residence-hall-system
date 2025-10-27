package com.rhs.backend.model;

import com.rhs.backend.model.embedded.GuestDetails;
import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    private String id;

    @Field("room_id")
    private String roomId;

    @Field("firebase_uid")
    private String firebaseUid;

    @Field("email")
    private String email;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("phone_number")
    private String phoneNumber;

    @Field("password")
    private String password;

    @Field("user_type")
    private UserType userType = UserType.STUDENT;

    @Field("account_status")
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Field("is_enabled")
    private Boolean isEnabled = false;

    @Field("student_number")
    private String studentNumber;

    @Field("course")
    private String course;

    @Field("year_of_study")
    private Integer yearOfStudy;

    @Field("emergency_contact")
    private String emergencyContact;

    @Field("profile_picture_url")
    private String profilePictureUrl;

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Builder pattern implementation
    public static StudentBuilder builder() {
        return new StudentBuilder();
    }

    public static class StudentBuilder {
        private String id;
        private String roomId;
        private String firebaseUid;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String password;
        private UserType userType = UserType.STUDENT;
        private AccountStatus accountStatus = AccountStatus.PENDING;
        private Boolean isEnabled = false;
        private String studentNumber;
        private String course;
        private Integer yearOfStudy;
        private String emergencyContact;
        private String profilePictureUrl;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt;

        public StudentBuilder id(String id) {
            this.id = id;
            return this;
        }

        public StudentBuilder roomId(String roomId) {
            this.roomId = roomId;
            return this;
        }

        public StudentBuilder firebaseUid(String firebaseUid) {
            this.firebaseUid = firebaseUid;
            return this;
        }

        public StudentBuilder email(String email) {
            this.email = email;
            return this;
        }

        public StudentBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public StudentBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public StudentBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public StudentBuilder password(String password) {
            this.password = password;
            return this;
        }

        public StudentBuilder userType(UserType userType) {
            this.userType = userType;
            return this;
        }

        public StudentBuilder accountStatus(AccountStatus accountStatus) {
            this.accountStatus = accountStatus;
            return this;
        }

        public StudentBuilder isEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public StudentBuilder studentNumber(String studentNumber) {
            this.studentNumber = studentNumber;
            return this;
        }

        public StudentBuilder course(String course) {
            this.course = course;
            return this;
        }

        public StudentBuilder yearOfStudy(Integer yearOfStudy) {
            this.yearOfStudy = yearOfStudy;
            return this;
        }

        public StudentBuilder emergencyContact(String emergencyContact) {
            this.emergencyContact = emergencyContact;
            return this;
        }

        public StudentBuilder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public StudentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudentBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Student build() {
            Student student = new Student();
            student.id = this.id;
            student.roomId = this.roomId;
            student.firebaseUid = this.firebaseUid;
            student.email = this.email;
            student.firstName = this.firstName;
            student.lastName = this.lastName;
            student.phoneNumber = this.phoneNumber;
            student.password = this.password;
            student.userType = this.userType;
            student.accountStatus = this.accountStatus;
            student.isEnabled = this.isEnabled;
            student.studentNumber = this.studentNumber;
            student.course = this.course;
            student.yearOfStudy = this.yearOfStudy;
            student.emergencyContact = this.emergencyContact;
            student.profilePictureUrl = this.profilePictureUrl;
            student.createdAt = this.createdAt;
            student.updatedAt = this.updatedAt;
            return student;
        }
    }

    /**
     * Create a new maintenance query
     */
    public MaintenanceQuery createMaintenanceQuery(MaintenanceQuery query) {
        query.setStudentId(this.id);
        query.setStudentName(this.firstName + " " + this.lastName);
        query.setStudentEmail(this.email);
        query.setRoomId(this.roomId);
        query.setCreatedAt(LocalDateTime.now());
        return query;
    }

    /**
     * Create a sleepover pass application
     */
    public SleepOverPass createSleepoverPass(SleepOverPass pass) {
        pass.setStudentId(this.id);
        pass.setCreatedAt(LocalDateTime.now());
        pass.setStatus("PENDING");
        return pass;
    }

    /**
     * Review maintenance queries created by this student
     */
    public List<MaintenanceQuery> reviewMyQueries(List<MaintenanceQuery> allQueries) {
        return allQueries.stream()
                .filter(query -> this.id.equals(query.getStudentId()))
                .toList();
    }

    /**
     * Review sleepover passes created by this student
     */
    public List<SleepOverPass> reviewMySleepoverPasses(List<SleepOverPass> allPasses) {
        return allPasses.stream()
                .filter(pass -> this.id.equals(pass.getStudentId()))
                .toList();
    }

    /**
     * Upload photo
     */
    public String uploadPhoto(String photoUrl) {
        return photoUrl;
    }

    /**
     * Get full name
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * Check if approved
     */
    public boolean isApproved() {
        return AccountStatus.APPROVED.equals(this.accountStatus);
    }

    /**
     * Check if student
     */
    public boolean isStudent() {
        return UserType.STUDENT.equals(this.userType);
    }

    /**
     * Check if has room
     */
    public boolean hasRoom() {
        return this.roomId != null && !this.roomId.isEmpty();
    }

    /**
     * Approve student
     */
    public void approve() {
        this.accountStatus = AccountStatus.APPROVED;
        this.isEnabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reject student
     */
    public void reject() {
        this.accountStatus = AccountStatus.REJECTED;
        this.isEnabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Suspend student
     */
    public void suspend() {
        this.accountStatus = AccountStatus.SUSPENDED;
        this.isEnabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update profile
     */
    public void updateProfile(String firstName, String lastName, String phoneNumber,
            String emergencyContact) {
        if (firstName != null)
            this.firstName = firstName;
        if (lastName != null)
            this.lastName = lastName;
        if (phoneNumber != null)
            this.phoneNumber = phoneNumber;
        if (emergencyContact != null)
            this.emergencyContact = emergencyContact;
        this.updatedAt = LocalDateTime.now();
    }
}