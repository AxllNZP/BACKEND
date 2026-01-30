package com.example.PROYECTO_PRUEBA.controller;

import com.example.PROYECTO_PRUEBA.dto.AuthRequest;
import com.example.PROYECTO_PRUEBA.dto.AuthResponse;
import com.example.PROYECTO_PRUEBA.dto.RegisterRequest;
import com.example.PROYECTO_PRUEBA.model.Usuario;
import com.example.PROYECTO_PRUEBA.repository.IUsuarioRepository;
import com.example.PROYECTO_PRUEBA.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl service;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }


}