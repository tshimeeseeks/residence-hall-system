package com.rhs.Maintenance.service;


import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.rhs.Maintenance.model.MaintenanceQuery;
import com.rhs.Maintenance.repository.MaintenanceQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MaintenanceService maintenanceService;
    private final MaintenanceQueryRepository repository;

    @Value("${file.report-dir:reports/}")
    private String reportDir;

    public String generateReport() throws FileNotFoundException {
        List<MaintenanceQuery> queries = repository.findAll();
        Map<String, Object> stats = maintenanceService.getStats();

        File dir = new File(reportDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = "maintenance_report_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
        String filePath = reportDir + fileName;

        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("RHS Maintenance Report").setBold().setFontSize(18));
        document.add(new Paragraph("Generated: " + LocalDateTime.now()).setFontSize(10));
        document.add(new Paragraph("\nSummary Statistics:\n"));

        stats.forEach((k, v) -> document.add(new Paragraph(k + ": " + v)));

        document.add(new Paragraph("\nRecent Maintenance Queries:"));
        Table table = new Table(5);
        table.addHeaderCell("Issue Type");
        table.addHeaderCell("Priority");
        table.addHeaderCell("Status");
        table.addHeaderCell("Reported At");
        table.addHeaderCell("Resolved At");

        queries.stream().limit(20).forEach(q -> {
            table.addCell(q.getIssueType());
            table.addCell(q.getPriority().name());
            table.addCell(q.getStatus().name());
            table.addCell(q.getReportedAt() != null ? q.getReportedAt().toString() : "-");
            table.addCell(q.getResolvedAt() != null ? q.getResolvedAt().toString() : "-");
        });

        document.add(table);
        document.close();
        return filePath;
    }
}

