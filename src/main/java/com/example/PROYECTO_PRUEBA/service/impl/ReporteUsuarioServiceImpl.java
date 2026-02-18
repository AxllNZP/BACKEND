// src/main/java/com/example/PROYECTO_PRUEBA/service/impl/ReporteUsuarioServiceImpl.java
package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.dto.UsuarioReporteDto;
import com.example.PROYECTO_PRUEBA.repository.IFacturaRepository;
import com.example.PROYECTO_PRUEBA.service.ReporteUsuarioService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteUsuarioServiceImpl implements ReporteUsuarioService {

    private final IFacturaRepository facturaRepository;

    @Override
    public List<UsuarioReporteDto> generarReporte(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long usuarioId) {

        return facturaRepository.generarReporteUsuarios(
                fechaInicio,
                fechaFin,
                usuarioId
        );
    }

    @Override
    public byte[] generarPdf(LocalDateTime fechaInicio,
                             LocalDateTime fechaFin,
                             Long usuarioId) {

        List<UsuarioReporteDto> lista =
                generarReporte(fechaInicio, fechaFin, usuarioId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("REPORTE DE USUARIOS"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            table.addCell("Usuario");
            table.addCell("Nombre Completo");
            table.addCell("Rol");
            table.addCell("Estado");
            table.addCell("Facturas");
            table.addCell("Subtotal");
            table.addCell("IGV");
            table.addCell("Total");

            for (UsuarioReporteDto u : lista) {
                table.addCell(u.getNombreUsuario());
                table.addCell(u.getNombreCompleto());
                table.addCell(u.getRol());
                table.addCell(u.getEstado());
                table.addCell(String.valueOf(u.getTotalFacturas()));
                table.addCell(u.getTotalSubtotal().toString());
                table.addCell(u.getTotalIgv().toString());
                table.addCell(u.getTotalVendido().toString());
            }

            document.add(table);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    @Override
    public byte[] generarExcel(LocalDateTime fechaInicio,
                               LocalDateTime fechaFin,
                               Long usuarioId) {

        List<UsuarioReporteDto> lista =
                generarReporte(fechaInicio, fechaFin, usuarioId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte Usuarios");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Usuario");
            header.createCell(1).setCellValue("Nombre Completo");
            header.createCell(2).setCellValue("Rol");
            header.createCell(3).setCellValue("Estado");
            header.createCell(4).setCellValue("Facturas");
            header.createCell(5).setCellValue("Subtotal");
            header.createCell(6).setCellValue("IGV");
            header.createCell(7).setCellValue("Total");

            int rowIndex = 1;

            for (UsuarioReporteDto u : lista) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(u.getNombreUsuario());
                row.createCell(1).setCellValue(u.getNombreCompleto());
                row.createCell(2).setCellValue(u.getRol());
                row.createCell(3).setCellValue(u.getEstado());
                row.createCell(4).setCellValue(u.getTotalFacturas());
                row.createCell(5).setCellValue(u.getTotalSubtotal().doubleValue());
                row.createCell(6).setCellValue(u.getTotalIgv().doubleValue());
                row.createCell(7).setCellValue(u.getTotalVendido().doubleValue());
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }
}
