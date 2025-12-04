package com.apicultor.apicutor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Configurar origens permitidas a partir de propriedade (suporta lista separada por vírgula)
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            for (String origin : allowedOrigins.split(",")) {
                String o = origin.trim();
                if (!o.isEmpty()) {
                    config.addAllowedOrigin(o);
                }
            }
        }
        
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
