//C:\Users\AXELL\Desktop\Practicas\BACKEND\PROYECTO_PRUEBA\src\main\java\com\example\PROYECTO_PRUEBA\service\impl\ArchivoServiceImpl.java
package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.model.Archivo;
import com.example.PROYECTO_PRUEBA.repository.IArchivoRepository;
import com.example.PROYECTO_PRUEBA.service.IArchivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchivoServiceImpl implements IArchivoService {

    private final IArchivoRepository archivoRepository;

    @Override
    public Archivo guardarArchivo(MultipartFile file) throws IOException {
        Archivo archivo = Archivo.builder()
                .nombre(file.getOriginalFilename())
                .tipo(file.getContentType())
                .contenido(file.getBytes())
                .tamanio(file.getSize())
                .build();

        return archivoRepository.save(archivo);
    }

    @Override
    public Archivo obtenerArchivo(Long id) {
        return archivoRepository.findById(id).orElse(null);
    }

    @Override
    public List<Archivo> listarArchivos() {
        return archivoRepository.findAll();
    }

    @Override
    public boolean eliminarArchivo(Long id) {
        if (!archivoRepository.existsById(id)) {
            return false;
        }
        archivoRepository.deleteById(id);
        return true;
    }
}