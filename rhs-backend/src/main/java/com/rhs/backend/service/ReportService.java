package com.rhs.backend.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.rhs.backend.model.MaintenanceQuery;
import com.rhs.backend.model.Reports;
import com.rhs.backend.model.enums.QueryStatus;
import com.rhs.backend.repository.MaintenanceRepository;
import com.rhs.backend.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MaintenanceRepository maintenanceRepository;
    private final ReportRepository reportRepository;

    @Value("${file.report-dir:reports/}")
    private String reportDir;

    /**
     * Generates a PDF maintenance report and saves metadata in Reports collection.
     */
    public byte[] generatePdfReport() {
        try {
            List<MaintenanceQuery> queries = maintenanceRepository.findAll();
            Map<String, Object> stats = calculateStats(queries);

            File dir = new File(reportDir);
            if (!dir.exists())
                dir.mkdirs();

            String fileName = "maintenance_report_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            String filePath = reportDir + fileName;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("RHS Maintenance Report").setBold().setFontSize(18));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()).setFontSize(10));
            document.add(new Paragraph("\nSummary Statistics:\n"));

            stats.forEach((k, v) -> document.add(new Paragraph(k + ": " + v)));

            Table table = new Table(5);
            table.addHeaderCell("Title");
            table.addHeaderCell("Priority");
            table.addHeaderCell("Status");
            table.addHeaderCell("Created At");
            table.addHeaderCell("Resolved At");

            queries.stream().limit(20).forEach(q -> {
                table.addCell(Optional.ofNullable(q.getQueryTitle()).orElse("-"));
                table.addCell(Optional.ofNullable(q.getPriority()).orElse("-"));
                table.addCell(q.getStatus() != null ? q.getStatus().name() : "-");
                table.addCell(q.getCreatedAt() != null ? q.getCreatedAt().toString() : "-");
                table.addCell(q.getResolvedAt() != null ? q.getResolvedAt().toString() : "-");
            });

            document.add(new Paragraph("\nRecent Maintenance Queries:"));
            document.add(table);
            document.close();

            // Save report metadata
            Reports report = new Reports();
            report.setReportType("Maintenance");
            report.setReportData(stats);
            report.setGeneratedAt(new Date());
            report.setFilePath(filePath);
            reportRepository.save(report);

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage());
        }
    }

    /**
     * Export data to Excel.
     */
    public byte[] exportToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Maintenance Queries");
            List<MaintenanceQuery> queries = maintenanceRepository.findAll();

            String[] headers = { "Title", "Priority", "Status", "Created At", "Resolved At" };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (MaintenanceQuery q : queries) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(Optional.ofNullable(q.getQueryTitle()).orElse("-"));
                row.createCell(1).setCellValue(Optional.ofNullable(q.getPriority()).orElse("-"));
                row.createCell(2).setCellValue(q.getStatus() != null ? q.getStatus().name() : "-");
                row.createCell(3).setCellValue(q.getCreatedAt() != null ? q.getCreatedAt().toString() : "-");
                row.createCell(4).setCellValue(q.getResolvedAt() != null ? q.getResolvedAt().toString() : "-");
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error exporting to Excel: " + e.getMessage());
        }
    }

    /**
     * Stats endpoints
     */
    public Map<String, Object> getMaintenanceStats() {
        List<MaintenanceQuery> queries = maintenanceRepository.findAll();
        return calculateStats(queries);
    }

    public Map<String, Object> getSleepoverStats() {
        return Map.of(
                "Total Sleepovers", 10,
                "Active", 7,
                "Completed", 3);
    }

    public Map<String, Object> getUserStats() {
        return Map.of(
                "Total Users", 45,
                "Active", 40,
                "Inactive", 5);
    }

    /**
     * Common stats calculator
     */
    private Map<String, Object> calculateStats(List<MaintenanceQuery> queries) {
        Map<String, Object> stats = new LinkedHashMap<>();
        long total = queries.size();
        long pending = queries.stream().filter(q -> q.getStatus() == QueryStatus.PENDING).count();
        long inProgress = queries.stream().filter(q -> q.getStatus() == QueryStatus.IN_PROGRESS).count();
        long resolved = queries.stream().filter(q -> q.getStatus() == QueryStatus.RESOLVED).count();

        stats.put("Total Queries", total);
        stats.put("Pending", pending);
        stats.put("In Progress", inProgress);
        stats.put("Resolved", resolved);
        stats.put("Generated At", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        return stats;
    }
}
