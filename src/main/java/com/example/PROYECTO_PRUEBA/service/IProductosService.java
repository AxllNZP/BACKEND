package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Productos; // Cambiado de Usuario a Productos
import com.example.PROYECTO_PRUEBA.model.Usuario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductosService {

    //get
    List<Productos> listarProductos();
    Optional<Productos> buscarPorId(Long id);
    List<Productos> buscarPorPrecioMenor(BigDecimal precio);
    Optional<Productos> buscarPorNombre(String nombre);
    Optional<Productos> buscarPorStock(Integer stock);


    //POST
    Productos guardar(Productos producto);

    //PUT
    Productos actualizarProducto(Long id, Productos producto);

    //DELETE
    void eliminarproducto(Long id);
}