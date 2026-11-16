package Controladores;

import Modelo.ConexionBD;
import Modelo.ControlVisita;
import Modelo.Errores;
import Modelo.Jornada;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
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
 * Todo el CRUD se hace con el paquete PL/SQL pkg_visita.
 *
 * - id_controlvisitas (CV001...) y las jornadas los calcula el trigger de la base de datos:
 *   la jornada NO se envía, sale de la hora (mañana, tarde o noche).
 * - num_visitas (1, 2, 3...) lo calcula el paquete, por hogar.
 * - Las horas se guardan como texto "HH:mm" (24 horas). Las fechas son DATE.
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class ControlVisitaDAO {

    // Código de oracle.jdbc.OracleTypes.CURSOR (así no depende de la versión del driver)
    private static final int CURSOR = -10;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    // ------------------------------------------------------------------
    // AGREGAR (el número de visita se calcula solo)
    // ------------------------------------------------------------------
    public void agregar(ControlVisita v) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_visita.agregar(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            int i = 1;
            cs.setString(i++, v.getIdHogar());
            setFecha(cs, i++, v.getFecha());
            setTexto(cs, i++, hora(v.getHoraInicio()));
            setTexto(cs, i++, hora(v.getHoraFin()));
            setTexto(cs, i++, v.getResultado());
            setFecha(cs, i++, v.getDiaProxima());
            setTexto(cs, i++, hora(v.getHoraProxima()));
            setTexto(cs, i++, v.getPendiente());
            cs.registerOutParameter(i, Types.VARCHAR);   // 9: número de visita
            cs.execute();
            v.setNumVisita(aEntero(cs.getString(9)));
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana ControlVisitas)
    // ------------------------------------------------------------------
    public List<ControlVisita> listar(String idHogar) throws SQLException {
        List<ControlVisita> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_visita.listar(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.registerOutParameter(2, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                while (rs.next()) {
                    lista.add(mapear(rs, idHogar));
                }
            }
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // BUSCAR (para llenar la ventana de modificar)
    // ------------------------------------------------------------------
    public ControlVisita buscar(String idHogar, int numVisita) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_visita.buscar(?, ?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setString(2, String.valueOf(numVisita));
            cs.registerOutParameter(3, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                return rs.next() ? mapear(rs, idHogar) : null;
            }
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // MODIFICAR (la visita se identifica por idHogar + numVisita)
    // ------------------------------------------------------------------
    public void modificar(ControlVisita v) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_visita.modificar(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {
            int i = 1;
            cs.setString(i++, v.getIdHogar());
            cs.setString(i++, String.valueOf(v.getNumVisita()));
            setFecha(cs, i++, v.getFecha());
            setTexto(cs, i++, hora(v.getHoraInicio()));
            setTexto(cs, i++, hora(v.getHoraFin()));
            setTexto(cs, i++, v.getResultado());
            setFecha(cs, i++, v.getDiaProxima());
            setTexto(cs, i++, hora(v.getHoraProxima()));
            setTexto(cs, i, v.getPendiente());
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, int numVisita) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_visita.eliminar(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setString(2, String.valueOf(numVisita));
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------
    private ControlVisita mapear(ResultSet rs, String idHogar) throws SQLException {
        ControlVisita v = new ControlVisita();
        v.setIdHogar(idHogar);
        v.setNumVisita(aEntero(rs.getString("num_visitas")));
        Date f = rs.getDate("fecha_visita");
        v.setFecha(f == null ? null : f.toLocalDate());
        v.setHoraInicio(aHora(rs.getString("inicio_visita")));
        v.setJornadaInicio(Jornada.paraMostrar(rs.getString("inicio_jornada")));
        v.setHoraFin(aHora(rs.getString("finalizacion_visita")));
        v.setJornadaFin(Jornada.paraMostrar(rs.getString("finalizacion_jornada")));
        v.setResultado(rs.getString("resultado_entrevista"));
        Date d = rs.getDate("dia_prox_visita");
        v.setDiaProxima(d == null ? null : d.toLocalDate());
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

    private static String hora(LocalTime t) {
        return t == null ? null : t.format(HORA);
    }

    private static void setFecha(CallableStatement cs, int pos, LocalDate f) throws SQLException {
        if (f == null) {
            cs.setNull(pos, Types.DATE);
        } else {
            cs.setDate(pos, Date.valueOf(f));
        }
    }

    private static void setTexto(CallableStatement cs, int pos, String s) throws SQLException {
        if (s == null || s.isEmpty()) {
            cs.setNull(pos, Types.VARCHAR);
        } else {
            cs.setString(pos, s);
        }
    }
}
