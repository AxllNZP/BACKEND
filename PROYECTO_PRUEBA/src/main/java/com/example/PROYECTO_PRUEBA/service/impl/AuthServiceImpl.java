package com.example.PROYECTO_PRUEBA.service.impl;

import com.example.PROYECTO_PRUEBA.config.security.JwtService;
import com.example.PROYECTO_PRUEBA.dto.AuthRequest;
import com.example.PROYECTO_PRUEBA.dto.AuthResponse;
import com.example.PROYECTO_PRUEBA.dto.RegisterRequest;
import com.example.PROYECTO_PRUEBA.model.EstadoGeneral;
import com.example.PROYECTO_PRUEBA.model.Usuario;
import com.example.PROYECTO_PRUEBA.repository.IUsuarioRepository;
import com.example.PROYECTO_PRUEBA.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * 🔥 REGISTRO: Ahora pasa el objeto Usuario completo al generar el token
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Crear y guardar el usuario
        var user = new Usuario();
        user.setNombreUsuario(request.getNombreUsuario());
        user.setClave(passwordEncoder.encode(request.getClave()));
        user.setNombreCompleto(request.getNombreCompleto());
        user.setEmail(request.getEmail());
        user.setRol(request.getRol());
        user.setEstado(EstadoGeneral.activo);

        // Guardar en BD (esto asigna el ID automáticamente)
        Usuario savedUser = repository.save(user);

        // 2. Crear UserDetails con el ROL correcto
        UserDetails userDetails = new User(
                savedUser.getNombreUsuario(),
                savedUser.getClave(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + savedUser.getRol().name())
                )
        );

        // 3. 🔑 CLAVE: Pasar el objeto Usuario completo para incluir el rol en el JWT
        var jwtToken = jwtService.generateToken(userDetails, savedUser);

        return AuthResponse.builder().token(jwtToken).build();
    }

    /**
     * 🔥 LOGIN: Ahora pasa el objeto Usuario completo al generar el token
     */
    public AuthResponse authenticate(AuthRequest request) {
        // 1. Autenticar al usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNombreUsuario(),
                        request.getClave()
                )
        );

        // 2. Obtener el usuario de la BD
        Usuario user = repository.busqusuario(request.getNombreUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Crear UserDetails con el ROL correcto
        UserDetails userDetails = new User(
                user.getNombreUsuario(),
                user.getClave(),
                Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRol().name())
                )
        );

        // 4. 🔑 CLAVE: Pasar el objeto Usuario completo para incluir el rol en el JWT
        var jwtToken = jwtService.generateToken(userDetails, user);

        return AuthResponse.builder().token(jwtToken).build();
    }
}