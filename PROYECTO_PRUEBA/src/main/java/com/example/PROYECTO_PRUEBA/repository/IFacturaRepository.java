package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}