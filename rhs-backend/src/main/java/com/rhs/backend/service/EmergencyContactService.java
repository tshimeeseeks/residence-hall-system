package com.rhs.backend.service;

import com.rhs.backend.model.EmergencyContact;
import com.rhs.backend.repository.EmergencyContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;

    @Autowired
    public EmergencyContactService(EmergencyContactRepository emergencyContactRepository) {
        this.emergencyContactRepository = emergencyContactRepository;
    }

    public List<EmergencyContact> getAllEmergencyContacts() {
        return emergencyContactRepository.findAll();
    }

    public void sendPriorityNotification(String message) {
        // In a real application, this would integrate with an SMS or email service
        System.out.println("Sending priority notification: " + message);
    }
}
