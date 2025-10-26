package com.rhs.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class Reports {

    @Id
    private ObjectId id;

    private String reportType;
    private Object reportData;
    private Map<String, Object> parameters;
    private ObjectId generatedBy;
    private Date generatedAt;
    private String filePath;

    // === UML methods (stubs for clarity) ===

    public void generateMaintenanceReport(Date startDate, Date endDate) {
        // handled by ReportService, stub for reference
    }

    public void generateSleepoverReport(Date startDate, Date endDate) {
        // handled by ReportService, stub for reference
    }

    public String exportToPDF() {
        // handled by ReportService, stub for reference
        return filePath;
    }

    public String exportToExcel() {
        // handled by ReportService, stub for reference
        return filePath;
    }

    public void scheduleReport(String frequency) {
        // handled by a scheduler in ReportService (future expansion)
    }
}
