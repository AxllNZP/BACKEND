package com.example.PROYECTO_PRUEBA.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para los detalles de una factura (productos)
 * Representa cada línea de producto en la factura
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleFacturaDTO {

    // No enviamos el ID porque se genera automáticamente

    private Long idProducto;           // ID del producto a facturar
    private String nombreProducto;     // Nombre (solo para respuesta)
    private Integer cantidad;          // Cantidad de productos
    private BigDecimal precioUnitario; // Precio por unidad
    private BigDecimal subtotal;       // Se calcula automáticamente (cantidad × precio)
}