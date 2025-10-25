package com.rhs.backend.model.embedded;

 

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetails {
    @Field("room_id")
    private String roomId;

    @Field("building")
    private String building;

    @Field("floor")
    private Integer floor;
}
