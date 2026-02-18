package com.example.PROYECTO_PRUEBA.service;

import com.example.PROYECTO_PRUEBA.dto.AuthRequest;
import com.example.PROYECTO_PRUEBA.dto.AuthResponse;
import com.example.PROYECTO_PRUEBA.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse authenticate(AuthRequest request);
}
