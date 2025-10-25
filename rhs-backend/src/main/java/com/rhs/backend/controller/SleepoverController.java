package com.rhs.backend.controller;

import com.rhs.backend.model.ApprovalStatus;
import com.rhs.backend.model.SleepoverPass;
import com.rhs.backend.service.SleepoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sleepover/applications")
public class SleepoverController {

    private final SleepoverService sleepoverService;

    @Autowired
    public SleepoverController(SleepoverService sleepoverService) {
        this.sleepoverService = sleepoverService;
    }

    @PostMapping
    public ResponseEntity<SleepoverPass> createSleepoverPass(@RequestBody SleepoverPass sleepoverPass) {
        try {
            SleepoverPass createdPass = sleepoverService.createSleepoverPass(sleepoverPass);
            return ResponseEntity.ok(createdPass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<SleepoverPass>> getMySleepoverPasses(@RequestParam String applicantId) {
        return ResponseEntity.ok(sleepoverService.getMySleepoverPasses(applicantId));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<SleepoverPass>> getAllSleepoverPasses() {
        return ResponseEntity.ok(sleepoverService.getAllSleepoverPasses());
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<SleepoverPass> getSleepoverPassById(@PathVariable String applicationId) {
        return sleepoverService.getSleepoverPassById(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{applicationId}/review")
    public ResponseEntity<SleepoverPass> reviewSleepoverPass(@PathVariable String applicationId, @RequestParam ApprovalStatus status) {
        try {
            SleepoverPass reviewedPass = sleepoverService.reviewSleepoverPass(applicationId, status);
            return ResponseEntity.ok(reviewedPass);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
