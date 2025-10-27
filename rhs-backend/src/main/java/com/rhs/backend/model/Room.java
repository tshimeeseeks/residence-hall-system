package com.rhs.backend.model;

import com.rhs.backend.model.embedded.RoomDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    private String id;

    @Field("room_number")
    private String roomNumber;

    @Field("type")
    private String type;

    @Field("block")
    private String block;

    @Field("building")
    private String building;

    @Field("floor")
    private Integer floor;

    @Field("last_maintenance")
    private LocalDate lastMaintenance;

    @Field("capacity")
    private Integer capacity;

    @Field("current_occupancy")
    @Builder.Default
    private Integer currentOccupancy = 0;

    /**
     * Get room details as embedded object
     */
    public RoomDetails getRoomDetails() {
        return RoomDetails.builder()
                .roomId(this.id)
                .building(this.building)
                .floor(this.floor)
                .build();
    }

    /**
     * Update last maintenance date
     */
    public void updateLastMaintenanceDate(LocalDate date) {
        this.lastMaintenance = date;
    }

    /**
     * Get maintenance history for this room
     */
    public List<MaintenanceQuery> getMaintenanceHistory(List<MaintenanceQuery> allQueries) {
        return allQueries.stream()
                .filter(query -> this.id.equals(query.getRoomId()))
                .toList();
    }

    /**
     * Reset current resident count
     */
    public void resetCurrentResident() {
        this.currentOccupancy = 0;
    }

    /**
     * Increment occupancy when student moves in
     */
    public void incrementOccupancy() {
        if (this.currentOccupancy == null) {
            this.currentOccupancy = 0;
        }
        if (this.currentOccupancy < this.capacity) {
            this.currentOccupancy++;
        } else {
            throw new RuntimeException("Room is at full capacity");
        }
    }

    /**
     * Decrement occupancy when student moves out
     */
    public void decrementOccupancy() {
        if (this.currentOccupancy == null || this.currentOccupancy <= 0) {
            this.currentOccupancy = 0;
        } else {
            this.currentOccupancy--;
        }
    }

    /**
     * Check if room is available for new residents
     */
    public boolean isAvailable() {
        return this.currentOccupancy == null || this.currentOccupancy < this.capacity;
    }
}