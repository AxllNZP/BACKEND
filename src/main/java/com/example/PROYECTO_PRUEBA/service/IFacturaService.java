package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.dto.FacturaRequestDTO;
import com.example.PROYECTO_PRUEBA.dto.FacturaResponseDTO;
import com.example.PROYECTO_PRUEBA.model.Factura;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio de facturación
 * Define qué operaciones se pueden hacer con las facturas
 */
public interface IFacturaService {

    /**
     * Crear una nueva factura
     * @param requestDTO Datos de la factura a crear
     * @return Factura creada con todos sus datos
     */
    FacturaResponseDTO crearFactura(FacturaRequestDTO requestDTO);

    /**
     * Obtener una factura por su ID
     * @param idFactura ID de la factura
     * @return Datos de la factura
     */
    FacturaResponseDTO obtenerFacturaPorId(Long idFactura);

    /**
     * Listar todas las facturas
     * @return Lista de todas las facturas
     */
    List<FacturaResponseDTO> listarTodasLasFacturas();

    /**
     * Listar facturas por cliente
     * @param idCliente ID del cliente
     * @return Lista de facturas del cliente
     */
    List<FacturaResponseDTO> listarFacturasPorCliente(Long idCliente);

    /**
     * Listar facturas por usuario (vendedor)
     * @param idUsuario ID del usuario
     * @return Lista de facturas del vendedor
     */
    List<FacturaResponseDTO> listarFacturasPorUsuario(Long idUsuario);


   // REPORTE FACTURA PDF/EXCEL
    byte[] generarPdf(Long idFactura);
    byte[] generarExcel(Long idFactura);



    //REPOTES CLIENTES
    List<Factura> obtenerReporteFacturasPorClienteYFecha(
            Long idCliente,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
    byte[] generarReporteClientePdf(Long idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    byte[] generarReporteClienteExcel(Long idCliente, LocalDateTime fechaInicio, LocalDateTime fechaFin);

}