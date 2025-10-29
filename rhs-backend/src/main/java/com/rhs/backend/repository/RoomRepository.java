package com.rhs.backend.repository;

import com.rhs.backend.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByBlock(String block);

    List<Room> findByBuilding(String building);

    List<Room> findByFloor(Integer floor);

    List<Room> findByBuildingAndFloor(String building, Integer floor);

    List<Room> findByType(String type);

    List<Room> findByBlockAndType(String block, String type);

    @Query("{ $expr: { $lt: ['$current_occupancy', '$capacity'] } }")
    List<Room> findAllAvailableRooms();

    List<Room> findByLastMaintenanceBefore(LocalDate date);

    List<Room> findByLastMaintenanceIsNull();

    boolean existsByRoomNumber(String roomNumber);

    long countByType(String type);

    long countByBlock(String block);

    long countByBuilding(String building);

    @Query(sort = "{ 'roomNumber' : 1 }")
    List<Room> findAllOrderByRoomNumber();
}