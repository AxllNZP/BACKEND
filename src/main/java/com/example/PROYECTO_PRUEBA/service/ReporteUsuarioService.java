// src/main/java/com/example/PROYECTO_PRUEBA/service/ReporteUsuarioService.java
package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.dto.UsuarioReporteDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteUsuarioService {

    List<UsuarioReporteDto> generarReporte(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long usuarioId
    );

    byte[] generarPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long usuarioId
    );

    byte[] generarExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long usuarioId
    );
}
