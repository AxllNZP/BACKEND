package com.example.PROYECTO_PRUEBA.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para registrar pagos
 * Una factura puede tener múltiples pagos (pagos parciales)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoDTO {

    private Long idPago;               // Solo para respuesta
    private BigDecimal monto;          // Monto del pago
    private LocalDateTime fechaPago;   // Se genera automáticamente si no se envía
    private String numeroOperacion;    // Número de operación bancaria (opcional)
    private Long idFormaPago;          // ID de la forma de pago (efectivo, tarjeta, etc.)
    private String nombreFormaPago;    // Nombre (solo para respuesta)
    private Long idMoneda;             // ID de la moneda del pago
    private String codigoMoneda;       // Código (solo para respuesta, ej: "PEN", "USD")
    private Long idUsuario;            // ID del usuario que registra el pago
    private String nombreUsuario;      // Nombre (solo para respuesta)
}