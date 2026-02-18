// src/main/java/com/example/PROYECTO_PRUEBA/controller/ReporteUsuarioController.java
package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.dto.UsuarioReporteDto;
import com.example.PROYECTO_PRUEBA.service.ReporteUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes/usuarios")
@RequiredArgsConstructor
public class ReporteUsuarioController {

    private final ReporteUsuarioService reporteService;

    @GetMapping
    public ResponseEntity<List<UsuarioReporteDto>> obtener(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Long usuarioId
    ) {

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);

        return ResponseEntity.ok(
                reporteService.generarReporte(inicio, fin, usuarioId)
        );
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Long usuarioId
    ) {

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);

        byte[] file = reporteService.generarPdf(inicio, fin, usuarioId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_usuarios.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> excel(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) Long usuarioId
    ) {

        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);

        byte[] file = reporteService.generarExcel(inicio, fin, usuarioId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_usuarios.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }
}
