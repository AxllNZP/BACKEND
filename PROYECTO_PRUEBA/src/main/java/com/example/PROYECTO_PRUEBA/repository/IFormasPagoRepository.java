package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository para FormaPago
 */
@Repository
public interface IFormasPagoRepository extends JpaRepository<FormaPago, Long> {

}