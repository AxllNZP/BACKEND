package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.dto.ProductoReporteDto;
import com.example.PROYECTO_PRUEBA.model.DetalleFactura;
import com.example.PROYECTO_PRUEBA.model.Productos;
import com.example.PROYECTO_PRUEBA.repository.DetalleFacturaRepository;
import com.example.PROYECTO_PRUEBA.service.ReporteProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.lowagie.text.*;

import java.io.ByteArrayOutputStream;

// =======================
// OPENPDF (PDF)
// =======================
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;

// =======================
// APACHE POI (EXCEL)
// =======================
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Service
@RequiredArgsConstructor
public class ReporteProductoServiceImpl implements ReporteProductoService {

    private final DetalleFacturaRepository detalleRepository;

    @Override
    public List<ProductoReporteDto> generarReporteProductos(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long productoId) {

        // 🔥 Ahora el filtro lo hace la BD
        List<DetalleFactura> detalles =
                detalleRepository.findProductosVendidosByFechaBetween(
                        fechaInicio,
                        fechaFin,
                        productoId
                );

        if (detalles.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<DetalleFactura>> agrupado =
                detalles.stream()
                        .collect(Collectors.groupingBy(
                                d -> d.getProducto().getIdProducto()
                        ));

        List<ProductoReporteDto> reporte = new ArrayList<>();

        for (List<DetalleFactura> lista : agrupado.values()) {

            Productos producto = lista.get(0).getProducto();

            int totalUnidades = lista.stream()
                    .mapToInt(DetalleFactura::getCantidad)
                    .sum();

            BigDecimal totalIngresos = lista.stream()
                    .map(DetalleFactura::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            long totalFacturas = lista.stream()
                    .map(d -> d.getFactura().getIdFactura())
                    .distinct()
                    .count();

            Set<String> clientes = lista.stream()
                    .map(d -> d.getFactura()
                            .getCliente()
                            .getNombreRazonSocial())
                    .collect(Collectors.toSet());

            reporte.add(new ProductoReporteDto(
                    producto.getIdProducto(),
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getPrecio(),
                    producto.getStock(),
                    totalUnidades,
                    totalIngresos,
                    totalFacturas,
                    clientes
            ));
        }

        return reporte;
    }


    // ================= PDF =================

    @Override
    public byte[] generarReporteProductosPdf(LocalDateTime fechaInicio,
                                             LocalDateTime fechaFin,
                                             Long productoId) {

        List<ProductoReporteDto> productos =
                generarReporteProductos(fechaInicio, fechaFin, productoId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("REPORTE DE PRODUCTOS"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);

            table.addCell("Codigo");
            table.addCell("Producto");
            table.addCell("Unidades");
            table.addCell("Ingresos");
            table.addCell("Facturas");
            table.addCell("Clientes");

            for (ProductoReporteDto p : productos) {
                table.addCell(p.getCodigo());
                table.addCell(p.getNombre());
                table.addCell(String.valueOf(p.getTotalUnidadesVendidas()));
                table.addCell(p.getTotalIngresos().toString());
                table.addCell(String.valueOf(p.getTotalFacturas()));
                table.addCell(String.join(", ", p.getClientes()));
            }

            document.add(table);
            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    // ================= EXCEL =================

    @Override
    public byte[] generarReporteProductosExcel(LocalDateTime fechaInicio,
                                               LocalDateTime fechaFin,
                                               Long productoId) {

        List<ProductoReporteDto> productos =
                generarReporteProductos(fechaInicio, fechaFin, productoId);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Codigo");
            header.createCell(1).setCellValue("Producto");
            header.createCell(2).setCellValue("Unidades");
            header.createCell(3).setCellValue("Ingresos");
            header.createCell(4).setCellValue("Facturas");
            header.createCell(5).setCellValue("Clientes");

            int rowIndex = 1;

            for (ProductoReporteDto p : productos) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(p.getCodigo());
                row.createCell(1).setCellValue(p.getNombre());
                row.createCell(2).setCellValue(p.getTotalUnidadesVendidas());
                row.createCell(3).setCellValue(p.getTotalIngresos().doubleValue());
                row.createCell(4).setCellValue(p.getTotalFacturas());
                row.createCell(5).setCellValue(String.join(", ", p.getClientes()));
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

}
