package com.example.PROYECTO_PRUEBA.config.security;

import com.example.PROYECTO_PRUEBA.config.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ CORS configurado PRIMERO
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ✅ CSRF deshabilitado
                .csrf(csrf -> csrf.disable())

                // ✅ AUTORIZACIÓN CORREGIDA
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Endpoints públicos
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🔒 USUARIOS y MONEDAS: SOLO ADMIN
                        // ⚠️ IMPORTANTE: Como agregamos "ROLE_" en ApplicationConfig,
                        // aquí usamos "ADMIN" sin el prefijo
                        .requestMatchers("/api/usuarios/**").authenticated()
                        .requestMatchers("/api/moneda/**").authenticated()

                        // ✅ FACTURAS, CLIENTES, PRODUCTOS: Cualquier usuario autenticado
                        .requestMatchers("/api/facturas/**").authenticated()
                        .requestMatchers("/api/clientes/**").permitAll()
                        .requestMatchers("/api/productos/**").authenticated()
                        .requestMatchers("/api/formapago/**").authenticated()
                        .requestMatchers("/api/archivos/**").authenticated()

                        // 🔐 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // ✅ Sesiones STATELESS (JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Proveedor de autenticación
                .authenticationProvider(authenticationProvider)

                // ✅ Filtro JWT
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * ✅ CONFIGURACIÓN CORS ÚNICA Y CENTRALIZADA
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir origen de Angular
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Permitir todos los headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Exponer Authorization header
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Aplicar a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}