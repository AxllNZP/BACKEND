package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Productos
 */
@Repository
public interface IProductosRepository extends JpaRepository<Productos, Long> {

    /**
     * Buscar productos por estado
     */
    @Query("SELECT p FROM Productos p WHERE p.estado = :estado")
    List<Productos> busqestadop(@Param("estado") EstadoGeneral estado);

    /**
     * Buscar productos con precio menor al especificado
     */
    @Query("SELECT p FROM Productos p WHERE p.precio < :precio")
    List<Productos> busqpreciop(@Param("precio") BigDecimal precio);

    /**
     * Buscar producto por nombre
     */
    @Query("SELECT p FROM Productos p WHERE LOWER(p.nombre) = LOWER(:nombre)")
    Optional<Productos> busqnombrep(@Param("nombre") String nombre);

    /**
     * Buscar producto por stock
     */
    @Query("SELECT p FROM Productos p WHERE p.stock = :stock")
    Optional<Productos> busqstockp(@Param("stock") Integer stock);
}