import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ResetUsuarios {
    public static void main(String[] args) {
        String url = "jdbc:mysql://prueba.cd8ugs4ict9h.us-east-2.rds.amazonaws.com:3306/autofixpro?useSSL=true&allowPublicKeyRetrieval=true";
        String user = "admin";
        String password = "cienpies92";

        try {
            System.out.println("========================================");
            System.out.println("🔄 Conectando a MySQL...");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conectado a MySQL exitosamente");

            Statement stmt = conn.createStatement();

            // Eliminar todos los usuarios
            System.out.println("🗑️  Eliminando usuarios existentes...");
            int rowsDeleted = stmt.executeUpdate("DELETE FROM usuarios");
            System.out.println("✅ Usuarios eliminados: " + rowsDeleted);

            // Verificar
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            rs.next();
            int count = rs.getInt(1);
            System.out.println("📊 Total usuarios restantes: " + count);

            if (count == 0) {
                System.out.println("✅ ¡Base de datos lista para DataLoader!");
            }

            System.out.println("========================================");

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}