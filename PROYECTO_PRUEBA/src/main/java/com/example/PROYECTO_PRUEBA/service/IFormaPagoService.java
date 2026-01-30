package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.FormaPago;
import com.example.PROYECTO_PRUEBA.model.FormasPagos;

import java.util.List;
import java.util.Optional;

public interface IFormaPagoService {
    List<FormaPago> listarTodos();
    Optional<FormaPago> buscarPorId(Long id);
    FormaPago guardar(FormaPago formaPago);
    FormaPago actualizar(Long id, FormaPago formaPago);
    void eliminar(Long id);
}
