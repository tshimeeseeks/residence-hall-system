package com.rhs.backend.security;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FirebaseUserDetails {
    private String uid;
    private String email;
    private boolean admin;
}
