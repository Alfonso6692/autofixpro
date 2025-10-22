package com.example.autofixpro.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            logger.info("🗄️  Verificando estructura de base de datos MySQL...");

            // Verificar si la base de datos tiene tablas
            String checkTablesQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'autofixpro'";
            Integer tableCount = jdbcTemplate.queryForObject(checkTablesQuery, Integer.class);

            logger.info("📊 Encontradas {} tablas en la base de datos", tableCount);

            if (tableCount == 0) {
                logger.info("🔧 Base de datos vacía. Hibernate creará las tablas automáticamente...");
            } else {
                logger.info("✅ Base de datos MySQL inicializada correctamente con {} tablas", tableCount);
            }

            // Verificar conectividad básica
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            logger.info("🐬 Conectado a MySQL versión: {}", version);

            // Verificar si hay datos de ejemplo
            try {
                String countClientesQuery = "SELECT COUNT(*) FROM clientes";
                Integer clienteCount = jdbcTemplate.queryForObject(countClientesQuery, Integer.class);
                logger.info("👥 Clientes registrados: {}", clienteCount);

                if (clienteCount == 0) {
                    logger.info("📝 Datos de prueba serán cargados automáticamente por data.sql");
                }
            } catch (Exception e) {
                logger.debug("🔄 Las tablas serán creadas por Hibernate en el primer inicio");
            }

        } catch (Exception e) {
            logger.error("❌ Error inicializando base de datos MySQL: {}", e.getMessage());
            logger.error("🔍 Verificar credenciales y conectividad a: prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com");
        }
    }
}