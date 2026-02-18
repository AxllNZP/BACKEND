package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.dto.ProductoReporteDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteProductoService {

    List<ProductoReporteDto> generarReporteProductos(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long productoId
    );

    byte[] generarReporteProductosPdf(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long productoId
    );

    byte[] generarReporteProductosExcel(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Long productoId
    );
}
