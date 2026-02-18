package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.dto.*;
import com.example.PROYECTO_PRUEBA.model.*;
import com.example.PROYECTO_PRUEBA.repository.*;
import com.example.PROYECTO_PRUEBA.service.IFacturaService;

import com.lowagie.text.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// =======================
// OPENPDF (PDF)
// =======================
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

// =======================
// APACHE POI (EXCEL)
// =======================
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




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


                //Validar pago
                if (pagoDTO.getMonto() == null || pagoDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("El monto del pago debe ser mayor a cero");
                }

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

    @Override
    public byte[] generarPdf(Long idFactura) {

        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            // =========================
            // FUENTES
            // =========================
            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);


            // =========================
            // HEADER
            // =========================
            Paragraph titulo = new Paragraph("FACTURA", tituloFont);
            titulo.setAlignment(Element.ALIGN_RIGHT);
            document.add(titulo);

            document.add(new Paragraph("Serie: " + factura.getSerie() + "-" + factura.getNumero(), boldFont));
            document.add(new Paragraph("Fecha Emisión: " + factura.getFechaEmision().toLocalDate(), normalFont));
            document.add(new Paragraph(" "));

            // =========================
            // DATOS CLIENTE
            // =========================
            PdfPTable clienteTable = new PdfPTable(2);
            clienteTable.setWidthPercentage(100);
            clienteTable.setSpacingBefore(10);

            clienteTable.addCell(getCell("Cliente:", boldFont));
            clienteTable.addCell(getCell(factura.getCliente().getNombreRazonSocial(), normalFont));

            clienteTable.addCell(getCell("Documento:", boldFont));
            clienteTable.addCell(getCell(factura.getCliente().getNumeroDocumento(), normalFont));

            clienteTable.addCell(getCell("Atendido por:", boldFont));
            clienteTable.addCell(getCell(factura.getUsuario().getNombreUsuario(), normalFont));

            document.add(clienteTable);

            // =========================
            // TABLA DETALLES
            // =========================
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(20);
            table.setWidths(new float[]{4, 1, 2, 2});

            table.addCell(getHeaderCell("Producto"));
            table.addCell(getHeaderCell("Cant."));
            table.addCell(getHeaderCell("P. Unit"));
            table.addCell(getHeaderCell("Subtotal"));

            for (DetalleFactura det : factura.getDetalles()) {

                table.addCell(getCell(det.getProducto().getNombre(), normalFont));
                table.addCell(getCell(String.valueOf(det.getCantidad()), normalFont));
                table.addCell(getCell(factura.getMoneda().getSimbolo() + " " + det.getPrecioUnitario(), normalFont));
                table.addCell(getCell(factura.getMoneda().getSimbolo() + " " + det.getSubtotal(), normalFont));
            }

            document.add(table);

            // =========================
            // RESUMEN
            // =========================
            PdfPTable resumen = new PdfPTable(2);
            resumen.setWidthPercentage(40);
            resumen.setHorizontalAlignment(Element.ALIGN_RIGHT);
            resumen.setSpacingBefore(20);

            resumen.addCell(getCell("Subtotal:", boldFont));
            resumen.addCell(getCell(factura.getMoneda().getSimbolo() + " " + factura.getSubtotal(), normalFont));

            resumen.addCell(getCell("IGV:", boldFont));
            resumen.addCell(getCell(factura.getMoneda().getSimbolo() + " " + factura.getIgv(), normalFont));

            resumen.addCell(getCell("TOTAL:", boldFont));
            resumen.addCell(getCell(factura.getMoneda().getSimbolo() + " " + factura.getTotal(), boldFont));

            document.add(resumen);

            // =========================
            // PAGOS
            // =========================
            if (!factura.getPagos().isEmpty()) {

                Paragraph pagosTitle = new Paragraph("Pagos realizados", boldFont);
                pagosTitle.setSpacingBefore(20);
                document.add(pagosTitle);

                PdfPTable pagosTable = new PdfPTable(4);
                pagosTable.setWidthPercentage(100);
                pagosTable.setSpacingBefore(10);

                pagosTable.addCell(getHeaderCell("Fecha"));
                pagosTable.addCell(getHeaderCell("Forma Pago"));
                pagosTable.addCell(getHeaderCell("Operación"));
                pagosTable.addCell(getHeaderCell("Monto"));

                for (Pago p : factura.getPagos()) {
                    pagosTable.addCell(getCell(p.getFechaPago().toLocalDate().toString(), normalFont));
                    pagosTable.addCell(getCell(String.valueOf(p.getFormaPago().getNombre()), normalFont));
                    pagosTable.addCell(getCell(p.getNumeroOperacion(), normalFont));
                    pagosTable.addCell(getCell(factura.getMoneda().getSimbolo() + " " + p.getMonto(), normalFont));
                }

                document.add(pagosTable);
            }

            // =========================
            // SALDO
            // =========================
            BigDecimal totalPagado = factura.getPagos().stream()
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldoPendiente = factura.getTotal().subtract(totalPagado);


            Paragraph saldo = new Paragraph(
                    "Saldo Pendiente: " + factura.getMoneda().getSimbolo() + " " + saldoPendiente,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)
            );
            saldo.setSpacingBefore(20);
            saldo.setAlignment(Element.ALIGN_RIGHT);

            document.add(saldo);
            Paragraph pagado = new Paragraph(
                    "Total Pagado: " + factura.getMoneda().getSimbolo() + " " + totalPagado,
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            pagado.setAlignment(Element.ALIGN_RIGHT);
            document.add(pagado);


            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    private PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(8);
        return cell;
    }



    @Override
    public byte[] generarExcel(Long idFactura) {

        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Factura");

            int rowIndex = 0;

            // ==============================
            // DATOS GENERALES
            // ==============================

            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue("Factura:");
            row.createCell(1).setCellValue(factura.getSerie() + "-" + factura.getNumero());

            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue("Fecha:");
            row.createCell(1).setCellValue(factura.getFechaEmision().toString());

            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue("Cliente:");
            row.createCell(1).setCellValue(factura.getCliente().getNombreRazonSocial());

            row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue("Documento:");
            row.createCell(1).setCellValue(factura.getCliente().getNumeroDocumento());

            rowIndex++;

            // ==============================
            // DETALLE
            // ==============================

            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Producto");
            header.createCell(1).setCellValue("Cantidad");
            header.createCell(2).setCellValue("Precio Unitario");
            header.createCell(3).setCellValue("Subtotal");

            for (DetalleFactura det : factura.getDetalles()) {
                Row detailRow = sheet.createRow(rowIndex++);
                detailRow.createCell(0).setCellValue(det.getProducto().getNombre());
                detailRow.createCell(1).setCellValue(det.getCantidad());
                detailRow.createCell(2).setCellValue(det.getPrecioUnitario().doubleValue());
                detailRow.createCell(3).setCellValue(det.getSubtotal().doubleValue());
            }

            rowIndex++;

            // ==============================
            // TOTALES
            // ==============================

            row = sheet.createRow(rowIndex++);
            row.createCell(2).setCellValue("Subtotal:");
            row.createCell(3).setCellValue(factura.getSubtotal().doubleValue());

            row = sheet.createRow(rowIndex++);
            row.createCell(2).setCellValue("IGV:");
            row.createCell(3).setCellValue(factura.getIgv().doubleValue());

            row = sheet.createRow(rowIndex++);
            row.createCell(2).setCellValue("Total:");
            row.createCell(3).setCellValue(factura.getTotal().doubleValue());

            rowIndex++;

            // ==============================
            // PAGOS (SI EXISTEN)
            // ==============================

            if (factura.getPagos() != null && !factura.getPagos().isEmpty()) {

                row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue("Pagos:");

                Row pagosHeader = sheet.createRow(rowIndex++);
                pagosHeader.createCell(0).setCellValue("Fecha");
                pagosHeader.createCell(1).setCellValue("Forma Pago");
                pagosHeader.createCell(2).setCellValue("Operacion");
                pagosHeader.createCell(3).setCellValue("Monto");

                for (Pago p : factura.getPagos()) {
                    Row pagoRow = sheet.createRow(rowIndex++);
                    pagoRow.createCell(0).setCellValue(p.getFechaPago().toString());
                    pagoRow.createCell(1).setCellValue(String.valueOf(p.getFormaPago().getNombre()));
                    pagoRow.createCell(2).setCellValue(p.getNumeroOperacion());
                    pagoRow.createCell(3).setCellValue(p.getMonto().doubleValue());
                }
            }

            // ==============================
            // AUTO SIZE
            // ==============================

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 importante para ver el error real
            throw new RuntimeException("Error al generar Excel", e);
        }
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


    //REPORTES CLIENTES
    @Override
    public List<Factura> obtenerReporteFacturasPorClienteYFecha(
            Long idCliente,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    ) {

        if (idCliente == null) {
            throw new IllegalArgumentException("El idCliente no puede ser null");
        }

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser null");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fechaInicio no puede ser mayor a fechaFin");
        }

        return facturaRepository.findFacturasByClienteAndFechaBetween(
                idCliente,
                fechaInicio,
                fechaFin
        );
    }

    @Override
    public byte[] generarReporteClientePdf(Long idCliente,
                                           LocalDateTime fechaInicio,
                                           LocalDateTime fechaFin) {

        List<Factura> facturas =
                obtenerReporteFacturasPorClienteYFecha(idCliente, fechaInicio, fechaFin);

        if (facturas.isEmpty()) {
            throw new RuntimeException("No existen facturas para el rango seleccionado");
        }

        String nombreCliente = facturas.get(0)
                .getCliente()
                .getNombreRazonSocial();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);

            Paragraph titulo = new Paragraph("REPORTE DE FACTURAS POR CLIENTE", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph("Cliente: " + nombreCliente, boldFont));
            document.add(new Paragraph("Desde: " + fechaInicio.toLocalDate()
                    + "  Hasta: " + fechaFin.toLocalDate(), normalFont));
            document.add(new Paragraph(" "));

            BigDecimal totalGeneral = BigDecimal.ZERO;

            for (Factura factura : facturas) {

                Paragraph facturaTitulo = new Paragraph(
                        "Factura: " + factura.getSerie() + "-" + factura.getNumero()
                                + " | Fecha: " + factura.getFechaEmision().toLocalDate(),
                        boldFont);
                facturaTitulo.setSpacingBefore(10);
                document.add(facturaTitulo);

                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                table.addCell("Producto");
                table.addCell("Cantidad");
                table.addCell("Precio Unit.");
                table.addCell("Subtotal");
                table.addCell("Total Factura");

                for (DetalleFactura detalle : factura.getDetalles()) {

                    table.addCell(detalle.getProducto().getNombre());
                    table.addCell(detalle.getCantidad().toString());
                    table.addCell(detalle.getPrecioUnitario().toString());
                    table.addCell(detalle.getSubtotal().toString());
                    table.addCell(factura.getTotal().toString());
                }

                document.add(table);
                totalGeneral = totalGeneral.add(factura.getTotal());
            }

            Paragraph total = new Paragraph(
                    "TOTAL GENERAL: " + totalGeneral,
                    boldFont
            );
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(20);

            document.add(total);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de reporte", e);
        }
    }


    @Override
    public byte[] generarReporteClienteExcel(Long idCliente,
                                             LocalDateTime fechaInicio,
                                             LocalDateTime fechaFin) {

        List<Factura> facturas =
                obtenerReporteFacturasPorClienteYFecha(idCliente, fechaInicio, fechaFin);

        if (facturas.isEmpty()) {
            throw new RuntimeException("No existen facturas para el rango seleccionado");
        }

        String nombreCliente = facturas.get(0)
                .getCliente()
                .getNombreRazonSocial();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte Cliente");

            int rowIndex = 0;

            // Título
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue("REPORTE DE FACTURAS");
            rowIndex++;

            Row clienteRow = sheet.createRow(rowIndex++);
            clienteRow.createCell(0).setCellValue("Cliente:");
            clienteRow.createCell(1).setCellValue(nombreCliente);

            Row fechaRow = sheet.createRow(rowIndex++);
            fechaRow.createCell(0).setCellValue("Desde:");
            fechaRow.createCell(1).setCellValue(fechaInicio.toLocalDate().toString());
            fechaRow.createCell(2).setCellValue("Hasta:");
            fechaRow.createCell(3).setCellValue(fechaFin.toLocalDate().toString());

            rowIndex++;

            // Header
            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Factura");
            header.createCell(1).setCellValue("Fecha");
            header.createCell(2).setCellValue("Producto");
            header.createCell(3).setCellValue("Cantidad");
            header.createCell(4).setCellValue("Precio Unit.");
            header.createCell(5).setCellValue("Subtotal");
            header.createCell(6).setCellValue("Total Factura");

            BigDecimal totalGeneral = BigDecimal.ZERO;

            for (Factura factura : facturas) {

                for (DetalleFactura detalle : factura.getDetalles()) {

                    Row row = sheet.createRow(rowIndex++);

                    row.createCell(0).setCellValue(
                            factura.getSerie() + "-" + factura.getNumero());

                    row.createCell(1).setCellValue(
                            factura.getFechaEmision().toLocalDate().toString());

                    row.createCell(2).setCellValue(
                            detalle.getProducto().getNombre());

                    row.createCell(3).setCellValue(
                            detalle.getCantidad().doubleValue());

                    row.createCell(4).setCellValue(
                            detalle.getPrecioUnitario().doubleValue());

                    row.createCell(5).setCellValue(
                            detalle.getSubtotal().doubleValue());

                    row.createCell(6).setCellValue(
                            factura.getTotal().doubleValue());
                }

                totalGeneral = totalGeneral.add(factura.getTotal());
            }

            Row totalRow = sheet.createRow(rowIndex++);
            totalRow.createCell(5).setCellValue("TOTAL GENERAL:");
            totalRow.createCell(6).setCellValue(totalGeneral.doubleValue());

            for (int i = 0; i <= 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de reporte", e);
        }
    }


}