package com.rhs.backend.service;

import com.rhs.backend.model.Room;
import com.rhs.backend.model.Student;
import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.embedded.RoomDetails;
import com.rhs.backend.repository.RoomRepository;
import com.rhs.backend.repository.StudentRepository;
import com.rhs.backend.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final MaintenanceRepository maintenanceRepository;

    /**
     * Get room by ID
     */
    @Transactional(readOnly = true)
    public Room getRoomById(String roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + roomId));
    }

    /**
     * Get room by room number
     */
    @Transactional(readOnly = true)
    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found with number: " + roomNumber));
    }

    /**
     * Get room details (embedded object)
     */
    @Transactional(readOnly = true)
    public RoomDetails getRoomDetails(String roomId) {
        Room room = getRoomById(roomId);
        return room.getRoomDetails();
    }

    /**
     * Update last maintenance date for a room
     */
    @Transactional
    public Room updateLastMaintenanceDate(String roomId, LocalDate date) {
        Room room = getRoomById(roomId);
        room.updateLastMaintenanceDate(date);
        room = roomRepository.save(room);

        log.info("Updated last maintenance date for room: {} to {}", roomId, date);
        return room;
    }

    /**
     * Get maintenance history for a room
     */
    @Transactional(readOnly = true)
    public List<MaintenanceQuery> getRoomMaintenanceHistory(String roomId) {
        Room room = getRoomById(roomId);
        List<MaintenanceQuery> allQueries = maintenanceRepository.findByRoomId(roomId);
        return room.getMaintenanceHistory(allQueries);
    }

    /**
     * Get all rooms in a block
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsByBlock(String block) {
        return roomRepository.findByBlock(block);
    }

    /**
     * Get all rooms in a building
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsByBuilding(String building) {
        return roomRepository.findByBuilding(building);
    }

    /**
     * Get all rooms on a specific floor in a building
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsByBuildingAndFloor(String building, Integer floor) {
        return roomRepository.findByBuildingAndFloor(building, floor);
    }

    /**
     * Get all rooms by type
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsByType(String type) {
        return roomRepository.findByType(type);
    }

    /**
     * Get available rooms
     */
    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    /**
     * Create a new room
     */
    @Transactional
    public Room createRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException("Room number already exists: " + room.getRoomNumber());
        }

        // Set default values
        if (room.getCurrentOccupancy() == null) {
            room.setCurrentOccupancy(0);
        }
        if (room.getCapacity() == null) {
            room.setCapacity(getDefaultCapacityByType(room.getType()));
        }

        room = roomRepository.save(room);
        log.info("Created new room: {} in building {} floor {}",
                room.getRoomNumber(), room.getBuilding(), room.getFloor());
        return room;
    }

    /**
     * Update room information
     */
    @Transactional
    public Room updateRoom(String roomId, Room updatedRoom) {
        Room room = getRoomById(roomId);

        if (updatedRoom.getRoomNumber() != null && !updatedRoom.getRoomNumber().equals(room.getRoomNumber())) {
            if (roomRepository.existsByRoomNumber(updatedRoom.getRoomNumber())) {
                throw new RuntimeException("Room number already exists: " + updatedRoom.getRoomNumber());
            }
            room.setRoomNumber(updatedRoom.getRoomNumber());
        }

        if (updatedRoom.getType() != null) {
            room.setType(updatedRoom.getType());
        }
        if (updatedRoom.getBlock() != null) {
            room.setBlock(updatedRoom.getBlock());
        }
        if (updatedRoom.getBuilding() != null) {
            room.setBuilding(updatedRoom.getBuilding());
        }
        if (updatedRoom.getFloor() != null) {
            room.setFloor(updatedRoom.getFloor());
        }
        if (updatedRoom.getCapacity() != null) {
            room.setCapacity(updatedRoom.getCapacity());
        }

        room = roomRepository.save(room);
        log.info("Updated room: {}", roomId);
        return room;
    }

    /**
     * Assign student to room
     */
    @Transactional
    public Room assignStudentToRoom(String roomId, String studentId) {
        Room room = getRoomById(roomId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (!room.isAvailable()) {
            throw new RuntimeException("Room is at full capacity");
        }

        // Remove student from previous room if any
        if (student.getRoomId() != null) {
            removeStudentFromRoom(student.getRoomId(), studentId);
        }

        // Assign to new room
        student.setRoomId(roomId);
        studentRepository.save(student);

        room.incrementOccupancy();
        room = roomRepository.save(room);

        log.info("Assigned student {} to room {}", studentId, roomId);
        return room;
    }

    /**
     * Remove student from room
     */
    @Transactional
    public Room removeStudentFromRoom(String roomId, String studentId) {
        Room room = getRoomById(roomId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getRoomId() == null || !student.getRoomId().equals(roomId)) {
            throw new RuntimeException("Student is not assigned to this room");
        }

        student.setRoomId(null);
        studentRepository.save(student);

        room.decrementOccupancy();
        room = roomRepository.save(room);

        log.info("Removed student {} from room {}", studentId, roomId);
        return room;
    }

    /**
     * Get rooms that need maintenance
     */
    @Transactional(readOnly = true)
    public List<Room> getRoomsNeedingMaintenance(int monthsAgo) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(monthsAgo);

        List<Room> roomsBeforeCutoff = roomRepository.findByLastMaintenanceBefore(cutoffDate);
        List<Room> roomsNeverMaintained = roomRepository.findByLastMaintenanceIsNull();

        roomsBeforeCutoff.addAll(roomsNeverMaintained);
        return roomsBeforeCutoff;
    }

    /**
     * Reset all residents from a room
     */
    @Transactional
    public void resetCurrentResident(String roomId) {
        Room room = getRoomById(roomId);

        // Find all students in this room
        List<Student> residents = studentRepository.findAll().stream()
                .filter(s -> roomId.equals(s.getRoomId()))
                .toList();

        // Remove room assignment from all students
        for (Student resident : residents) {
            resident.setRoomId(null);
            studentRepository.save(resident);
        }

        // Reset room occupancy
        room.resetCurrentResident();
        roomRepository.save(room);

        log.info("Reset all residents for room: {}", roomId);
    }

    /**
     * Delete room
     */
    @Transactional
    public void deleteRoom(String roomId) {
        Room room = getRoomById(roomId);

        if (room.getCurrentOccupancy() != null && room.getCurrentOccupancy() > 0) {
            throw new RuntimeException("Cannot delete room with current residents");
        }

        roomRepository.delete(room);
        log.info("Deleted room: {}", roomId);
    }

    /**
     * Get default capacity based on room type
     */
    private Integer getDefaultCapacityByType(String type) {
        if (type == null)
            return 1;

        return switch (type.toUpperCase()) {
            case "SINGLE" -> 1;
            case "DOUBLE" -> 2;
            case "TRIPLE" -> 3;
            case "QUAD" -> 4;
            default -> 1;
        };
    }
}