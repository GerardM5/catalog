package com.example.catalog.service;

import com.example.catalog.dto.ProductExportFormat;

public class ProductExportFile {

    private final String filename;
    private final String contentType;
    private final byte[] content;

    public ProductExportFile(String filename, String contentType, byte[] content) {
        this.filename = filename;
        this.contentType = contentType;
        this.content = content;
    }

    public static ProductExportFile of(String baseFilename, ProductExportFormat format, byte[] content) {
        return new ProductExportFile(
                baseFilename + "." + format.getExtension(),
                format.getContentType(),
                content
        );
    }

    public String getFilename() {
        return filename;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
