package com.rhs.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.rhs.backend.model.embedded.RoomDetails;

import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Student extends User {

    @Field("student_number")
    @Indexed(unique = true)
    private String studentNumber;

    @Field("room_details")
    private RoomDetails roomDetails;

    @Field("course")
    private String course;

    @Field("year_of_study")
    private Integer yearOfStudy;
}
