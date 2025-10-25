package com.rhs.backend;

import org.junit.jupiter.api.Test;

import com.rhs.backend.model.User;

import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId("U001")
                .username("student1")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@student.com")
                .phoneNumber("555-123-4567")
                .password("studentpass")
                .roomId("R101")
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals("U001", user.getUserId());
        assertEquals("student1", user.getUsername());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("jane.smith@student.com", user.getEmail());
        assertEquals("R101", user.getRoomId());
    }

    @Test
    void testBuilderPattern() {
        User newUser = User.builder()
                .userId("U002")
                .username("student2")
                .email("student2@student.com")
                .roomId("R202")
                .build();

        assertNotNull(newUser);
        assertEquals("U002", newUser.getUserId());
        assertEquals("student2", newUser.getUsername());
        assertEquals("R202", newUser.getRoomId());
        assertNull(newUser.getFirstName()); // Should be null as not set
    }

    @Test
    void testNoArgsConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getUserId());
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getRoomId());
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        User fullUser = new User("U003", "student3", "Bob", "Johnson",
                "bob@student.com", "444-555-6666", "password123",
                "R303", now, now);

        assertEquals("U003", fullUser.getUserId());
        assertEquals("student3", fullUser.getUsername());
        assertEquals("Bob", fullUser.getFirstName());
        assertEquals("R303", fullUser.getRoomId());
    }

    @Test
    void testGettersAndSetters() {
        user.setRoomId("R999");
        user.setPhoneNumber("111-222-3333");

        assertEquals("R999", user.getRoomId());
        assertEquals("111-222-3333", user.getPhoneNumber());
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = User.builder()
                .userId("U001")
                .username("student1")
                .firstName("Jane")
                .roomId("R101")
                .build();

        User user2 = User.builder()
                .userId("U001")
                .username("student1")
                .firstName("Jane")
                .roomId("R101")
                .build();

        User user3 = User.builder()
                .userId("U002")
                .username("student2")
                .firstName("Bob")
                .roomId("R202")
                .build();

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testToString() {
        String userString = user.toString();

        assertNotNull(userString);
        assertTrue(userString.contains("userId=U001"));
        assertTrue(userString.contains("username=student1"));
        assertTrue(userString.contains("roomId=R101"));
    }

    @Test
    void testFieldValidation() {
        // Test required fields
        assertNotNull(user.getUserId());
        assertNotNull(user.getUsername());
        assertNotNull(user.getRoomId());

        // Test email format (basic check)
        assertTrue(user.getEmail().contains("@"));

        // Test room ID format (basic check)
        assertTrue(user.getRoomId().startsWith("R"));
    }

    @Test
    void testRoomIdFormat() {
        // Test various room ID formats
        user.setRoomId("R101");
        assertTrue(user.getRoomId().matches("R\\d+"));

        user.setRoomId("R999");
        assertTrue(user.getRoomId().matches("R\\d+"));
    }

    @Test
    void testDateFields() {
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        // Test that dates are reasonable (not in future beyond a few seconds)
        Date now = new Date();
        assertTrue(user.getCreatedAt().before(now) ||
                Math.abs(user.getCreatedAt().getTime() - now.getTime()) < 5000);
    }

    @Test
    void testPasswordHandling() {
        // Basic password tests
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().length() > 0);

        user.setPassword("newPassword123");
        assertEquals("newPassword123", user.getPassword());
    }
}