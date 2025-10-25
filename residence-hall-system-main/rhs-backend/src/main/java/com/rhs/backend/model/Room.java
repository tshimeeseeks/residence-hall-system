package com.rhs.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document(collection = "rooms")
public class Room {
  @Id
  private String id;

  @Indexed(unique = true)
  private String code;   // e.g., "A-102"

  private int capacity;  // max
  private int occupants; // current 0..capacity
}
