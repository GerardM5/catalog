package com.example.catalog.dto;

import java.util.Locale;

public enum ProductExportFormat {
    CSV("csv", "text/csv"),
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String extension;
    private final String contentType;

    ProductExportFormat(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static ProductExportFormat fromValue(String value) {
        if (value == null) {
            return CSV;
        }

        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "csv" -> CSV;
            case "excel", "xlsx" -> EXCEL;
            default -> throw new IllegalArgumentException("Unsupported export format: " + value);
        };
    }
}
