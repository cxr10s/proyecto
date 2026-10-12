package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * DAO de la tabla ENCUESTADOR (solo inicio de sesión).
 *
 * La columna de la contraseña se busca por su nombre en los metadatos (empieza por "CONTRASE"),
 * así funciona tanto si en tu base se llama CONTRASEÑA como CONTRASENA.
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class EncuestadorDAO {

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    /**
     * Devuelve el encuestador si el usuario y la contraseña coinciden; null si no.
     * (usuario no es único en la tabla, por eso se revisan todas las coincidencias).
     */
    public Encuestador autenticar(String usuario, String clave) throws SQLException {
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM encuestador WHERE usuario = ?")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String claveBD = leerClave(rs);
                    if (claveBD != null && claveBD.equals(clave)) {
                        return new Encuestador(
                                rs.getString("cedula"),
                                rs.getString("nombres"),
                                rs.getString("apellidos"),
                                rs.getString("usuario"),
                                rs.getString("cedula_supervisor"));
                    }
                }
            }
        }
        return null;
    }

    private static String leerClave(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++) {
            if (md.getColumnName(i).toUpperCase().startsWith("CONTRASE")) {
                return rs.getString(i);
            }
        }
        return null;
    }
}
