package com.example.autofixpro.config;

import com.example.autofixpro.dao.UsuarioDAO;
import com.example.autofixpro.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("🔐 VERIFICANDO USUARIOS INICIALES...");
        System.out.println("========================================");

        long totalUsuarios = usuarioDAO.count();
        System.out.println("📊 Usuarios existentes en la base de datos: " + totalUsuarios);

        // Solo crear usuarios si no existe ninguno
        if (totalUsuarios == 0) {
            System.out.println("========================================");
            System.out.println("🆕 Creando usuarios iniciales...");
            System.out.println("========================================");

            // Crear usuario ADMIN
            if (usuarioDAO.findByUsername("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNombre("Administrador del Sistema");
                admin.setEmail("admin@autofixpro.com");
                admin.setTelefono("+51999999999");
                admin.setRole(Usuario.Role.ADMIN);
                admin.setActivo(true);
                usuarioDAO.save(admin);
                System.out.println("✅ Usuario ADMIN creado: admin / admin123");
            }

            // Crear usuario TECNICO
            if (usuarioDAO.findByUsername("tecnico1nec").isEmpty()) {
                Usuario tecnico = new Usuario();
                tecnico.setUsername("tecnico1nec");
                tecnico.setPassword(passwordEncoder.encode("admin123"));
                tecnico.setNombre("José Luis Ramírez");
                tecnico.setEmail("jose_luis@outlook.com");
                tecnico.setTelefono("+51965409978");
                tecnico.setRole(Usuario.Role.TECNICO);
                tecnico.setActivo(true);
                usuarioDAO.save(tecnico);
                System.out.println("✅ Usuario TECNICO creado: tecnico1nec / admin123");
            }

            // Crear usuario RECEPCIONISTA
            if (usuarioDAO.findByUsername("recepcion").isEmpty()) {
                Usuario recepcion = new Usuario();
                recepcion.setUsername("recepcion");
                recepcion.setPassword(passwordEncoder.encode("admin123"));
                recepcion.setNombre("María Recepción");
                recepcion.setEmail("recepcion@autofixpro.com");
                recepcion.setTelefono("+51988888888");
                recepcion.setRole(Usuario.Role.RECEPCIONISTA);
                recepcion.setActivo(true);
                usuarioDAO.save(recepcion);
                System.out.println("✅ Usuario RECEPCIONISTA creado: recepcion / admin123");
            }

            // Crear usuario USER
            if (usuarioDAO.findByUsername("cliente1").isEmpty()) {
                Usuario cliente = new Usuario();
                cliente.setUsername("cliente1");
                cliente.setPassword(passwordEncoder.encode("admin123"));
                cliente.setNombre("Juan Pérez García");
                cliente.setEmail("juan.perez@email.com");
                cliente.setTelefono("+51987654321");
                cliente.setRole(Usuario.Role.USER);
                cliente.setActivo(true);
                usuarioDAO.save(cliente);
                System.out.println("✅ Usuario USER creado: cliente1 / admin123");
            }

            System.out.println("========================================");
            System.out.println("✅ Total usuarios en la base de datos: " + usuarioDAO.count());
            System.out.println("========================================");
        } else {
            System.out.println("ℹ️  Los usuarios ya existen. No se crearán duplicados.");
            System.out.println("========================================");
        }
    }
}