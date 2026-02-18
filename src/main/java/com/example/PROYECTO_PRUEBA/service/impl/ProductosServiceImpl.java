package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Productos;
import com.example.PROYECTO_PRUEBA.model.Usuario;
import com.example.PROYECTO_PRUEBA.repository.IProductosRepository;
import com.example.PROYECTO_PRUEBA.service.IProductosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductosServiceImpl implements IProductosService {

    private final IProductosRepository iProductosRepository;

    @Override
    public List<Productos> listarProductos() {
        return iProductosRepository.findAll();
    }

    @Override
    public Optional<Productos> buscarPorId(Long id) {
        return iProductosRepository.findById(id);
    }


    @Override
    public List<Productos> buscarPorPrecioMenor(BigDecimal precio) {
        return iProductosRepository.busqpreciop(precio);
    }

    @Override
    public Optional<Productos> buscarPorNombre(String nombre) {
        return iProductosRepository.busqnombrep(nombre);
    }

    @Override
    public Optional<Productos> buscarPorStock(Integer stock) {
        return iProductosRepository.busqstockp(stock);
    }

    @Override
    public Productos guardar(Productos producto) {
        // .save() toma el objeto, lo inserta en la tabla y te devuelve
        // el objeto ya guardado (incluso con el ID generado por la DB)
        return iProductosRepository.save(producto);
    }

    @Override
    public Productos actualizarProducto(Long id, Productos producto) {
        Optional<Productos> productoExistente = iProductosRepository.findById(id);

        if (productoExistente.isPresent()) {
            Productos productoActualizado = productoExistente.get();

            productoActualizado.setCodigo(producto.getCodigo());
            productoActualizado.setNombre(producto.getNombre());
            productoActualizado.setDescripcion(producto.getDescripcion());
            productoActualizado.setPrecio(producto.getPrecio());
            productoActualizado.setStock(producto.getStock());
            productoActualizado.setEstado(producto.getEstado());

            return iProductosRepository.save(productoActualizado);
        }

        return null;
    }

    @Override
    public void eliminarproducto(Long id) {
        iProductosRepository.deleteById(id);
    }


}