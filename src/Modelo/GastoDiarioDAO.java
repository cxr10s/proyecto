package Modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla GASTOS_DIARIOS_PERSONALES.
 *
 * - id_gastos_diarios: automático (GD001, GD002, ...).
 * - numero_orden: automático por hogar (1, 2, 3, ...).
 * - num_encuesta: se busca solo a partir del hogar (identificador.num_encuesta).
 * - dia_semana: se calcula solo a partir de la fecha.
 * - total_renglones: se recalcula solo (cantidad de gastos registrados del hogar)
 *   cada vez que se agrega o elimina un gasto.
 * - Unidad de medida, forma de adquisición, lugar de compra y frecuencia
 *   se guardan por su id (llaves foráneas a las tablas de catálogo).
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class GastoDiarioDAO {

    private static final String PREFIJO_ID = "GD";

    private static final String SELECT_BASE =
            "SELECT g.numero_orden, g.fecha, g.nombre_articulo, g.cantidad, "
            + "g.id_medida, um.tipo_de_medida, g.equivalencia, "
            + "g.id_forma_adquisicion, fa.nombre_forma, "
            + "g.id_lugar_compra, lc.tipo_lugar, g.costo, "
            + "g.id_frecuencia, fc.nombre_frecuencia, g.gastos_hogar_o_personal "
            + "FROM gastos_diarios_personales g "
            + "JOIN unidad_medida um ON um.id_medida = g.id_medida "
            + "JOIN forma_adquisicion fa ON fa.id_forma_adquisicion = g.id_forma_adquisicion "
            + "JOIN lugar_compra lc ON lc.id_lugar_compra = g.id_lugar_compra "
            + "JOIN frecuencia_compra fc ON fc.id_frecuencia = g.id_frecuencia "
            + "WHERE g.num_encuesta = (SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?) ";

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
    public void agregar(GastoDiario g) throws SQLException {
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            String numEncuesta = numEncuestaDe(con, g.getIdHogar());
            String id = siguienteId(con);
            int orden = siguienteOrden(con, numEncuesta);

            String sql = "INSERT INTO gastos_diarios_personales (id_gastos_diarios, dia_semana, fecha, "
                    + "total_renglones, numero_orden, nombre_articulo, cantidad, id_medida, equivalencia, "
                    + "id_forma_adquisicion, id_lugar_compra, costo, id_frecuencia, "
                    + "gastos_hogar_o_personal, num_encuesta) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int i = 1;
                ps.setString(i++, id);
                ps.setString(i++, diaSemana(g.getFecha()));
                ps.setDate(i++, Date.valueOf(g.getFecha()));
                ps.setInt(i++, 0);                       // se recalcula abajo
                ps.setInt(i++, orden);
                ps.setString(i++, g.getNombreArticulo());
                ps.setInt(i++, g.getCantidad());
                ps.setString(i++, g.getIdMedida());
                ps.setString(i++, g.getEquivalencia());
                ps.setString(i++, g.getIdFormaAdquisicion());
                ps.setString(i++, g.getIdLugarCompra());
                ps.setLong(i++, g.getCosto());
                ps.setString(i++, g.getIdFrecuencia());
                ps.setString(i++, g.getTipoGasto());
                ps.setString(i++, numEncuesta);
                ps.executeUpdate();
            }
            recalcularTotal(con, numEncuesta);

            con.commit();
            g.setNumOrden(orden);
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana GastosDiarios)
    // ------------------------------------------------------------------
    public List<GastoDiario> listar(String idHogar) throws SQLException {
        String sql = SELECT_BASE + "ORDER BY g.numero_orden";
        List<GastoDiario> lista = new ArrayList<>();
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
    public GastoDiario buscar(String idHogar, int numOrden) throws SQLException {
        String sql = SELECT_BASE + "AND g.numero_orden = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            ps.setInt(2, numOrden);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs, idHogar) : null;
            }
        }
    }

    // ------------------------------------------------------------------
    // MODIFICAR (el gasto se identifica por idHogar + numOrden)
    // ------------------------------------------------------------------
    public void modificar(GastoDiario g) throws SQLException {
        String sql = "UPDATE gastos_diarios_personales SET dia_semana = ?, fecha = ?, nombre_articulo = ?, "
                + "cantidad = ?, id_medida = ?, equivalencia = ?, id_forma_adquisicion = ?, "
                + "id_lugar_compra = ?, costo = ?, id_frecuencia = ?, gastos_hogar_o_personal = ? "
                + "WHERE numero_orden = ? AND num_encuesta = "
                + "(SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?)";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, diaSemana(g.getFecha()));
            ps.setDate(i++, Date.valueOf(g.getFecha()));
            ps.setString(i++, g.getNombreArticulo());
            ps.setInt(i++, g.getCantidad());
            ps.setString(i++, g.getIdMedida());
            ps.setString(i++, g.getEquivalencia());
            ps.setString(i++, g.getIdFormaAdquisicion());
            ps.setString(i++, g.getIdLugarCompra());
            ps.setLong(i++, g.getCosto());
            ps.setString(i++, g.getIdFrecuencia());
            ps.setString(i++, g.getTipoGasto());
            ps.setInt(i++, g.getNumOrden());
            ps.setString(i++, g.getIdHogar());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el gasto " + g.getNumOrden() + " para modificar.");
            }
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR (y recalcula el total de renglones del hogar)
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, int numOrden) throws SQLException {
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);
            String numEncuesta = numEncuestaDe(con, idHogar);
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM gastos_diarios_personales WHERE num_encuesta = ? AND numero_orden = ?")) {
                ps.setString(1, numEncuesta);
                ps.setInt(2, numOrden);
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("No se encontró el gasto " + numOrden + ".");
                }
            }
            recalcularTotal(con, numEncuesta);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------
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

    /** Siguiente id_gastos_diarios con formato GD001, GD002, ... */
    private String siguienteId(Connection con) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(id_gastos_diarios, '^" + PREFIJO_ID + "[0-9]+$') "
                + "THEN SUBSTR(id_gastos_diarios, " + (PREFIJO_ID.length() + 1) + ") END)), 0) + 1 "
                + "FROM gastos_diarios_personales";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return PREFIJO_ID + String.format("%03d", rs.getInt(1));
        }
    }

    /** Siguiente número de orden de ese hogar: 1, 2, 3, ... */
    private int siguienteOrden(Connection con, String numEncuesta) throws SQLException {
        String sql = "SELECT NVL(MAX(numero_orden), 0) + 1 FROM gastos_diarios_personales WHERE num_encuesta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numEncuesta);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Deja en total_renglones de todas las filas del hogar la cantidad de gastos registrados. */
    private void recalcularTotal(Connection con, String numEncuesta) throws SQLException {
        String sql = "UPDATE gastos_diarios_personales SET total_renglones = "
                + "(SELECT COUNT(*) FROM gastos_diarios_personales WHERE num_encuesta = ?) "
                + "WHERE num_encuesta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numEncuesta);
            ps.setString(2, numEncuesta);
            ps.executeUpdate();
        }
    }

    private GastoDiario mapear(ResultSet rs, String idHogar) throws SQLException {
        GastoDiario g = new GastoDiario();
        g.setIdHogar(idHogar);
        g.setNumOrden(rs.getInt("numero_orden"));
        Date f = rs.getDate("fecha");
        g.setFecha(f == null ? null : f.toLocalDate());
        g.setNombreArticulo(rs.getString("nombre_articulo"));
        g.setCantidad(rs.getInt("cantidad"));
        g.setIdMedida(rs.getString("id_medida"));
        g.setNombreMedida(rs.getString("tipo_de_medida"));
        g.setEquivalencia(rs.getString("equivalencia"));
        g.setIdFormaAdquisicion(rs.getString("id_forma_adquisicion"));
        g.setNombreForma(rs.getString("nombre_forma"));
        g.setIdLugarCompra(rs.getString("id_lugar_compra"));
        g.setNombreLugar(rs.getString("tipo_lugar"));
        g.setCosto(rs.getLong("costo"));
        g.setIdFrecuencia(rs.getString("id_frecuencia"));
        g.setNombreFrecuencia(rs.getString("nombre_frecuencia"));
        g.setTipoGasto(rs.getString("gastos_hogar_o_personal"));
        return g;
    }

    /** Día de la semana sin tildes (la columna dia_semana mide 10). */
    private static String diaSemana(LocalDate fecha) {
        DayOfWeek d = fecha.getDayOfWeek();
        switch (d) {
            case MONDAY:    return "Lunes";
            case TUESDAY:   return "Martes";
            case WEDNESDAY: return "Miercoles";
            case THURSDAY:  return "Jueves";
            case FRIDAY:    return "Viernes";
            case SATURDAY:  return "Sabado";
            default:        return "Domingo";
        }
    }
}
