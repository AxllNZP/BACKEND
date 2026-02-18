package com.example.PROYECTO_PRUEBA.controller;


import com.example.PROYECTO_PRUEBA.model.FormaPago;
import com.example.PROYECTO_PRUEBA.service.IFormaPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/formapago")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class FormaPagoController {

    private final IFormaPagoService iFormaPagoService;

    @GetMapping
    public ResponseEntity<List<FormaPago>> listarTodos() {
        return ResponseEntity.ok(iFormaPagoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaPago> buscarPorId(@PathVariable Long id) {
        return iFormaPagoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<FormaPago> crear(@RequestBody FormaPago formaPago) {
        FormaPago formguard = iFormaPagoService.guardar(formaPago);
        return ResponseEntity.status(201).body(formguard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormaPago> actualizar(@PathVariable Long id, @RequestBody FormaPago formaPago) {
        FormaPago formact = iFormaPagoService.actualizar(id, formaPago);

        if (formact != null) {
            return ResponseEntity.ok(formact);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Optional<FormaPago> cliente = iFormaPagoService.buscarPorId(id);

        if (cliente.isPresent()) {
            iFormaPagoService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
