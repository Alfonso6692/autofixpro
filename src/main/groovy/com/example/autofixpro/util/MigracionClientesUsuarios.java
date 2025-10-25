package com.example.autofixpro.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Utilidad para migrar clientes existentes y asociarlos con usuarios.
 * Esta clase se ejecuta automáticamente al iniciar la aplicación una sola vez.
 */
@Component
@Order(100) // Ejecutar después de DataLoader
public class MigracionClientesUsuarios implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private boolean migrationExecuted = false;

    @Override
    public void run(String... args) throws Exception {
        if (migrationExecuted) {
            return;
        }

        System.out.println("=============================================================");
        System.out.println("🔄 INICIANDO MIGRACIÓN: Clientes ↔️ Usuarios");
        System.out.println("=============================================================");

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // PASO 1: Verificación inicial
            System.out.println("\n📊 PASO 1: Verificación inicial");
            System.out.println("-------------------------------------------------------------");
            ResultSet rs1 = stmt.executeQuery(
                "SELECT " +
                "(SELECT COUNT(*) FROM usuarios WHERE role = 'USER') AS usuarios_tipo_user, " +
                "(SELECT COUNT(*) FROM clientes) AS total_clientes, " +
                "(SELECT COUNT(*) FROM clientes WHERE usuario_id IS NOT NULL) AS clientes_con_usuario, " +
                "(SELECT COUNT(*) FROM clientes WHERE usuario_id IS NULL) AS clientes_sin_usuario"
            );
            if (rs1.next()) {
                System.out.println("   Usuarios tipo USER: " + rs1.getInt("usuarios_tipo_user"));
                System.out.println("   Total clientes: " + rs1.getInt("total_clientes"));
                System.out.println("   Clientes con usuario: " + rs1.getInt("clientes_con_usuario"));
                System.out.println("   Clientes sin usuario: " + rs1.getInt("clientes_sin_usuario"));
            }
            rs1.close();

            // PASO 2: Mostrar coincidencias
            System.out.println("\n🔍 PASO 2: Buscando coincidencias por email");
            System.out.println("-------------------------------------------------------------");
            ResultSet rs2 = stmt.executeQuery(
                "SELECT c.cliente_id, c.nombres, c.apellidos, c.email AS cliente_email, " +
                "u.id AS usuario_id, u.username, u.email AS usuario_email " +
                "FROM clientes c " +
                "INNER JOIN usuarios u ON c.email = u.email " +
                "WHERE u.role = 'USER' AND c.usuario_id IS NULL"
            );
            int coincidencias = 0;
            while (rs2.next()) {
                coincidencias++;
                System.out.println("   ✓ Cliente: " + rs2.getString("nombres") + " " + rs2.getString("apellidos") +
                                   " (" + rs2.getString("cliente_email") + ") " +
                                   "→ Usuario: " + rs2.getString("username"));
            }
            System.out.println("   Total coincidencias encontradas: " + coincidencias);
            rs2.close();

            // PASO 3: Actualizar clientes con coincidencia por email
            System.out.println("\n🔗 PASO 3: Asociando clientes con usuarios");
            System.out.println("-------------------------------------------------------------");
            int updated = stmt.executeUpdate(
                "UPDATE clientes c " +
                "INNER JOIN usuarios u ON c.email = u.email " +
                "SET c.usuario_id = u.id " +
                "WHERE u.role = 'USER' AND c.usuario_id IS NULL"
            );
            System.out.println("   ✅ Clientes asociados: " + updated);

            // PASO 4: Crear clientes para usuarios sin cliente
            System.out.println("\n➕ PASO 4: Creando clientes para usuarios sin cliente");
            System.out.println("-------------------------------------------------------------");
            int inserted = stmt.executeUpdate(
                "INSERT INTO clientes (nombres, apellidos, dni, telefono, email, usuario_id) " +
                "SELECT " +
                "  SUBSTRING_INDEX(u.nombre, ' ', 1) AS nombres, " +
                "  SUBSTRING(u.nombre, LENGTH(SUBSTRING_INDEX(u.nombre, ' ', 1)) + 2) AS apellidos, " +
                "  '' AS dni, " +
                "  IFNULL(u.telefono, '') AS telefono, " +
                "  u.email, " +
                "  u.id AS usuario_id " +
                "FROM usuarios u " +
                "LEFT JOIN clientes c ON c.usuario_id = u.id " +
                "WHERE u.role = 'USER' AND c.cliente_id IS NULL"
            );
            System.out.println("   ✅ Nuevos clientes creados: " + inserted);

            // PASO 5: Verificación final
            System.out.println("\n📈 PASO 5: Estadísticas finales");
            System.out.println("-------------------------------------------------------------");
            ResultSet rs5 = stmt.executeQuery(
                "SELECT " +
                "(SELECT COUNT(*) FROM usuarios WHERE role = 'USER') AS usuarios_tipo_user, " +
                "(SELECT COUNT(*) FROM clientes) AS total_clientes, " +
                "(SELECT COUNT(*) FROM clientes WHERE usuario_id IS NOT NULL) AS clientes_con_usuario, " +
                "(SELECT COUNT(*) FROM clientes WHERE usuario_id IS NULL) AS clientes_sin_usuario"
            );
            if (rs5.next()) {
                System.out.println("   Usuarios tipo USER: " + rs5.getInt("usuarios_tipo_user"));
                System.out.println("   Total clientes: " + rs5.getInt("total_clientes"));
                System.out.println("   Clientes con usuario: " + rs5.getInt("clientes_con_usuario"));
                System.out.println("   Clientes sin usuario: " + rs5.getInt("clientes_sin_usuario"));
            }
            rs5.close();

            System.out.println("\n=============================================================");
            System.out.println("✅ MIGRACIÓN COMPLETADA EXITOSAMENTE");
            System.out.println("=============================================================\n");

            migrationExecuted = true;

        } catch (Exception e) {
            System.err.println("\n❌ ERROR EN LA MIGRACIÓN: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
