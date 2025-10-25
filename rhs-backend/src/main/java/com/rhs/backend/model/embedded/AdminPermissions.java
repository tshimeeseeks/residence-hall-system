package com.rhs.backend.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPermissions {
    @Field("role")
    private String role;

    @Field("permissions")
    private List<String> permissions;

    @Field("can_manage_users")
    private Boolean canManageUsers;

    @Field("can_manage_queries")
    private Boolean canManageQueries;

    @Field("can_approve_passes")
    private Boolean canApprovePasses;

    @Field("can_manage_rooms")
    private Boolean canManageRooms;
}
