import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar contraseñas encriptadas con BCrypt
 *
 * CÓMO USAR:
 * 1. Compila: javac -cp "build/libs/*" GenerarPasswordBCrypt.java
 * 2. Ejecuta: java -cp ".:build/libs/*" GenerarPasswordBCrypt tu_contraseña
 */
public class GenerarPasswordBCrypt {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("===========================================");
            System.out.println("GENERADOR DE CONTRASEÑAS BCRYPT");
            System.out.println("===========================================");
            System.out.println("Uso: java GenerarPasswordBCrypt <contraseña>");
            System.out.println();
            System.out.println("Ejemplo:");
            System.out.println("  java GenerarPasswordBCrypt micontraseña123");
            System.out.println();
            System.out.println("Generando ejemplos con 'admin123':");
            generarHash("admin123");
            System.out.println();
            System.out.println("Generando ejemplos con 'password':");
            generarHash("password");
            System.out.println();
            return;
        }

        String password = args[0];
        generarHash(password);
    }

    private static void generarHash(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        System.out.println("===========================================");
        System.out.println("Contraseña original: " + password);
        System.out.println("Hash BCrypt: " + hashedPassword);
        System.out.println("===========================================");
        System.out.println();
        System.out.println("SQL para actualizar tu usuario:");
        System.out.println("UPDATE usuarios SET password = '" + hashedPassword + "' WHERE username = 'tu_usuario';");
        System.out.println();
    }
}
