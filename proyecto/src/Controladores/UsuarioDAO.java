package Controladores;

import Modelo.ConexionBD;
import Modelo.Errores;
import Modelo.Usuario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Inicio de sesión (reemplaza a EncuestadorDAO). Usa el paquete pkg_login.
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class UsuarioDAO {

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    /** Devuelve el usuario si el usuario y la contraseña coinciden; null si no. */
    public Usuario autenticar(String usuario, String clave) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_login.autenticar(?, ?, ?, ?, ?, ?)}")) {
            cs.setString(1, usuario);
            cs.setString(2, clave);
            cs.registerOutParameter(3, Types.VARCHAR);   // cédula
            cs.registerOutParameter(4, Types.VARCHAR);   // nombres
            cs.registerOutParameter(5, Types.VARCHAR);   // apellidos
            cs.registerOutParameter(6, Types.VARCHAR);   // rol
            cs.execute();

            String cedula = cs.getString(3);
            if (cedula == null) {
                return null;
            }
            return new Usuario(cedula, cs.getString(4), cs.getString(5), cs.getString(6));
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }
}
