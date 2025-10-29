package com.rhs.backend.service;

import com.rhs.backend.model.Room;
import com.rhs.backend.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    /**
     * Get all rooms
     */
    public List<Room> getAllRooms() {
        log.info("Fetching all rooms");
        return roomRepository.findAll();
    }

    /**
     * Get all available rooms (rooms with available space)
     */
    public List<Room> getAvailableRooms() {
        log.info("Fetching available rooms");
        List<Room> availableRooms = roomRepository.findAllAvailableRooms();
        log.info("Found {} available rooms", availableRooms.size());
        return availableRooms;
    }

    /**
     * Get room by ID
     */
    public Optional<Room> getRoomById(String id) {
        log.info("Fetching room with ID: {}", id);
        return roomRepository.findById(id);
    }

    /**
     * Get room by room number
     */
    public Optional<Room> getRoomByNumber(String roomNumber) {
        log.info("Fetching room with number: {}", roomNumber);
        return roomRepository.findByRoomNumber(roomNumber);
    }

    /**
     * Get rooms by building
     */
    public List<Room> getRoomsByBuilding(String building) {
        log.info("Fetching rooms in building: {}", building);
        return roomRepository.findByBuilding(building);
    }

    /**
     * Save or update room
     */
    public Room saveRoom(Room room) {
        log.info("Saving room: {}", room.getRoomNumber());
        return roomRepository.save(room);
    }

    /**
     * Delete room
     */
    public void deleteRoom(String id) {
        log.info("Deleting room with ID: {}", id);
        roomRepository.deleteById(id);
    }
}