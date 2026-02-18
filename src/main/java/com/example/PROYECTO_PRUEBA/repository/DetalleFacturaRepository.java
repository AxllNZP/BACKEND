package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.dto.UsuarioReporteDto;
import com.example.PROYECTO_PRUEBA.model.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {

    @Query("""
       SELECT d FROM DetalleFactura d
       WHERE d.factura.fechaEmision BETWEEN :fechaInicio AND :fechaFin
       AND (:idProducto IS NULL OR d.producto.idProducto = :idProducto)
       ORDER BY d.factura.fechaEmision ASC
       """)
    List<DetalleFactura> findProductosVendidosByFechaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("idProducto") Long idProducto
    );

    @Query("""
        SELECT new com.example.PROYECTO_PRUEBA.dto.UsuarioReporteDto(
            u.idUsuario,
            u.nombreUsuario,
            u.nombreCompleto,
            CAST(u.rol AS string),
            CAST(u.estado AS string),
            COUNT(f),
            COALESCE(SUM(f.subtotal), 0),
            COALESCE(SUM(f.igv), 0),
            COALESCE(SUM(f.total), 0)
        )
        FROM Factura f
        JOIN f.usuario u
        WHERE f.fechaEmision BETWEEN :fechaInicio AND :fechaFin
        AND (:usuarioId IS NULL OR u.idUsuario = :usuarioId)
        GROUP BY u.idUsuario, u.nombreUsuario, u.nombreCompleto, u.rol, u.estado
        ORDER BY u.nombreUsuario ASC
    """)
    List<UsuarioReporteDto> generarReporteUsuarios(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("usuarioId") Long usuarioId
    );

}
