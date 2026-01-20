package com.mateandgit.candestore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        // CORS is handled by SecurityConfig's CorsConfigurationSource
        // This method is kept for compatibility but actual CORS configuration
        // is managed through SecurityConfig to support environment-based origins
    }

}