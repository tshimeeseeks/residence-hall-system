package com.rhs.backend.controller;

import com.rhs.backend.model.EmergencyContact;
import com.rhs.backend.service.EmergencyContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/emergency")
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @Autowired
    public EmergencyContactController(EmergencyContactService emergencyContactService) {
        this.emergencyContactService = emergencyContactService;
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<EmergencyContact>> getAllEmergencyContacts() {
        return ResponseEntity.ok(emergencyContactService.getAllEmergencyContacts());
    }

    @PostMapping("/notify")
    public ResponseEntity<Void> sendPriorityNotification(@RequestBody Map<String, String> payload) {
        emergencyContactService.sendPriorityNotification(payload.get("message"));
        return ResponseEntity.ok().build();
    }
}
