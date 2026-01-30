package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.Cliente;
import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Cliente
 */
@Repository
public interface IClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Buscar cliente por número de documento
     */
    @Query("SELECT c FROM Cliente c WHERE c.numeroDocumento = :numeroDocumento")
    Optional<Cliente> buscarPorNumeroDocumento(@Param("numeroDocumento") String numeroDocumento);

    /**
     * Buscar clientes por estado
     */
    @Query("SELECT c FROM Cliente c WHERE c.estado = :estado")
    List<Cliente> buscarPorEstado(@Param("estado") EstadoGeneral estado);

    /**
     * Buscar clientes por nombre (búsqueda parcial)
     */
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nombreRazonSocial) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Cliente> buscarPorNombre(@Param("nombre") String nombre);
}