// src/main/java/com/example/PROYECTO_PRUEBA/service/IClienteService.java
package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.Cliente;
import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;

import java.util.List;
import java.util.Optional;

public interface IClienteService {
    List<Cliente> listarTodos();
    Optional<Cliente> buscarPorId(Long id);
    Optional<Cliente> buscarPorNumeroDocumento(String numeroDocumento);
    List<Cliente> buscarPorEstado(EstadoGeneral estado);
    List<Cliente> buscarPorNombre(String nombre);
    Cliente guardar(Cliente cliente);
    Cliente actualizar(Long id, Cliente cliente);
    void eliminar(Long id);
}