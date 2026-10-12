package Modelo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla CONTROL_VISITAS (una fila = una visita a un hogar).
 *
 * - id_controlvisitas: automático (CV001, CV002, ...).
 * - num_visitas: automático por hogar (1, 2, 3, ...).
 * - num_encuesta: aquí se guarda el id_hogar (así está la FK en tu base).
 * - fecha_visita es DATE; las horas se guardan como texto "HH:mm" y
 *   dia_prox_visita como texto "dd/MM/yyyy" (esa columna es VARCHAR).
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class ControlVisitaDAO {

    private static final String PREFIJO_ID = "CV";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String COLUMNAS = "num_encuesta, num_visitas, fecha_visita, inicio_visita, "
            + "inicio_jornada, finalizacion_visita, finalizacion_jornada, resultado_entrevista, "
            + "dia_prox_visita, hora_prox_visita, partes_diligenciar";

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    // ------------------------------------------------------------------
    // AGREGAR (id y número de visita se calculan solos)
    // ------------------------------------------------------------------
    public void agregar(ControlVisita v) throws SQLException {
        Connection con = conectar();
        String idControl = siguienteIdControl(con);
        int numVisita = siguienteNumVisita(con, v.getIdHogar());
        String sql = "INSERT INTO control_visitas (id_controlvisitas, " + COLUMNAS + ") "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, idControl);
            ps.setString(i++, v.getIdHogar());
            ps.setString(i++, String.valueOf(numVisita));
            setFecha(ps, i++, v.getFecha());
            setTexto(ps, i++, hora(v.getHoraInicio()));
            setTexto(ps, i++, v.getJornadaInicio());
            setTexto(ps, i++, hora(v.getHoraFin()));
            setTexto(ps, i++, v.getJornadaFin());
            setTexto(ps, i++, v.getResultado());
            setTexto(ps, i++, dia(v.getDiaProxima()));
            setTexto(ps, i++, hora(v.getHoraProxima()));
            setTexto(ps, i++, v.getPendiente());
            ps.executeUpdate();
        }
        v.setNumVisita(numVisita);
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana ControlVisitas)
    // ------------------------------------------------------------------
    public List<ControlVisita> listar(String idHogar) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM control_visitas "
                + "WHERE num_encuesta = ? ORDER BY TO_NUMBER(num_visitas)";
        List<ControlVisita> lista = new ArrayList<>();
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // BUSCAR (para llenar la ventana de modificar)
    // ------------------------------------------------------------------
    public ControlVisita buscar(String idHogar, int numVisita) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM control_visitas "
                + "WHERE num_encuesta = ? AND num_visitas = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            ps.setString(2, String.valueOf(numVisita));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    // ------------------------------------------------------------------
    // MODIFICAR (la visita se identifica por idHogar + numVisita)
    // ------------------------------------------------------------------
    public void modificar(ControlVisita v) throws SQLException {
        String sql = "UPDATE control_visitas SET fecha_visita = ?, inicio_visita = ?, inicio_jornada = ?, "
                + "finalizacion_visita = ?, finalizacion_jornada = ?, resultado_entrevista = ?, "
                + "dia_prox_visita = ?, hora_prox_visita = ?, partes_diligenciar = ? "
                + "WHERE num_encuesta = ? AND num_visitas = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int i = 1;
            setFecha(ps, i++, v.getFecha());
            setTexto(ps, i++, hora(v.getHoraInicio()));
            setTexto(ps, i++, v.getJornadaInicio());
            setTexto(ps, i++, hora(v.getHoraFin()));
            setTexto(ps, i++, v.getJornadaFin());
            setTexto(ps, i++, v.getResultado());
            setTexto(ps, i++, dia(v.getDiaProxima()));
            setTexto(ps, i++, hora(v.getHoraProxima()));
            setTexto(ps, i++, v.getPendiente());
            ps.setString(i++, v.getIdHogar());
            ps.setString(i++, String.valueOf(v.getNumVisita()));
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la visita " + v.getNumVisita() + " para modificar.");
            }
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, int numVisita) throws SQLException {
        String sql = "DELETE FROM control_visitas WHERE num_encuesta = ? AND num_visitas = ?";
        Connection con = conectar();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            ps.setString(2, String.valueOf(numVisita));
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la visita " + numVisita + ".");
            }
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------
    /** Siguiente id_controlvisitas con formato CV001, CV002, ... */
    private String siguienteIdControl(Connection con) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(id_controlvisitas, '^" + PREFIJO_ID + "[0-9]+$') "
                + "THEN SUBSTR(id_controlvisitas, " + (PREFIJO_ID.length() + 1) + ") END)), 0) + 1 FROM control_visitas";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return PREFIJO_ID + String.format("%03d", rs.getInt(1));
        }
    }

    /** Siguiente número de visita de ese hogar: 1, 2, 3, ... */
    private int siguienteNumVisita(Connection con, String idHogar) throws SQLException {
        String sql = "SELECT NVL(MAX(TO_NUMBER(CASE WHEN REGEXP_LIKE(num_visitas, '^[0-9]+$') "
                + "THEN num_visitas END)), 0) + 1 FROM control_visitas WHERE num_encuesta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, idHogar);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private ControlVisita mapear(ResultSet rs) throws SQLException {
        ControlVisita v = new ControlVisita();
        v.setIdHogar(rs.getString("num_encuesta"));
        v.setNumVisita(aEntero(rs.getString("num_visitas")));
        Date f = rs.getDate("fecha_visita");
        v.setFecha(f == null ? null : f.toLocalDate());
        v.setHoraInicio(aHora(rs.getString("inicio_visita")));
        v.setJornadaInicio(rs.getString("inicio_jornada"));
        v.setHoraFin(aHora(rs.getString("finalizacion_visita")));
        v.setJornadaFin(rs.getString("finalizacion_jornada"));
        v.setResultado(rs.getString("resultado_entrevista"));
        v.setDiaProxima(aDia(rs.getString("dia_prox_visita")));
        v.setHoraProxima(aHora(rs.getString("hora_prox_visita")));
        v.setPendiente(rs.getString("partes_diligenciar"));
        return v;
    }

    private static int aEntero(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (RuntimeException e) {
            return 0;
        }
    }

    private static LocalTime aHora(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(s.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDate aDia(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s.trim(), DIA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String hora(LocalTime t) {
        return t == null ? null : t.format(HORA);
    }

    private static String dia(LocalDate d) {
        return d == null ? null : d.format(DIA);
    }

    private static void setFecha(PreparedStatement ps, int pos, LocalDate f) throws SQLException {
        if (f == null) {
            ps.setNull(pos, Types.DATE);
        } else {
            ps.setDate(pos, Date.valueOf(f));
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
