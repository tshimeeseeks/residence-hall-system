package com.rhs.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.rhs.backend.model.embedded.AdminPermissions;

@Document(collection = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {

    @Field("admin_permissions")
    private AdminPermissions adminPermissions;

    @Field("department")
    private String department;
}