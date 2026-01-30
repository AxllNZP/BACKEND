package com.example.PROYECTO_PRUEBA.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    private String nombreUsuario;
    private String clave;
}