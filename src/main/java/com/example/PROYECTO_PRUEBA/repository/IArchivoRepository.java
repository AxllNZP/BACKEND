package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IArchivoRepository extends JpaRepository<Archivo, Long> {
}