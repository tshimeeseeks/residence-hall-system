package com.rhs.backend.controller;

import com.rhs.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/maintenance/stats")
    public ResponseEntity<Map<String, Object>> getMaintenanceStats() {
        return ResponseEntity.ok(reportService.getMaintenanceStats());
    }

    @GetMapping("/sleepover/stats")
    public ResponseEntity<Map<String, Object>> getSleepoverStats() {
        return ResponseEntity.ok(reportService.getSleepoverStats());
    }



    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        return ResponseEntity.ok(reportService.getUserStats());
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdfReport() {
        byte[] pdf = reportService.generatePdfReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        byte[] excel = reportService.exportToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
