package com.example.PROYECTO_PRUEBA.repository;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.RolUsuario;
import com.example.PROYECTO_PRUEBA.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Usuario
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {


    /**
     * Buscar por estado
     */
    @Query("SELECT u FROM Usuario u WHERE u.estado = :estado")
    List<Usuario> busqestado(@Param("estado") EstadoGeneral estado);

    /**
     * Buscar por rol
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol")
    List<Usuario> busqrol(@Param("rol") RolUsuario rol);

    /**
     * Buscar por nombre de usuario
     */
    @Query("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario")
    Optional<Usuario> busqusuario(@Param("nombreUsuario") String nombreUsuario);

    /**
     * Buscar por email
     */
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> busqemail(@Param("email") String email);
}