package com.rhs.Maintenance.controller;


import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rhs.Maintenance.dto.MaintenanceQueryDto;
import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.model.enums.PriorityLevel;
import com.rhs.Maintenance.model.enums.Status;
import com.rhs.Maintenance.service.MaintenanceService;
import com.rhs.Maintenance.service.ReportService;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<MaintenanceQuery> create(@RequestBody MaintenanceQueryDto dto) {
        return ResponseEntity.ok(maintenanceService.createQuery(dto));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceQuery>> list(
            @RequestParam(required = false) PriorityLevel priority,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(maintenanceService.searchQueries(priority, status, start, end));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceQuery> updateStatus(
            @PathVariable ObjectId id,
            @RequestParam Status status,
            @RequestParam(required = false) ObjectId adminId
    ) {
        if (adminId != null)
            return ResponseEntity.ok(maintenanceService.assignTo(id, adminId));
        else
            return ResponseEntity.ok(maintenanceService.updateStatus(id, status));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        return ResponseEntity.ok(maintenanceService.getStats());
    }

    @PostMapping("/report")
    public ResponseEntity<String> generateReport() throws FileNotFoundException {
        return ResponseEntity.ok(reportService.generateReport());
    }
}
