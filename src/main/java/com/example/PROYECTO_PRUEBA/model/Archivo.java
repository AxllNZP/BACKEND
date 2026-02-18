//C:\Users\AXELL\Desktop\Practicas\BACKEND\PROYECTO_PRUEBA\src\main\java\com\example\PROYECTO_PRUEBA\model\Archivo.java

package com.example.PROYECTO_PRUEBA.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "archivos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Archivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo; // application/pdf

    @Lob //Indica que el contenido es grande.
    @Column(nullable = false)
    private byte[] contenido;

    @Column(nullable = false)
    private Long tamanio;
}