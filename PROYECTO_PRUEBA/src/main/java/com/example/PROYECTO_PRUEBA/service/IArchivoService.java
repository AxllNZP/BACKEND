//C:\Users\AXELL\Desktop\Practicas\BACKEND\PROYECTO_PRUEBA\src\main\java\com\example\PROYECTO_PRUEBA\service\IArchivoService.java
package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.model.Archivo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IArchivoService {

    Archivo guardarArchivo(MultipartFile file) throws IOException;

    Archivo obtenerArchivo(Long id);

    List<Archivo> listarArchivos();

    /**
     * Elimina el archivo por id.
     * @param id id del archivo
     * @return true si se eliminó, false si no existía
     */
    boolean eliminarArchivo(Long id);
}