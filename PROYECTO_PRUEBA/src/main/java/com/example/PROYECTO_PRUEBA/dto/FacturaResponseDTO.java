package com.example.PROYECTO_PRUEBA.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para DEVOLVER los datos de una factura
 * Este es el JSON que recibirás como respuesta
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaResponseDTO {

    // ===== DATOS BÁSICOS =====
    private Long idFactura;
    private String serie;
    private String numero;             // Se genera automáticamente
    private LocalDateTime fechaEmision;
    private String observaciones;

    // ===== MONTOS =====
    private BigDecimal subtotal;       // Suma de subtotales de detalles
    private BigDecimal igv;            // 18% del subtotal
    private BigDecimal total;          // subtotal + igv

    // ===== INFORMACIÓN DE CLIENTE =====
    private Long idCliente;
    private String nombreCliente;
    private String documentoCliente;

    // ===== INFORMACIÓN DE USUARIO =====
    private Long idUsuario;
    private String nombreUsuario;

    // ===== INFORMACIÓN DE MONEDA =====
    private Long idMoneda;
    private String codigoMoneda;       // PEN, USD, etc.
    private String simboloMoneda;      // S/, $, etc.

    // ===== DETALLES Y PAGOS =====
    private List<DetalleFacturaDTO> detalles;
    private List<PagoDTO> pagos;

    // ===== INFORMACIÓN ADICIONAL =====
    private BigDecimal totalPagado;    // Suma de todos los pagos
    private BigDecimal saldoPendiente; // total - totalPagado
}