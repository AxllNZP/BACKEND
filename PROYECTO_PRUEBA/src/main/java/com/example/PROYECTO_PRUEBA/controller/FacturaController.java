package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.dto.FacturaRequestDTO;
import com.example.PROYECTO_PRUEBA.dto.FacturaResponseDTO;
import com.example.PROYECTO_PRUEBA.model.Factura;
import com.example.PROYECTO_PRUEBA.service.IFacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 🎮 Controller de Facturas
 * Define los endpoints REST que usarás en Postman
 */
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Cambiado para ser consistente con tus otros controllers
public class FacturaController {

    private final IFacturaService facturaService;

    /**
     * 📝 POST /api/facturas
     * Crear una nueva factura
     *
     * En Postman:
     * - Método: POST
     * - URL: http://localhost:8080/api/facturas
     * - Body: JSON con los datos de la factura
     */
    @PostMapping
    public ResponseEntity<FacturaResponseDTO> crearFactura(@RequestBody FacturaRequestDTO requestDTO) {
        try {
            FacturaResponseDTO response = facturaService.crearFactura(requestDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } catch (RuntimeException e) {
            // Si hay error, lo lanzamos con un mensaje claro
            throw new RuntimeException("Error al crear factura: " + e.getMessage());
        }
    }

    /**
     * 🔍 GET /api/facturas/{id}
     * Obtener una factura por su ID
     *
     * En Postman:
     * - Método: GET
     * - URL: http://localhost:8080/api/facturas/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponseDTO> obtenerFacturaPorId(@PathVariable Long id) {
        try {
            FacturaResponseDTO response = facturaService.obtenerFacturaPorId(id);
            return ResponseEntity.ok(response); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    /**
     * 📋 GET /api/facturas
     * Listar todas las facturas
     *
     * En Postman:
     * - Método: GET
     * - URL: http://localhost:8080/api/facturas
     */
    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> listarTodasLasFacturas() {
        List<FacturaResponseDTO> facturas = facturaService.listarTodasLasFacturas();
        return ResponseEntity.ok(facturas); // 200 OK
    }

    /**
     * 👤 GET /api/facturas/cliente/{idCliente}
     * Listar facturas de un cliente específico
     *
     * En Postman:
     * - Método: GET
     * - URL: http://localhost:8080/api/facturas/cliente/1
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<FacturaResponseDTO>> listarFacturasPorCliente(@PathVariable Long idCliente) {
        List<FacturaResponseDTO> facturas = facturaService.listarFacturasPorCliente(idCliente);
        return ResponseEntity.ok(facturas);
    }

    /**
     * 👨‍💼 GET /api/facturas/usuario/{idUsuario}
     * Listar facturas de un usuario (vendedor) específico
     *
     * En Postman:
     * - Método: GET
     * - URL: http://localhost:8080/api/facturas/usuario/1
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<FacturaResponseDTO>> listarFacturasPorUsuario(@PathVariable Long idUsuario) {
        List<FacturaResponseDTO> facturas = facturaService.listarFacturasPorUsuario(idUsuario);
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {

        byte[] pdf = facturaService.generarPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @GetMapping("/{id}/excel")
    public ResponseEntity<byte[]> descargarExcel(@PathVariable Long id) {

        byte[] excel = facturaService.generarExcel(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }



    //REPORTE CLIENTES/////////////////////////////////////////
    @GetMapping("/cliente/{idCliente}/reporte")
    public ResponseEntity<List<Factura>> obtenerReporteFacturas(
            @PathVariable Long idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin
    ) {

        List<Factura> facturas = facturaService
                .obtenerReporteFacturasPorClienteYFecha(
                        idCliente,
                        fechaInicio,
                        fechaFin
                );

        return ResponseEntity.ok(facturas);
    }
    @GetMapping("/cliente/{idCliente}/reporte/pdf")
    public ResponseEntity<byte[]> descargarReportePdf(
            @PathVariable Long idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin
    ) {

        byte[] pdf = facturaService
                .generarReporteClientePdf(idCliente, fechaInicio, fechaFin);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_cliente.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


    @GetMapping("/cliente/{idCliente}/reporte/excel")
    public ResponseEntity<byte[]> descargarReporteExcel(
            @PathVariable Long idCliente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin
    ) {

        byte[] excel = facturaService
                .generarReporteClienteExcel(idCliente, fechaInicio, fechaFin);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte_cliente.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

}