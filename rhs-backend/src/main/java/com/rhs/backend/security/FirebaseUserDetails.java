package com.rhs.backend.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseUserDetails {
    private String uid;
    private String email;
    private boolean admin;
}```

###**Step 4:Fix FirebaseAuthenticationFilter.java**

**Replace:**```rhs-backend/src/main/java/com/rhs/backend/security/FirebaseAuthenticationFilter.java