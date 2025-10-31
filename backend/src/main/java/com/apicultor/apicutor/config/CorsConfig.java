package com.apicultor.apicutor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir origens específicas
        config.addAllowedOrigin("http://localhost:4200");
        
        // Permitir todos os cabeçalhos
        config.addAllowedHeader("*");
        
        // Permitir todos os métodos (GET, POST, PUT, DELETE, etc.)
        config.addAllowedMethod("*");
        
        // Permitir credenciais
        config.setAllowCredentials(true);
        
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}