// src/main/java/com/example/PROYECTO_PRUEBA/dto/UsuarioReporteDto.java
package com.example.PROYECTO_PRUEBA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class UsuarioReporteDto {

    private Long idUsuario;
    private String nombreUsuario;
    private String nombreCompleto;
    private String rol;
    private String estado;

    private Long totalFacturas;
    private BigDecimal totalSubtotal;
    private BigDecimal totalIgv;
    private BigDecimal totalVendido;
}
