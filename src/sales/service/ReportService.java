package sales.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import sales.dao.ReportDao;
import sales.model.DashboardSummary;
import sales.model.InvoiceReportRow;
import sales.model.PaymentReportRow;
import sales.model.SalesOrderReportRow;

public class ReportService {

    private final ReportDao reportDao;

    public ReportService(ReportDao reportDao) {
        this.reportDao = reportDao;
    }

    public DashboardSummary getDashboardSummary() {
        return reportDao.getDashboardSummary();
    }

    public List<SalesOrderReportRow> getSalesOrderReport(LocalDate from, LocalDate to, String status) {
        validateDateRange(from, to);
        return reportDao.getSalesOrderReport(from, to, status);
    }

    public List<InvoiceReportRow> getInvoiceReport(LocalDate from, LocalDate to, String status) {
        validateDateRange(from, to);
        return reportDao.getInvoiceReport(from, to, status);
    }

    public List<PaymentReportRow> getPaymentReport(LocalDate from, LocalDate to, String method) {
        validateDateRange(from, to);
        return reportDao.getPaymentReport(from, to, method);
    }

    public void exportToCsv(List<String> headers, List<List<String>> rows, File outputFile) {
        if (outputFile == null) {
            throw new IllegalArgumentException("Target CSV file is required.");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(joinCsvRow(headers));
            writer.newLine();
            for (List<String> row : rows) {
                writer.write(joinCsvRow(row));
                writer.newLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to export CSV file: " + ex.getMessage(), ex);
        }
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Both Date From and Date To are required.");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Date From cannot be after Date To.");
        }
    }

    private String joinCsvRow(List<String> cells) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escapeCsv(cells.get(i)));
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        String raw = value == null ? "" : value;
        boolean needsQuotes = raw.contains(",") || raw.contains("\"") || raw.contains("\n") || raw.contains("\r");
        if (!needsQuotes) {
            return raw;
        }
        return "\"" + raw.replace("\"", "\"\"") + "\"";
    }
}
