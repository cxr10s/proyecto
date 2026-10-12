/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author ASUS
 */
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    
     private static ConexionBD instancia;
    private Connection connection = null;
    
    private final String url = "jdbc:oracle:thin:@192.168.254.215:1521:orcl";   // Oracle 10g XE: formato SID
    private final String user = "proyectointcrud";
    private final String password = "proyectointcrud";

    // Constructor privado para Singleton
    private ConexionBD() {
        conectar();
    }

    // Método para conectar a la base de datos
    private void conectar() {
        try {
            // ojdbc14 (driver de Oracle 10g) no se registra solo: hay que cargarlo a mano
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Conexión establecida: " + meta.getDriverName());
            }
        } catch (ClassNotFoundException ex) {
            System.out.println("No se encontró el driver de Oracle (agrega ojdbc14.jar a Libraries): " + ex.getMessage());
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }
    }

    // Obtener instancia única (patrón Singleton)
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Obtener conexión
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Conexión cerrada. Intentando reconectar...");
                conectar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Cerrar la conexión
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
