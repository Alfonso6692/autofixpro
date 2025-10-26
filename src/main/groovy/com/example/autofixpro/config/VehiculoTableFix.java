package com.example.autofixpro.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente para corregir la tabla vehiculos eliminando la columna 'year' duplicada.
 * La tabla debe tener solo la columna 'año'.
 */
@Component
public class VehiculoTableFix implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(VehiculoTableFix.class);
    private final JdbcTemplate jdbcTemplate;

    public VehiculoTableFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("🔧 Verificando estructura de tabla vehiculos...");

            // Verificar si existe la columna 'year'
            String checkColumnQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'autofixpro' " +
                    "AND TABLE_NAME = 'vehiculos' " +
                    "AND COLUMN_NAME = 'year'";

            Integer yearColumnExists = jdbcTemplate.queryForObject(checkColumnQuery, Integer.class);

            if (yearColumnExists != null && yearColumnExists > 0) {
                log.warn("⚠️  Se encontró columna 'year' duplicada en tabla vehiculos");
                log.info("🗑️  Eliminando columna 'year'...");

                try {
                    jdbcTemplate.execute("ALTER TABLE vehiculos DROP COLUMN `year`");
                    log.info("✅ Columna 'year' eliminada exitosamente");
                } catch (Exception e) {
                    log.error("❌ Error al eliminar columna 'year': {}", e.getMessage());
                }
            } else {
                log.info("✅ Estructura de tabla vehiculos correcta (solo columna 'año')");
            }

            // Verificar que existe la columna 'año'
            String checkAñoQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'autofixpro' " +
                    "AND TABLE_NAME = 'vehiculos' " +
                    "AND COLUMN_NAME = 'año'";

            Integer añoColumnExists = jdbcTemplate.queryForObject(checkAñoQuery, Integer.class);

            if (añoColumnExists == null || añoColumnExists == 0) {
                log.warn("⚠️  No se encontró columna 'año' en tabla vehiculos");
                log.info("➕ Creando columna 'año'...");

                try {
                    jdbcTemplate.execute("ALTER TABLE vehiculos ADD COLUMN `año` VARCHAR(4) NOT NULL");
                    log.info("✅ Columna 'año' creada exitosamente");
                } catch (Exception e) {
                    log.error("❌ Error al crear columna 'año': {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("❌ Error al verificar/corregir tabla vehiculos: {}", e.getMessage(), e);
        }
    }
}
