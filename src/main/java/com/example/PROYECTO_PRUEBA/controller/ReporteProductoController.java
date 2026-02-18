package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.dto.ProductoReporteDto;
import com.example.PROYECTO_PRUEBA.service.ReporteProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes/productos")
@RequiredArgsConstructor
public class ReporteProductoController {

    private final ReporteProductoService reporteService;

    // 1️⃣ JSON
    @GetMapping
    public ResponseEntity<List<ProductoReporteDto>> obtenerReporte(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Long productoId
    ) {

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);

        return ResponseEntity.ok(
                reporteService.generarReporteProductos(inicio, fin, productoId)
        );
    }

    // 2️⃣ PDF
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Long productoId
    ) {

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);

        byte[] pdf = reporteService
                .generarReporteProductosPdf(inicio, fin, productoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_productos.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // 3️⃣ EXCEL
    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarExcel(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Long productoId
    ) {

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);

        byte[] excel = reporteService
                .generarReporteProductosExcel(inicio, fin, productoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_productos.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

}
