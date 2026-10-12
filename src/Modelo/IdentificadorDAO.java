package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla IDENTIFICADOR.
 *
 * Al agregar un hogar se crea primero su ENCUESTA (num_encuesta automático) y
 * luego el IDENTIFICADOR (id_hogar automático desde 001), todo en una sola
 * transacción: si algo falla, no se guarda nada.
 */
public class IdentificadorDAO {

    // ---- Valores fijos de esta versión ----
    private static final String ESTADO_INICIAL = "Sin revisar";
    private static final String RESULTADO_INICIAL = "Encuesta incompleta";
    private static final String PREFIJO_ENCUESTA = "ENC";   // ENC001, ENC002...

    // Se buscan en la base por nombre (sin importar mayúsculas)
    private static final String REGION = "Oriental";
    private static final String DEPARTAMENTO = "Santander";
    private static final String MUNICIPIO = "Lebrija";

    /** Cédula del encuestador que inició sesión en el Login. */
    private String cedulaEncuestador() throws SQLException {
        String cedula = Sesion.getCedula();
        if (cedula == null) {
            throw new SQLException("No hay un encuestador con sesión iniciada.\n"
                    + "Inicie la aplicación desde la ventana de Login.");
        }
        return cedula;
    }

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
    public void agregar(Identificador h) throws SQLException {
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            String cedulaEncuestador = cedulaEncuestador();
            String cedulaSupervisor = obtenerSupervisor(con, cedulaEncuestador);
            String idRegion = buscarIdPorNombre(con, "region", REGION);
            String idDepto = buscarIdPorNombre(con, "departamento", DEPARTAMENTO);
            String idMunicipio = buscarIdPorNombre(con, "municipio", MUNICIPIO);
            String numEncuesta = siguienteNumEncuesta(con);
            String idHogar = siguienteIdHogar(con);

            // 1) Encuesta (la tabla identificador depende de ella)
            String sqlEncuesta = "INSERT INTO encuesta (num_encuesta, cedula_encuestador, "
                    + "cedula_supervisor, estado, resultado_encuesta) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlEncuesta)) {
                ps.setString(1, numEncuesta);
                ps.setString(2, cedulaEncuestador);
                ps.setString(3, cedulaSupervisor);
                ps.setString(4, ESTADO_INICIAL);
                ps.setString(5, RESULTADO_INICIAL);
                ps.executeUpdate();
            }

            // 2) Identificador del hogar
            String sqlHogar = "INSERT INTO identificador (id_hogar, id_region, id_departamento, "
                    + "id_municipio, clase, sector, seccion, manzana, segmento, ciclo, "
                    + "num_edificacion, num_vivienda, total_hogares, num_hogar, total_personas, "
                    + "barrio_centropoblad_vereda, direccion, telefono, num_encuesta) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlHogar)) {
                int i = 1;
                ps.setString(i++, idHogar);
                ps.setString(i++, idRegion);
                ps.setString(i++, idDepto);
                ps.setString(i++, idMunicipio);
                ps.setString(i++, h.getClase());
                ps.setString(i++, h.getSector());
                ps.setString(i++, h.getSeccion());
                ps.setString(i++, h.getManzana());
                ps.setString(i++, h.getSegmento());
                ps.setString(i++, h.getCiclo());
                ps.setString(i++, h.getNumEdificacion());
                ps.setString(i++, h.getNumVivienda());
                ps.setString(i++, h.getTotalHogares());
                ps.setString(i++, h.getNumHogar());
                ps.setString(i++, h.getTotalPersonas());
                ps.setString(i++, h.getBarrioCentroVereda());
                ps.setString(i++, h.getDireccion());
                ps.setString(i++, h.getTelefono());
                ps.setString(i++, numEncuesta);
                ps.executeUpdate();
            }

            con.commit();
            h.setIdHogar(idHogar);
            h.setNumEncuesta(numEncuesta);
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(autoCommitOriginal);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana Inicio)
    // ------------------------------------------------------------------
    public List<Identificador> listar() throws SQLException {
        String sql = "SELECT i.id_hogar, m.nombre AS municipio, e.estado, e.resultado_encuesta "
                + "FROM identificador i "
                + "JOIN municipio m ON m.id = i.id_municipio "
                + "JOIN encuesta e ON e.num_encuesta = i.num_encuesta "
                + "WHERE e.cedula_encuestador = ? "
                + "ORDER BY i.id_hogar";
        List<Identificador> lista = new ArrayList<>();
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedulaEncuestador());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Identificador h = new Identificador();
                    h.setIdHogar(rs.getString("id_hogar"));
                    h.setMunicipio(rs.getString("municipio"));
                    h.setEstado(rs.getString("estado"));
                    h.setValidacion(rs.getString("resultado_encuesta"));
                    lista.add(h);
                }
            }
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // BUSCAR POR ID (para llenar la ventana de modificar)
    // ------------------------------------------------------------------
    public Identificador buscarPorId(String idHogar) throws SQLException {
        String sql = "SELECT * FROM identificador WHERE id_hogar = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Identificador h = new Identificador();
                h.setIdHogar(rs.getString("id_hogar"));
                h.setIdRegion(rs.getString("id_region"));
                h.setIdDepartamento(rs.getString("id_departamento"));
                h.setIdMunicipio(rs.getString("id_municipio"));
                h.setClase(rs.getString("clase"));
                h.setSector(rs.getString("sector"));
                h.setSeccion(rs.getString("seccion"));
                h.setManzana(rs.getString("manzana"));
                h.setSegmento(rs.getString("segmento"));
                h.setCiclo(rs.getString("ciclo"));
                h.setNumEdificacion(rs.getString("num_edificacion"));
                h.setNumVivienda(rs.getString("num_vivienda"));
                h.setTotalHogares(rs.getString("total_hogares"));
                h.setNumHogar(rs.getString("num_hogar"));
                h.setTotalPersonas(rs.getString("total_personas"));
                h.setBarrioCentroVereda(rs.getString("barrio_centropoblad_vereda"));
                h.setDireccion(rs.getString("direccion"));
                h.setTelefono(rs.getString("telefono"));
                h.setNumEncuesta(rs.getString("num_encuesta"));
                return h;
            }
        }
    }

    // ------------------------------------------------------------------
    // RESULTADO DEL CUADERNILLO (se guarda en ENCUESTA.resultado_encuesta;
    // es lo que Inicio muestra en la columna VALIDACION)
    // ------------------------------------------------------------------
    public String obtenerResultado(String idHogar) throws SQLException {
        String sql = "SELECT e.resultado_encuesta FROM encuesta e "
                + "JOIN identificador i ON i.num_encuesta = e.num_encuesta "
                + "WHERE i.id_hogar = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        }
    }

    public void actualizarResultado(String idHogar, String resultado) throws SQLException {
        String sql = "UPDATE encuesta SET resultado_encuesta = ? WHERE cedula_encuestador = ? "
                + "AND num_encuesta = (SELECT i.num_encuesta FROM identificador i WHERE i.id_hogar = ?)";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, resultado);
            ps.setString(2, cedulaEncuestador());
            ps.setString(3, idHogar);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el hogar " + idHogar + ".");
            }
        }
    }

    // ------------------------------------------------------------------
    // MODIFICAR (todo menos región, departamento, municipio, id_hogar y num_encuesta)
    // ------------------------------------------------------------------
    public void modificar(Identificador h) throws SQLException {
        String sql = "UPDATE identificador SET clase = ?, sector = ?, seccion = ?, manzana = ?, "
                + "segmento = ?, ciclo = ?, num_edificacion = ?, num_vivienda = ?, "
                + "total_hogares = ?, num_hogar = ?, total_personas = ?, "
                + "barrio_centropoblad_vereda = ?, direccion = ?, telefono = ? "
                + "WHERE id_hogar = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, h.getClase());
            ps.setString(i++, h.getSector());
            ps.setString(i++, h.getSeccion());
            ps.setString(i++, h.getManzana());
            ps.setString(i++, h.getSegmento());
            ps.setString(i++, h.getCiclo());
            ps.setString(i++, h.getNumEdificacion());
            ps.setString(i++, h.getNumVivienda());
            ps.setString(i++, h.getTotalHogares());
            ps.setString(i++, h.getNumHogar());
            ps.setString(i++, h.getTotalPersonas());
            ps.setString(i++, h.getBarrioCentroVereda());
            ps.setString(i++, h.getDireccion());
            ps.setString(i++, h.getTelefono());
            ps.setString(i++, h.getIdHogar());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró el hogar " + h.getIdHogar() + " para modificar.");
            }
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR (borra el hogar y su encuesta en una sola transacción)
    // ------------------------------------------------------------------
    public void eliminar(String idHogar) throws SQLException {
        Connection con = conectar();
        boolean autoCommitOriginal = con.getAutoCommit();
        try {
            con.setAutoCommit(false);

            String numEncuesta = null;
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT num_encuesta FROM identificador WHERE id_hogar = ?")) {
                ps.setString(1, idHogar);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        numEncuesta = rs.getString(1);
                    }
                }
            }
            if (numEncuesta == null) {
                throw new SQLException("No se encontró el hogar " + idHogar + ".");
            }

            // Valores de mercado del hogar (no tienen llave foránea, se borran por su código)
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM valor_mercado WHERE codigo LIKE ?")) {
                ps.setString(1, ValorMercadoDAO.prefijo(idHogar) + "%");
                ps.executeUpdate();
            }

            // Primero el hogar (depende de la encuesta) y después la encuesta
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM identificador WHERE id_hogar = ?")) {
                ps.setString(1, idHogar);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(
                    "DELETE FROM encuesta WHERE num_encuesta = ?")) {
                ps.setString(1, numEncuesta);
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
    // Auxiliares
    // ------------------------------------------------------------------
    private String obtenerSupervisor(Connection con, String cedulaEncuestador) throws SQLException {
        String sql = "SELECT cedula_supervisor FROM encuestador WHERE cedula = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedulaEncuestador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        throw new SQLException("El encuestador " + cedulaEncuestador
                + " no existe en la tabla ENCUESTADOR. Inserta primero al encuestador.");
    }

    /** Busca el id de region/departamento/municipio por su nombre (el nombre es una constante, no viene del usuario). */
    private String buscarIdPorNombre(Connection con, String tabla, String nombre) throws SQLException {
        String sql = "SELECT id FROM " + tabla + " WHERE LOWER(nombre) = LOWER(?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        throw new SQLException("No existe '" + nombre + "' en la tabla " + tabla.toUpperCase() + ".");
    }

    /** Siguiente id_hogar con formato 001, 002, ... */
    private String siguienteIdHogar(Connection con) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(id_hogar, '^[0-9]+$') "
                + "THEN id_hogar END)), 0) + 1 FROM identificador";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return String.format("%03d", rs.getInt(1));
        }
    }

    /** Siguiente num_encuesta con formato ENC001, ENC002, ... */
    private String siguienteNumEncuesta(Connection con) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(num_encuesta, '^" + PREFIJO_ENCUESTA + "[0-9]+$') "
                + "THEN SUBSTR(num_encuesta, " + (PREFIJO_ENCUESTA.length() + 1) + ") END)), 0) + 1 FROM encuesta";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return PREFIJO_ENCUESTA + String.format("%03d", rs.getInt(1));
        }
    }
}
