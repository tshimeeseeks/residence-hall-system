package com.rhs.backend.controller;

import com.rhs.backend.model.enums.AccountStatus;
import com.rhs.backend.model.SleepOverPass;
import com.rhs.backend.service.SleepOverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sleepover/applications")
public class SleepOverController {

    private final SleepOverService sleepoverService;

    @Autowired
    public SleepOverController(SleepOverService sleepoverService) {
        this.sleepoverService = sleepoverService;
    }

    @PostMapping
    public ResponseEntity<SleepOverPass> createSleepoverPass(@RequestBody SleepOverPass sleepoverPass) {
        try {
            SleepOverPass createdPass = sleepoverService.createSleepoverPass(sleepoverPass);
            return ResponseEntity.ok(createdPass);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<SleepOverPass>> getMySleepoverPasses(@RequestParam String applicantId) {
        return ResponseEntity.ok(sleepoverService.getMySleepoverPasses(applicantId));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<SleepOverPass>> getAllSleepoverPasses() {
        return ResponseEntity.ok(sleepoverService.getAllSleepoverPasses());
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<SleepOverPass> getSleepoverPassById(@PathVariable String applicationId) {
        return sleepoverService.getSleepoverPassById(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{applicationId}/review")
    public ResponseEntity<SleepOverPass> reviewSleepoverPass(@PathVariable String applicationId,
            @RequestParam AccountStatus status) {
        try {
            SleepOverPass reviewedPass = sleepoverService.reviewSleepoverPass(applicationId, status);
            return ResponseEntity.ok(reviewedPass);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
