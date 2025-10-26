package com.rhs.backend.controller;

import com.rhs.backend.model.User;
import com.rhs.backend.security.FirebaseUserDetails;
import com.rhs.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        
        User user = userService.syncUser(userDetails.getUid(), userDetails.getEmail());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", user);
        response.put("isAdmin", userDetails.isAdmin());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal FirebaseUserDetails userDetails) {
        
        User user = userService.getUserByFirebaseUid(userDetails.getUid())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("isAdmin", userDetails.isAdmin());
        
        return ResponseEntity.ok(response);
    }
}
