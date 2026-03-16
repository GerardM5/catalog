package com.example.catalog.service;

import com.example.catalog.dto.ProductExportFormat;
import com.example.catalog.dto.ProductResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ProductExportWriter {

    private static final String[] HEADERS = {
            "id", "referencia", "nombre", "descripcion", "peso", "volumen", "color",
            "ancho", "largo", "alto", "precio", "empresa", "categoria", "origenPdf"
    };

    public ProductExportFile write(List<ProductResponse> products, ProductExportFormat format) {
        byte[] content = switch (format) {
            case CSV -> writeCsv(products);
            case EXCEL -> writeExcel(products);
        };

        return ProductExportFile.of("products", format, content);
    }

    private byte[] writeCsv(List<ProductResponse> products) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.join(",", HEADERS)).append('\n');

        for (ProductResponse product : products) {
            builder.append(csvValue(product.getId()))
                    .append(',').append(csvValue(product.getReferencia()))
                    .append(',').append(csvValue(product.getNombre()))
                    .append(',').append(csvValue(product.getDescripcion()))
                    .append(',').append(csvValue(product.getPeso()))
                    .append(',').append(csvValue(product.getVolumen()))
                    .append(',').append(csvValue(product.getColor()))
                    .append(',').append(csvValue(product.getAncho()))
                    .append(',').append(csvValue(product.getLargo()))
                    .append(',').append(csvValue(product.getAlto()))
                    .append(',').append(csvValue(product.getPrecio()))
                    .append(',').append(csvValue(product.getEmpresa()))
                    .append(',').append(csvValue(product.getCategoria()))
                    .append(',').append(csvValue(product.getOrigenPdf()))
                    .append('\n');
        }

        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] writeExcel(List<ProductResponse> products) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("products");
            writeHeaderRow(sheet.createRow(0));

            int rowIndex = 1;
            for (ProductResponse product : products) {
                writeProductRow(sheet.createRow(rowIndex++), product);
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to generate Excel export", exception);
        }
    }

    private void writeHeaderRow(Row row) {
        for (int i = 0; i < HEADERS.length; i++) {
            row.createCell(i).setCellValue(HEADERS[i]);
        }
    }

    private void writeProductRow(Row row, ProductResponse product) {
        setCellValue(row.createCell(0), product.getId());
        setCellValue(row.createCell(1), product.getReferencia());
        setCellValue(row.createCell(2), product.getNombre());
        setCellValue(row.createCell(3), product.getDescripcion());
        setCellValue(row.createCell(4), product.getPeso());
        setCellValue(row.createCell(5), product.getVolumen());
        setCellValue(row.createCell(6), product.getColor());
        setCellValue(row.createCell(7), product.getAncho());
        setCellValue(row.createCell(8), product.getLargo());
        setCellValue(row.createCell(9), product.getAlto());
        setCellValue(row.createCell(10), product.getPrecio());
        setCellValue(row.createCell(11), product.getEmpresa());
        setCellValue(row.createCell(12), product.getCategoria());
        setCellValue(row.createCell(13), product.getOrigenPdf());
    }

    private String csvValue(Object value) {
        if (value == null) {
            return "";
        }

        String text = value.toString().replace("\"", "\"\"");
        return "\"" + text + "\"";
    }

    private void setCellValue(Cell cell, Object value) {
        cell.setCellValue(value == null ? "" : value.toString());
    }
}
