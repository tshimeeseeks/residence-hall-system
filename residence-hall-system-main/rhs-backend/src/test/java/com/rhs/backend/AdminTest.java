package com.rhs.backend;

import org.junit.jupiter.api.Test;

import com.rhs.backend.model.Admin;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class AdminTest {

    private Admin admin;

    @BeforeEach
    void setUp() {
        admin = Admin.builder()
                .adminId("A001")
                .username("admin1")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@residence.com")
                .phoneNumber("123-456-7890")
                .password("password123")
                .role("Administrator")
                .permissions("ALL")
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
    }

    @Test
    void testAdminCreation() {
        assertNotNull(admin);
        assertEquals("A001", admin.getAdminId());
        assertEquals("admin1", admin.getUsername());
        assertEquals("John", admin.getFirstName());
        assertEquals("Doe", admin.getLastName());
        assertEquals("john.doe@residence.com", admin.getEmail());
    }

    @Test
    void testBuilderPattern() {
        Admin newAdmin = Admin.builder()
                .adminId("A002")
                .username("admin2")
                .email("admin2@residence.com")
                .role("Moderator")
                .build();

        assertNotNull(newAdmin);
        assertEquals("A002", newAdmin.getAdminId());
        assertEquals("admin2", newAdmin.getUsername());
        assertEquals("Moderator", newAdmin.getRole());
        assertNull(newAdmin.getFirstName()); // Should be null as not set
    }

    @Test
    void testNoArgsConstructor() {
        Admin emptyAdmin = new Admin();
        assertNotNull(emptyAdmin);
        assertNull(emptyAdmin.getAdminId());
        assertNull(emptyAdmin.getUsername());
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        Admin fullAdmin = new Admin("A003", "admin3", "Jane", "Smith",
                "jane@residence.com", "987-654-3210", "pass456",
                "SuperAdmin", "WRITE,READ", now, now);

        assertEquals("A003", fullAdmin.getAdminId());
        assertEquals("admin3", fullAdmin.getUsername());
        assertEquals("Jane", fullAdmin.getFirstName());
        assertEquals("SuperAdmin", fullAdmin.getRole());
    }

    @Test
    void testGettersAndSetters() {
        admin.setRole("Manager");
        admin.setPermissions("READ,WRITE");

        assertEquals("Manager", admin.getRole());
        assertEquals("READ,WRITE", admin.getPermissions());
    }

    @Test
    void testEqualsAndHashCode() {
        Admin admin1 = Admin.builder()
                .adminId("A001")
                .username("admin1")
                .firstName("John")
                .build();

        Admin admin2 = Admin.builder()
                .adminId("A001")
                .username("admin1")
                .firstName("John")
                .build();

        Admin admin3 = Admin.builder()
                .adminId("A002")
                .username("admin2")
                .firstName("Jane")
                .build();

        assertEquals(admin1, admin2);
        assertNotEquals(admin1, admin3);
        assertEquals(admin1.hashCode(), admin2.hashCode());
    }

    @Test
    void testToString() {
        String adminString = admin.toString();

        assertNotNull(adminString);
        assertTrue(adminString.contains("adminId=A001"));
        assertTrue(adminString.contains("username=admin1"));
        assertTrue(adminString.contains("firstName=John"));
    }

    @Test
    void testFieldValidation() {
        // Test required fields
        assertNotNull(admin.getAdminId());
        assertNotNull(admin.getUsername());

        // Test email format (basic check)
        assertTrue(admin.getEmail().contains("@"));

        // Test phone number format (basic check)
        assertTrue(admin.getPhoneNumber().contains("-"));
    }

    @Test
    void testDateFields() {
        assertNotNull(admin.getCreatedAt());
        assertNotNull(admin.getUpdatedAt());

        // Test that dates are reasonable (not in future beyond a few seconds)
        Date now = new Date();
        assertTrue(admin.getCreatedAt().before(now) ||
                Math.abs(admin.getCreatedAt().getTime() - now.getTime()) < 5000);
    }
}