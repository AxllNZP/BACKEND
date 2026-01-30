package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.dto.*;
import com.example.PROYECTO_PRUEBA.model.*;
import com.example.PROYECTO_PRUEBA.repository.*;
import com.example.PROYECTO_PRUEBA.service.IFacturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de facturación
 * Aquí está TODA la lógica de negocio
 */
@Service
@RequiredArgsConstructor // Lombok genera constructor con los campos final
public class FacturaServiceImpl implements IFacturaService {

    // ===== INYECCIÓN DE DEPENDENCIAS =====
    // Todos los repositories que necesitamos
    private final IFacturaRepository facturaRepository;
    private final IClienteRepository clienteRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IProductosRepository productosRepository;
    private final IMonedaRepository monedaRepository;
    private final IFormasPagoRepository formaPagoRepository;

    /**
     * 🎯 MÉTODO PRINCIPAL: Crear una factura completa
     * @Transactional asegura que todo se guarde o nada (atomicidad)
     */
    @Override
    @Transactional
    public FacturaResponseDTO crearFactura(FacturaRequestDTO requestDTO) {

        // ===== PASO 1: VALIDAR QUE EXISTAN LAS ENTIDADES =====
        Cliente cliente = clienteRepository.findById(requestDTO.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + requestDTO.getIdCliente()));

        Usuario usuario = usuarioRepository.findById(requestDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + requestDTO.getIdUsuario()));

        Moneda moneda = monedaRepository.findById(requestDTO.getIdMoneda())
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + requestDTO.getIdMoneda()));

        // ===== PASO 2: CREAR LA FACTURA (CABECERA) =====
        Factura factura = new Factura();
        factura.setSerie(requestDTO.getSerie());
        factura.setNumero(generarNumeroFactura(requestDTO.getSerie())); // Generamos número automático
        factura.setFechaEmision(LocalDateTime.now());
        factura.setObservaciones(requestDTO.getObservaciones());
        factura.setCliente(cliente);
        factura.setUsuario(usuario);
        factura.setMoneda(moneda);

        // Inicializamos listas vacías
        factura.setDetalles(new ArrayList<>());
        factura.setPagos(new ArrayList<>());

        // ===== PASO 3: PROCESAR LOS DETALLES (PRODUCTOS) =====
        BigDecimal subtotalTotal = BigDecimal.ZERO;

        for (DetalleFacturaDTO detalleDTO : requestDTO.getDetalles()) {
            // Validar que el producto exista
            Productos producto = productosRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getIdProducto()));

            // Validar que haya stock suficiente
            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre()
                        + ". Stock disponible: " + producto.getStock()
                        + ", solicitado: " + detalleDTO.getCantidad());
            }

            // Crear el detalle
            DetalleFactura detalle = new DetalleFactura();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio()); // Tomamos el precio actual del producto
            detalle.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(detalleDTO.getCantidad())));
            detalle.setFactura(factura);

            // Agregar a la lista de detalles
            factura.getDetalles().add(detalle);

            // Acumular subtotal
            subtotalTotal = subtotalTotal.add(detalle.getSubtotal());

            // 🔥 IMPORTANTE: Reducir el stock del producto
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productosRepository.save(producto);
        }

        // ===== PASO 4: CALCULAR TOTALES =====
        // En Perú el IGV es 18%
        BigDecimal igv = subtotalTotal.multiply(new BigDecimal("0.18")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalTotal.add(igv);

        factura.setSubtotal(subtotalTotal);
        factura.setIgv(igv);
        factura.setTotal(total);

        // ===== PASO 5: PROCESAR LOS PAGOS (SI HAY) =====
        if (requestDTO.getPagos() != null && !requestDTO.getPagos().isEmpty()) {
            for (PagoDTO pagoDTO : requestDTO.getPagos()) {
                // Validar que exista la forma de pago
                FormaPago formaPago = formaPagoRepository.findById(pagoDTO.getIdFormaPago())
                        .orElseThrow(() -> new RuntimeException("Forma de pago no encontrada con ID: " + pagoDTO.getIdFormaPago()));

                // Validar que exista la moneda del pago
                Moneda monedaPago = monedaRepository.findById(pagoDTO.getIdMoneda())
                        .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + pagoDTO.getIdMoneda()));

                // Validar que exista el usuario que registra el pago
                Usuario usuarioPago = usuarioRepository.findById(pagoDTO.getIdUsuario())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + pagoDTO.getIdUsuario()));

                // Crear el pago
                Pago pago = new Pago();
                pago.setMonto(pagoDTO.getMonto());
                pago.setFechaPago(pagoDTO.getFechaPago() != null ? pagoDTO.getFechaPago() : LocalDateTime.now());
                pago.setNumeroOperacion(pagoDTO.getNumeroOperacion());
                pago.setFormaPago(formaPago);
                pago.setMoneda(monedaPago);
                pago.setUsuario(usuarioPago);
                pago.setFactura(factura);

                // Agregar a la lista de pagos
                factura.getPagos().add(pago);
            }
        }

        // ===== PASO 6: GUARDAR LA FACTURA (CON TODOS SUS DETALLES Y PAGOS) =====
        // Gracias a CascadeType.ALL, se guardan automáticamente los detalles y pagos
        Factura facturaGuardada = facturaRepository.save(factura);

        // ===== PASO 7: CONVERTIR A DTO Y RETORNAR =====
        return convertirAFacturaResponseDTO(facturaGuardada);
    }

    /**
     * 🔢 Generar número de factura automáticamente
     * Formato: 00000001, 00000002, etc.
     */
    private String generarNumeroFactura(String serie) {
        String ultimoNumero = facturaRepository.buscarMaximoNumeroPorSerie(serie);

        if (ultimoNumero == null) {
            // Si no hay facturas con esta serie, empezamos con 00000001
            return "00000001";
        }

        // Convertir a número, sumar 1, y volver a formatear
        int numero = Integer.parseInt(ultimoNumero) + 1;
        return String.format("%08d", numero); // 8 dígitos con ceros a la izquierda
    }

    /**
     * 📋 Obtener factura por ID
     */
    @Override
    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerFacturaPorId(Long idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + idFactura));

        return convertirAFacturaResponseDTO(factura);
    }

    /**
     * 📜 Listar todas las facturas
     */
    @Override
    @Transactional(readOnly = true)
    public List<FacturaResponseDTO> listarTodasLasFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        return facturas.stream()
                .map(this::convertirAFacturaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 👤 Listar facturas por cliente
     */
    @Override
    @Transactional(readOnly = true)
    public List<FacturaResponseDTO> listarFacturasPorCliente(Long idCliente) {
        List<Factura> facturas = facturaRepository.buscarPorCliente(idCliente);
        return facturas.stream()
                .map(this::convertirAFacturaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 👨‍💼 Listar facturas por usuario
     */
    @Override
    @Transactional(readOnly = true)
    public List<FacturaResponseDTO> listarFacturasPorUsuario(Long idUsuario) {
        List<Factura> facturas = facturaRepository.buscarPorUsuario(idUsuario);
        return facturas.stream()
                .map(this::convertirAFacturaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🔄 MÉTODO AUXILIAR: Convertir Entidad a DTO
     * Este método "traduce" la entidad JPA a un objeto JSON amigable
     */
    private FacturaResponseDTO convertirAFacturaResponseDTO(Factura factura) {
        // Convertir detalles
        List<DetalleFacturaDTO> detallesDTO = factura.getDetalles().stream()
                .map(detalle -> DetalleFacturaDTO.builder()
                        .idProducto(detalle.getProducto().getIdProducto())
                        .nombreProducto(detalle.getProducto().getNombre())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotal(detalle.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        // Convertir pagos
        List<PagoDTO> pagosDTO = factura.getPagos().stream()
                .map(pago -> PagoDTO.builder()
                        .idPago(pago.getIdPago())
                        .monto(pago.getMonto())
                        .fechaPago(pago.getFechaPago())
                        .numeroOperacion(pago.getNumeroOperacion())
                        .idFormaPago(pago.getFormaPago().getIdFormaPago())
                        .nombreFormaPago(pago.getFormaPago().getNombre().name())
                        .idMoneda(pago.getMoneda().getIdMoneda())
                        .codigoMoneda(pago.getMoneda().getCodigo())
                        .idUsuario(pago.getUsuario().getIdUsuario())
                        .nombreUsuario(pago.getUsuario().getNombreCompleto())
                        .build())
                .collect(Collectors.toList());

        // Calcular totales de pagos
        BigDecimal totalPagado = factura.getPagos().stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPendiente = factura.getTotal().subtract(totalPagado);

        // Construir el DTO de respuesta
        return FacturaResponseDTO.builder()
                .idFactura(factura.getIdFactura())
                .serie(factura.getSerie())
                .numero(factura.getNumero())
                .fechaEmision(factura.getFechaEmision())
                .observaciones(factura.getObservaciones())
                .subtotal(factura.getSubtotal())
                .igv(factura.getIgv())
                .total(factura.getTotal())
                .idCliente(factura.getCliente().getIdCliente())
                .nombreCliente(factura.getCliente().getNombreRazonSocial())
                .documentoCliente(factura.getCliente().getNumeroDocumento())
                .idUsuario(factura.getUsuario().getIdUsuario())
                .nombreUsuario(factura.getUsuario().getNombreCompleto())
                .idMoneda(factura.getMoneda().getIdMoneda())
                .codigoMoneda(factura.getMoneda().getCodigo())
                .simboloMoneda(factura.getMoneda().getSimbolo())
                .detalles(detallesDTO)
                .pagos(pagosDTO)
                .totalPagado(totalPagado)
                .saldoPendiente(saldoPendiente)
                .build();
    }
}