package com.example.PROYECTO_PRUEBA.dto;

import com.example.PROYECTO_PRUEBA.model.RolUsuario;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String nombreUsuario;
    private String clave;
    private String nombreCompleto;
    private String email;
    private RolUsuario rol;
}