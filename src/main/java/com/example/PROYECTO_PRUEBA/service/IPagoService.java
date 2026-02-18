package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.Pago;

import java.util.List;
import java.util.Optional;

public interface IPagoService {
    // Crear un pago (requiere formaPagoId y facturaId)
    Pago crearPago(Long formaPagoId, Long facturaId, Pago pago);

    // Obtener todos los pagos
    List<Pago> obtenerTodos();

    // Obtener un pago por ID
    Optional<Pago> obtenerPorId(Long id);

    // Obtener pagos de una factura
    List<Pago> obtenerPagosPorFactura(Long facturaId);

    // Obtener pagos realizados con una forma de pago específica
    List<Pago> obtenerPagosPorFormaPago(Long formaPagoId);

    // Obtener pagos que usen cierta forma de pago en una factura específica
    List<Pago> obtenerPagosPorFacturaYFormaPago(Long facturaId, Long formaPagoId);

    // Actualizar un pago
    Pago actualizarPago(Long id, Pago pago);

    // Eliminar un pago
    void eliminarPago(Long id);

}
