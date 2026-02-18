// src/main/java/com/example/PROYECTO_PRUEBA/config/CorsConfig.java
package com.example.PROYECTO_PRUEBA.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Permitir peticiones desde Angular (localhost:4200)
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

        // Permitir todos los métodos HTTP (GET, POST, PUT, DELETE, etc.)
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Permitir todos los headers
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        // Permitir cookies y credenciales
        corsConfiguration.setAllowCredentials(true);

        // Aplicar la configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}