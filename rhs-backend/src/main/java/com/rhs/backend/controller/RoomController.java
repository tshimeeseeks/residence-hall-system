package com.rhs.backend.controller;

import com.rhs.backend.model.Room;
import com.rhs.backend.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        try {
            log.info("Received request to get all rooms");
            List<Room> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("Error fetching all rooms", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableRooms() {
        try {
            log.info("Received request to get available rooms");
            List<Room> availableRooms = roomService.getAvailableRooms();

            // Transform to match frontend expectations
            List<Map<String, Object>> response = availableRooms.stream()
                    .map(room -> {
                        Map<String, Object> roomData = new HashMap<>();
                        roomData.put("id", room.getId());
                        roomData.put("roomNumber", room.getRoomNumber());
                        roomData.put("building", room.getBuilding());
                        roomData.put("floor", room.getFloor());
                        roomData.put("capacity", room.getCapacity());
                        roomData.put("occupiedSpaces",
                                room.getCurrentOccupancy() != null ? room.getCurrentOccupancy() : 0);
                        roomData.put("availableSpaces", room.getCapacity()
                                - (room.getCurrentOccupancy() != null ? room.getCurrentOccupancy() : 0));
                        roomData.put("roomType", room.getType());
                        return roomData;
                    })
                    .toList();

            log.info("Returning {} available rooms", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching available rooms", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch available rooms");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * GET /api/rooms/{id} - Get room by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable String id) {
        try {
            log.info("Received request to get room with ID: {}", id);
            return roomService.getRoomById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching room by ID", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch room");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * GET /api/rooms/number/{roomNumber} - Get room by room number
     */
    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<?> getRoomByNumber(@PathVariable String roomNumber) {
        try {
            log.info("Received request to get room with number: {}", roomNumber);
            return roomService.getRoomByNumber(roomNumber)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching room by number", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch room");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * POST /api/rooms - Create new room (admin only)
     */
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        try {
            log.info("Received request to create new room: {}", room.getRoomNumber());
            Room savedRoom = roomService.saveRoom(room);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
        } catch (Exception e) {
            log.error("Error creating room", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create room");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * PUT /api/rooms/{id} - Update room (admin only)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable String id, @RequestBody Room room) {
        try {
            log.info("Received request to update room with ID: {}", id);
            room.setId(id);
            Room updatedRoom = roomService.saveRoom(room);
            return ResponseEntity.ok(updatedRoom);
        } catch (Exception e) {
            log.error("Error updating room", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to update room");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DELETE /api/rooms/{id} - Delete room (admin only)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable String id) {
        try {
            log.info("Received request to delete room with ID: {}", id);
            roomService.deleteRoom(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Room deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting room", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete room");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}