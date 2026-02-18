package com.example.PROYECTO_PRUEBA.controller;


import com.example.PROYECTO_PRUEBA.model.Cliente;
import com.example.PROYECTO_PRUEBA.model.Moneda;
import com.example.PROYECTO_PRUEBA.service.IMonedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/moneda")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MonedaController {

    private final IMonedaService iMonedaService;

    @GetMapping
    public ResponseEntity<List<Moneda>> listarTodos() {
        return ResponseEntity.ok(iMonedaService.listarTodos());
    }

    // ✅ Ruta específica para código
    @GetMapping("/codigo/{codigo}")  // ← CAMBIO AQUÍ
    public ResponseEntity<Moneda> buscarPorCodigo(@PathVariable String codigo){
        return iMonedaService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Ruta específica para ID
    @GetMapping("/id/{id}")  // ← CAMBIO AQUÍ
    public ResponseEntity<Moneda> buscarPorId(@PathVariable Long id) {
        return iMonedaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Moneda> crear(@RequestBody Moneda moneda) {
        Moneda monedaguardada = iMonedaService.guardar(moneda);
        return ResponseEntity.status(201).body(monedaguardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Moneda> actualizar(@PathVariable Long id, @RequestBody Moneda moneda) {
        Moneda monedaact = iMonedaService.actualizar(id, moneda);

        if (monedaact != null) {
            return ResponseEntity.ok(monedaact);
        }

        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Optional<Moneda> moneda = iMonedaService.buscarPorId(id);

        if (moneda.isPresent()) {
            iMonedaService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }


}
