package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla CARACTERISTICAS_GASTOS.
 *
 * - id_caracteristicas: automático (CG001, CG002, ...).
 * - alimento: código del grupo ("01" ... "25"); nombre_alimento: su nombre (máx. 100 caracteres).
 * - Un alimento solo puede registrarse una vez por hogar (para cambiarlo se usa MODIFICAR).
 * - La frecuencia se guarda con "X" en una sola de las 9 columnas frec_*.
 * - val_ultima_compra y comprara_de_nuevo solo se preguntan si la frecuencia es mensual o mayor;
 *   si no aplica, comprara_de_nuevo queda en "NA" y el valor en NULL.
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class CaracteristicaGastoDAO {

    private static final String PREFIJO_ID = "CG";
    private static final String MARCA = "X";

    private static final String COLUMNAS_FREC = String.join(", ", CaracteristicaGasto.FREC_COLUMNAS);

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    // ------------------------------------------------------------------
    // AGREGAR
    // ------------------------------------------------------------------
    public void agregar(CaracteristicaGasto g) throws SQLException {
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            String numEncuesta = numEncuestaDe(con, g.getIdHogar());
            if (existe(con, numEncuesta, g.getCodigoAlimento())) {
                throw new SQLException("Ese alimento ya está registrado en este hogar.\n"
                        + "Selecciónelo en la tabla y use MODIFICAR.");
            }
            String id = siguienteId(con);

            String sql = "INSERT INTO caracteristicas_gastos (id_caracteristicas, alimento, nombre_alimento, "
                    + COLUMNAS_FREC + ", val_ultima_compra, comprara_de_nuevo, observaciones, num_encuesta) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int i = 1;
                ps.setString(i++, id);
                ps.setString(i++, g.getCodigoAlimento());
                ps.setString(i++, recortar(g.getNombreAlimento(), 100));
                for (int f = 0; f < CaracteristicaGasto.FREC_COLUMNAS.length; f++) {
                    setMarca(ps, i++, f == g.getFrecuencia());
                }
                setValor(ps, i++, g.getValorUltimaCompra());
                ps.setString(i++, g.getComprara());
                setTexto(ps, i++, g.getObservaciones());
                ps.setString(i++, numEncuesta);
                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana CaracteristicasDelGastoHogar)
    // ------------------------------------------------------------------
    public List<CaracteristicaGasto> listar(String idHogar) throws SQLException {
        String sql = SELECT_BASE + "ORDER BY alimento";
        List<CaracteristicaGasto> lista = new ArrayList<>();
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs, idHogar));
                }
            }
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // BUSCAR (para llenar la ventana de modificar)
    // ------------------------------------------------------------------
    public CaracteristicaGasto buscar(String idHogar, String codigoAlimento) throws SQLException {
        String sql = SELECT_BASE + "AND alimento = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            ps.setString(2, codigoAlimento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs, idHogar) : null;
            }
        }
    }

    // ------------------------------------------------------------------
    // MODIFICAR (el registro se identifica por idHogar + codigoAlimento)
    // ------------------------------------------------------------------
    public void modificar(CaracteristicaGasto g) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE caracteristicas_gastos SET ");
        for (String col : CaracteristicaGasto.FREC_COLUMNAS) {
            sql.append(col).append(" = ?, ");
        }
        sql.append("val_ultima_compra = ?, comprara_de_nuevo = ?, observaciones = ? ")
           .append("WHERE alimento = ? AND num_encuesta = ")
           .append("(SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?)");
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            for (int f = 0; f < CaracteristicaGasto.FREC_COLUMNAS.length; f++) {
                setMarca(ps, i++, f == g.getFrecuencia());
            }
            setValor(ps, i++, g.getValorUltimaCompra());
            ps.setString(i++, g.getComprara());
            setTexto(ps, i++, g.getObservaciones());
            ps.setString(i++, g.getCodigoAlimento());
            ps.setString(i++, g.getIdHogar());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el alimento para modificar.");
            }
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, String codigoAlimento) throws SQLException {
        Connection con = conectar();
        String numEncuesta = numEncuestaDe(con, idHogar);
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM caracteristicas_gastos WHERE num_encuesta = ? AND alimento = ?")) {
            ps.setString(1, numEncuesta);
            ps.setString(2, codigoAlimento);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el alimento.");
            }
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------
    private static final String SELECT_BASE =
            "SELECT alimento, nombre_alimento, " + COLUMNAS_FREC + ", "
            + "val_ultima_compra, comprara_de_nuevo, observaciones "
            + "FROM caracteristicas_gastos "
            + "WHERE num_encuesta = (SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?) ";

    /** num_encuesta (ENC001...) del hogar. */
    private String numEncuestaDe(Connection con, String idHogar) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT num_encuesta FROM identificador WHERE id_hogar = ?")) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        throw new SQLException("No existe el hogar " + idHogar + ".");
    }

    private boolean existe(Connection con, String numEncuesta, String codigoAlimento) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM caracteristicas_gastos WHERE num_encuesta = ? AND alimento = ?")) {
            ps.setString(1, numEncuesta);
            ps.setString(2, codigoAlimento);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /** Siguiente id_caracteristicas con formato CG001, CG002, ... */
    private String siguienteId(Connection con) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(id_caracteristicas, '^" + PREFIJO_ID + "[0-9]+$') "
                + "THEN SUBSTR(id_caracteristicas, " + (PREFIJO_ID.length() + 1) + ") END)), 0) + 1 "
                + "FROM caracteristicas_gastos";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return PREFIJO_ID + String.format("%03d", rs.getInt(1));
        }
    }

    private CaracteristicaGasto mapear(ResultSet rs, String idHogar) throws SQLException {
        CaracteristicaGasto g = new CaracteristicaGasto();
        g.setIdHogar(idHogar);
        g.setCodigoAlimento(rs.getString("alimento"));
        g.setNombreAlimento(rs.getString("nombre_alimento"));
        g.setFrecuencia(-1);
        for (int f = 0; f < CaracteristicaGasto.FREC_COLUMNAS.length; f++) {
            String marca = rs.getString(CaracteristicaGasto.FREC_COLUMNAS[f]);
            if (marca != null && !marca.trim().isEmpty()) {
                g.setFrecuencia(f);
                break;
            }
        }
        long valor = rs.getLong("val_ultima_compra");
        g.setValorUltimaCompra(rs.wasNull() ? null : valor);
        g.setComprara(rs.getString("comprara_de_nuevo"));
        g.setObservaciones(rs.getString("observaciones"));
        return g;
    }

    private static String recortar(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) : s;
    }

    private static void setMarca(PreparedStatement ps, int pos, boolean marcado) throws SQLException {
        if (marcado) {
            ps.setString(pos, MARCA);
        } else {
            ps.setNull(pos, Types.VARCHAR);
        }
    }

    private static void setValor(PreparedStatement ps, int pos, Long valor) throws SQLException {
        if (valor == null) {
            ps.setNull(pos, Types.NUMERIC);
        } else {
            ps.setLong(pos, valor);
        }
    }

    private static void setTexto(PreparedStatement ps, int pos, String s) throws SQLException {
        if (s == null || s.isEmpty()) {
            ps.setNull(pos, Types.VARCHAR);
        } else {
            ps.setString(pos, s);
        }
    }
}
