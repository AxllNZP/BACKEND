// src/main/java/com/example/PROYECTO_PRUEBA/controller/ProductosController.java
package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.model.Productos;
import com.example.PROYECTO_PRUEBA.service.IProductosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // ← AGREGAR ESTA LÍNEA
public class ProductosController {

    private final IProductosService productosService;

    @GetMapping
    public ResponseEntity<List<Productos>> listar() {
        return ResponseEntity.ok(productosService.listarProductos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Productos> porId(@PathVariable Long id) {
        return productosService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/precio-menor/{precio}")
    public ResponseEntity<List<Productos>> buscarPorPrecioMenor(@PathVariable BigDecimal precio) {
        return ResponseEntity.ok(productosService.buscarPorPrecioMenor(precio));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Productos> buscarPorNombre(@PathVariable String nombre) {
        return productosService.buscarPorNombre(nombre).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stock/{stock}")
    public ResponseEntity<Optional<Productos>> buscarPorStock(@PathVariable Integer stock){
        return ResponseEntity.ok(productosService.buscarPorStock(stock));
    }

    @PostMapping
    public ResponseEntity<Productos> crearProducto(@RequestBody Productos producto) {
        Productos productoGuardado = productosService.guardar(producto);
        return ResponseEntity.status(201).body(productoGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Productos> actualizarProducto(@PathVariable Long id, @RequestBody Productos producto) {
        Productos productoActualizado = productosService.actualizarProducto(id, producto);

        if (productoActualizado != null) {
            return ResponseEntity.ok(productoActualizado);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Optional<Productos> producto = productosService.buscarPorId(id);

        if (producto.isPresent()) {
            productosService.eliminarproducto(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
}