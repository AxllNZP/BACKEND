//C:\Users\AXELL\Desktop\Practicas\BACKEND\PROYECTO_PRUEBA\src\main\java\com\example\PROYECTO_PRUEBA\controller\ArchivoController.java
package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.model.Archivo;
import com.example.PROYECTO_PRUEBA.service.IArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/archivos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ArchivoController {

    private final IArchivoService archivoService;

    // 🔼 SUBIR ARCHIVO
    @PostMapping("/subir")
    public Archivo subirArchivo(@RequestParam("file") MultipartFile file) throws IOException {
        return archivoService.guardarArchivo(file);
    }

    // 📄 LISTAR ARCHIVOS
    @GetMapping
    public List<Archivo> listarArchivos() {
        return archivoService.listarArchivos()
                .stream()
                .map(a -> Archivo.builder()
                        .id(a.getId())
                        .nombre(a.getNombre())
                        .tipo(a.getTipo())
                        .tamanio(a.getTamanio())
                        .build()
                )
                .toList();
    }

    // ⬇️ DESCARGAR ARCHIVO
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Long id) {
        Archivo archivo = archivoService.obtenerArchivo(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + archivo.getNombre() + "\"")
                .contentType(MediaType.parseMediaType(archivo.getTipo()))
                .body(archivo.getContenido());
    }
    // ❌ BORRAR ARCHIVO
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarArchivo(@PathVariable Long id) {
        boolean eliminado = archivoService.eliminarArchivo(id);
        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build(); // 204
    }

}