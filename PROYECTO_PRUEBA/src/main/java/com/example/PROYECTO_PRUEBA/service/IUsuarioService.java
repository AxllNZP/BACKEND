package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Productos;
import com.example.PROYECTO_PRUEBA.model.RolUsuario;
import com.example.PROYECTO_PRUEBA.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioService {
    List<Usuario> listarTodos();
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarPorEstado(EstadoGeneral estado);
    List<Usuario> listarPorRol(RolUsuario rol);
    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);
    Optional<Usuario> buscarPorEmail(String email);

    //post
    Usuario guardarusuario(Usuario usuario);


    //PUT
    Usuario actualizarusuario(Long id, Usuario usuario);

    //DELETE
    void eliminarusuario(long id);
}