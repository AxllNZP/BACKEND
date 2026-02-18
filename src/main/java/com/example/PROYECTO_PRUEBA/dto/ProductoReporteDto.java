package com.example.PROYECTO_PRUEBA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
public class ProductoReporteDto {

    private Long idProducto;
    private String codigo;
    private String nombre;
    private BigDecimal precioActual;
    private Integer stockActual;

    private Integer totalUnidadesVendidas;
    private BigDecimal totalIngresos;
    private Long totalFacturas;

    private Set<String> clientes;
}
