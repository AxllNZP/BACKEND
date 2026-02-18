package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para Moneda
 */
@Repository
public interface IMonedaRepository extends JpaRepository<Moneda, Long> {

    /**
     * Buscar moneda por código (PEN, USD, EUR, etc.)
     */
    @Query("SELECT m FROM Moneda m WHERE m.codigo = :codigo")
    Optional<Moneda> buscarPorCodigo(@Param("codigo") String codigo);

}