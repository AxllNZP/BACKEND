package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.Cliente;
import com.example.PROYECTO_PRUEBA.model.Moneda;

import java.util.List;
import java.util.Optional;

public interface IMonedaService {

    List<Moneda> listarTodos();
    Optional<Moneda> buscarPorId(Long id);
    Optional<Moneda> buscarPorCodigo(String codigo);
    Moneda guardar(Moneda moneda);
    Moneda actualizar(Long id, Moneda moneda);
    void eliminar(Long id);
}
