package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.dto.UsuarioReporteDto;
import com.example.PROYECTO_PRUEBA.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operaciones de base de datos de Factura
 * JpaRepository nos da métodos automáticos: save, findById, findAll, delete, etc.
 */
@Repository
public interface IFacturaRepository extends JpaRepository<Factura, Long> {

    /**
     * Buscar facturas por cliente
     */
    @Query("SELECT f FROM Factura f WHERE f.cliente.idCliente = :idCliente")
    List<Factura> buscarPorCliente(@Param("idCliente") Long idCliente);

    /**
     * Buscar facturas por usuario (vendedor)
     */
    @Query("SELECT f FROM Factura f WHERE f.usuario.idUsuario = :idUsuario")
    List<Factura> buscarPorUsuario(@Param("idUsuario") Long idUsuario);


    /**
     *
     * Buscar el numero mas alto de factura para una serie
     */
    @Query("""
        SELECT MAX(f.numero)
        FROM Factura f
        WHERE f.serie = :serie
    """)
    String buscarMaximoNumeroPorSerie(@Param("serie") String serie);



    @Query("""
           SELECT f FROM Factura f
           WHERE f.cliente.idCliente = :idCliente
           AND f.fechaEmision BETWEEN :fechaInicio AND :fechaFin
           ORDER BY f.fechaEmision ASC
           """)
    List<Factura> findFacturasByClienteAndFechaBetween(
            @Param("idCliente") Long idCliente,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
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