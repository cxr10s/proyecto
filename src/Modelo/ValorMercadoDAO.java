package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * DAO de la tabla VALOR_MERCADO (valor total del mercado de un hogar, por tipo de mercado).
 *
 * La tabla no tiene columna de hogar, así que el hogar va dentro del código:
 *   codigo = "VM" + idHogar + "-" + id del tipo de mercado   (por ejemplo "VM001-95").
 * Así no hace falta modificar la tabla. Guardar es "crear o actualizar":
 * un hogar tiene como máximo un valor por tipo de mercado.
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class ValorMercadoDAO {

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    /** Prefijo de los códigos de un hogar: "VM001-". */
    public static String prefijo(String idHogar) {
        return "VM" + idHogar + "-";
    }

    /** Crea o actualiza el valor del mercado de ese tipo para el hogar. */
    public void guardar(String idHogar, String idTipoMercado, long valor) throws SQLException {
        String codigo = prefijo(idHogar) + idTipoMercado;
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            int filas;
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE valor_mercado SET valor_total = ? WHERE codigo = ?")) {
                ps.setLong(1, valor);
                ps.setString(2, codigo);
                filas = ps.executeUpdate();
            }
            if (filas == 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO valor_mercado (codigo, id_tipo_mercado, valor_total) VALUES (?, ?, ?)")) {
                    ps.setString(1, codigo);
                    ps.setString(2, idTipoMercado);
                    ps.setLong(3, valor);
                    ps.executeUpdate();
                }
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    /** Valores guardados del hogar: id del tipo de mercado -> valor. */
    public Map<String, Long> listar(String idHogar) throws SQLException {
        Map<String, Long> valores = new HashMap<>();
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_tipo_mercado, valor_total FROM valor_mercado WHERE codigo LIKE ?")) {
            ps.setString(1, prefijo(idHogar) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    valores.put(rs.getString(1), rs.getLong(2));
                }
            }
        }
        return valores;
    }
}
