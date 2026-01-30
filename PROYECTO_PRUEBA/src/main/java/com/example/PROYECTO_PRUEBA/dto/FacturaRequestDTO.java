package com.example.PROYECTO_PRUEBA.dto;

import lombok.*;

import java.util.List;

/**
 * DTO para RECIBIR la solicitud de crear una factura
 * Este es el JSON que enviarás desde Postman
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaRequestDTO {

    // ===== DATOS DE LA FACTURA =====
    private String serie;              // Ej: "F001"
    private String observaciones;      // Comentarios opcionales

    // ===== IDs DE RELACIONES =====
    private Long idCliente;            // ID del cliente
    private Long idUsuario;            // ID del vendedor que genera la factura
    private Long idMoneda;             // ID de la moneda (PEN, USD, etc.)

    // ===== DETALLES (PRODUCTOS) =====
    private List<DetalleFacturaDTO> detalles; // Lista de productos a facturar

    // ===== PAGOS (OPCIONAL EN LA CREACIÓN) =====
    private List<PagoDTO> pagos;       // Lista de pagos (puede estar vacía inicialmente)
}