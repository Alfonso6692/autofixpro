package com.example.autofixpro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.autofixpro.dao")
@EnableTransactionManagement
public class DatabaseConfig {
    // Configuración adicional de base de datos si es necesaria
}