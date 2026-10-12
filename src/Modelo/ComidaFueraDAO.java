package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla COMIDAS_FUERA_HOGAR.
 *
 * - id_comidas_fuera_hogar: automático (CF001, CF002, ...).
 * - num_orden: automático por hogar (1, 2, 3, ...).
 * - num_encuesta: se busca solo a partir del hogar (identificador.num_encuesta).
 * - Tipo de comida: se marca con "X" solo la columna elegida (desayuno, almuerzo, cena u onces).
 * - Domicilio: "X" si lo adquirió a domicilio; vacío si no.
 * - Forma de adquisición, lugar de compra y frecuencia se guardan por su id (llaves foráneas).
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class ComidaFueraDAO {

    private static final String PREFIJO_ID = "CF";
    private static final String MARCA = "X";

    private static final String SELECT_BASE =
            "SELECT c.num_orden, c.descripcion_alimento, "
            + "c.tipo_desayuno, c.tipo_almuerzo, c.tipo_cena, c.tipo_onces, c.cantidad, "
            + "c.id_forma_adquisicion, fa.nombre_forma, "
            + "c.id_lugar_compra, lc.tipo_lugar, c.costo, "
            + "c.id_frecuencia, fc.nombre_frecuencia, c.gasto_hogar_o_personal, c.domicilio "
            + "FROM comidas_fuera_hogar c "
            + "JOIN forma_adquisicion fa ON fa.id_forma_adquisicion = c.id_forma_adquisicion "
            + "JOIN lugar_compra lc ON lc.id_lugar_compra = c.id_lugar_compra "
            + "JOIN frecuencia_compra fc ON fc.id_frecuencia = c.id_frecuencia "
            + "WHERE c.num_encuesta = (SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?) ";

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
    public void agregar(ComidaFuera c) throws SQLException {
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            String numEncuesta = numEncuestaDe(con, c.getIdHogar());
            String id = siguienteId(con);
            int orden = siguienteOrden(con, numEncuesta);

            String sql = "INSERT INTO comidas_fuera_hogar (id_comidas_fuera_hogar, num_orden, "
                    + "descripcion_alimento, tipo_desayuno, tipo_almuerzo, tipo_cena, tipo_onces, cantidad, "
                    + "id_forma_adquisicion, id_lugar_compra, costo, id_frecuencia, "
                    + "gasto_hogar_o_personal, domicilio, num_encuesta) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int i = 1;
                ps.setString(i++, id);
                ps.setInt(i++, orden);
                ps.setString(i++, c.getDescripcion());
                setMarca(ps, i++, ComidaFuera.DESAYUNO.equals(c.getTipoComida()));
                setMarca(ps, i++, ComidaFuera.ALMUERZO.equals(c.getTipoComida()));
                setMarca(ps, i++, ComidaFuera.CENA.equals(c.getTipoComida()));
                setMarca(ps, i++, ComidaFuera.ONCES.equals(c.getTipoComida()));
                ps.setInt(i++, c.getCantidad());
                ps.setString(i++, c.getIdFormaAdquisicion());
                ps.setString(i++, c.getIdLugarCompra());
                ps.setLong(i++, c.getCosto());
                ps.setString(i++, c.getIdFrecuencia());
                ps.setString(i++, c.getTipoGasto());
                setMarca(ps, i++, c.isDomicilio());
                ps.setString(i++, numEncuesta);
                ps.executeUpdate();
            }

            con.commit();
            c.setNumOrden(orden);
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana ComidasPreparadasFueraDelHogar)
    // ------------------------------------------------------------------
    public List<ComidaFuera> listar(String idHogar) throws SQLException {
        String sql = SELECT_BASE + "ORDER BY c.num_orden";
        List<ComidaFuera> lista = new ArrayList<>();
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
    public ComidaFuera buscar(String idHogar, int numOrden) throws SQLException {
        String sql = SELECT_BASE + "AND c.num_orden = ?";
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
    // MODIFICAR (la comida se identifica por idHogar + numOrden)
    // ------------------------------------------------------------------
    public void modificar(ComidaFuera c) throws SQLException {
        String sql = "UPDATE comidas_fuera_hogar SET descripcion_alimento = ?, "
                + "tipo_desayuno = ?, tipo_almuerzo = ?, tipo_cena = ?, tipo_onces = ?, cantidad = ?, "
                + "id_forma_adquisicion = ?, id_lugar_compra = ?, costo = ?, id_frecuencia = ?, "
                + "gasto_hogar_o_personal = ?, domicilio = ? "
                + "WHERE num_orden = ? AND num_encuesta = "
                + "(SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?)";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, c.getDescripcion());
            setMarca(ps, i++, ComidaFuera.DESAYUNO.equals(c.getTipoComida()));
            setMarca(ps, i++, ComidaFuera.ALMUERZO.equals(c.getTipoComida()));
            setMarca(ps, i++, ComidaFuera.CENA.equals(c.getTipoComida()));
            setMarca(ps, i++, ComidaFuera.ONCES.equals(c.getTipoComida()));
            ps.setInt(i++, c.getCantidad());
            ps.setString(i++, c.getIdFormaAdquisicion());
            ps.setString(i++, c.getIdLugarCompra());
            ps.setLong(i++, c.getCosto());
            ps.setString(i++, c.getIdFrecuencia());
            ps.setString(i++, c.getTipoGasto());
            setMarca(ps, i++, c.isDomicilio());
            ps.setInt(i++, c.getNumOrden());
            ps.setString(i++, c.getIdHogar());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la comida " + c.getNumOrden() + " para modificar.");
            }
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, int numOrden) throws SQLException {
        Connection con = conectar();
        String numEncuesta = numEncuestaDe(con, idHogar);
        try (PreparedStatement ps = con.prepareStatement(
                "DELETE FROM comidas_fuera_hogar WHERE num_encuesta = ? AND num_orden = ?")) {
            ps.setString(1, numEncuesta);
            ps.setInt(2, numOrden);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la comida " + numOrden + ".");
            }
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

    /** Siguiente id_comidas_fuera_hogar con formato CF001, CF002, ... */
    private String siguienteId(Connection con) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(id_comidas_fuera_hogar, '^" + PREFIJO_ID + "[0-9]+$') "
                + "THEN SUBSTR(id_comidas_fuera_hogar, " + (PREFIJO_ID.length() + 1) + ") END)), 0) + 1 "
                + "FROM comidas_fuera_hogar";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return PREFIJO_ID + String.format("%03d", rs.getInt(1));
        }
    }

    /** Siguiente número de orden de ese hogar: 1, 2, 3, ... */
    private int siguienteOrden(Connection con, String numEncuesta) throws SQLException {
        String sql = "SELECT NVL(MAX(num_orden), 0) + 1 FROM comidas_fuera_hogar WHERE num_encuesta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numEncuesta);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private ComidaFuera mapear(ResultSet rs, String idHogar) throws SQLException {
        ComidaFuera c = new ComidaFuera();
        c.setIdHogar(idHogar);
        c.setNumOrden(rs.getInt("num_orden"));
        c.setDescripcion(rs.getString("descripcion_alimento"));
        if (marcado(rs.getString("tipo_desayuno"))) {
            c.setTipoComida(ComidaFuera.DESAYUNO);
        } else if (marcado(rs.getString("tipo_almuerzo"))) {
            c.setTipoComida(ComidaFuera.ALMUERZO);
        } else if (marcado(rs.getString("tipo_cena"))) {
            c.setTipoComida(ComidaFuera.CENA);
        } else if (marcado(rs.getString("tipo_onces"))) {
            c.setTipoComida(ComidaFuera.ONCES);
        }
        c.setCantidad(rs.getInt("cantidad"));
        c.setIdFormaAdquisicion(rs.getString("id_forma_adquisicion"));
        c.setNombreForma(rs.getString("nombre_forma"));
        c.setIdLugarCompra(rs.getString("id_lugar_compra"));
        c.setNombreLugar(rs.getString("tipo_lugar"));
        c.setCosto(rs.getLong("costo"));
        c.setIdFrecuencia(rs.getString("id_frecuencia"));
        c.setNombreFrecuencia(rs.getString("nombre_frecuencia"));
        c.setTipoGasto(rs.getString("gasto_hogar_o_personal"));
        c.setDomicilio(marcado(rs.getString("domicilio")));
        return c;
    }

    private static boolean marcado(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /** Pone "X" si está marcado o NULL si no. */
    private static void setMarca(PreparedStatement ps, int pos, boolean marcado) throws SQLException {
        if (marcado) {
            ps.setString(pos, MARCA);
        } else {
            ps.setNull(pos, Types.VARCHAR);
        }
    }
}
