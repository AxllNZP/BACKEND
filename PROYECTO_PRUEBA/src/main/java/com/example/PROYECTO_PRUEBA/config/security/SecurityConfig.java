package com.example.PROYECTO_PRUEBA.config;

import com.example.PROYECTO_PRUEBA.config.JwtAuthenticationFilter;
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
@EnableMethodSecurity // 🔥 ESTO HABILITA @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ⚠️ IMPORTANTE: Habilitar CORS ANTES de cualquier otra configuración
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Deshabilitar CSRF (no es necesario para APIs REST con JWT)
                .csrf(csrf -> csrf.disable())

                // Configurar autorización de endpoints
                .authorizeHttpRequests(auth -> auth
                        // 🔓 Endpoints públicos (sin autenticación)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🔒 SOLO ADMIN puede acceder a usuarios y monedas
                        .requestMatchers("/api/usuarios/**").hasRole("ROLE_admin")
                        .requestMatchers("/api/moneda/**").hasRole("ROLE_admin")

                        // ✅ Facturas, clientes y productos: cualquier usuario autenticado
                        .requestMatchers("/api/facturas/**").authenticated()
                        .requestMatchers("/api/clientes/**").authenticated()
                        .requestMatchers("/api/productos/**").authenticated()

                        // 🔐 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Configurar sesiones como STATELESS (sin sesiones, usando JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Agregar el proveedor de autenticación
                .authenticationProvider(authenticationProvider)

                // Agregar el filtro JWT antes del filtro de autenticación de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración de CORS para Spring Security
     * IMPORTANTE: Esta configuración debe coincidir con CorsConfig.java
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir peticiones desde Angular
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Permitir todos los headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Exponer el header Authorization
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Aplicar a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}