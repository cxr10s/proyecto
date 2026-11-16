package Controladores;

import Modelo.ConexionBD;
import Modelo.EncuestaPendiente;
import Modelo.Errores;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO del supervisor: lista las encuestas pendientes de revisión y da el dictamen.
 * Usa el paquete PL/SQL pkg_auditoria (solo el supervisor puede usarlo).
 *
 * Dictamen (se guarda en el estado de la encuesta):
 *   Completo   -> Aprobado   (la encuesta queda bloqueada para el encuestador)
 *   Incompleto -> Rechazado  (se reabre como "Encuesta incompleta" para que el encuestador la corrija)
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class RevisionDAO {

    // Código de oracle.jdbc.OracleTypes.CURSOR (así no depende de la versión del driver)
    private static final int CURSOR = -10;

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    /**
     * Encuestas completas que esperan revisión, de todos los encuestadores.
     * Los tres filtros son opcionales (null = sin filtro): parte de la cédula del encuestador y
     * rango de la fecha de la última visita.
     */
    public List<EncuestaPendiente> listarPendientes(String cedulaEncuestador, LocalDate desde, LocalDate hasta)
            throws SQLException {
        List<EncuestaPendiente> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_auditoria.listar_pendientes(?, ?, ?, ?)}")) {
            if (cedulaEncuestador == null || cedulaEncuestador.trim().isEmpty()) {
                cs.setNull(1, Types.VARCHAR);
            } else {
                cs.setString(1, cedulaEncuestador.trim());
            }
            setFecha(cs, 2, desde);
            setFecha(cs, 3, hasta);
            cs.registerOutParameter(4, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                while (rs.next()) {
                    EncuestaPendiente e = new EncuestaPendiente();
                    e.setIdHogar(rs.getString("id_hogar"));
                    e.setCedula(rs.getString("cedula"));
                    e.setEncuestador(rs.getString("encuestador"));
                    e.setMunicipio(rs.getString("municipio"));
                    Date f = rs.getDate("ultima_visita");
                    e.setUltimaVisita(f == null ? null : f.toLocalDate());
                    e.setEstado(rs.getString("estado"));
                    lista.add(e);
                }
            }
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
        return lista;
    }

    /**
     * Encuestas ya revisadas y aprobadas (completas), de todos los encuestadores.
     * Filtros opcionales (null = sin filtro): parte de la cédula del encuestador y rango de la
     * fecha de revisión. Solo sirve para consultar.
     */
    public List<EncuestaPendiente> listarRevisadas(String cedulaEncuestador, LocalDate desde, LocalDate hasta)
            throws SQLException {
        List<EncuestaPendiente> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_auditoria.listar_revisadas(?, ?, ?, ?)}")) {
            if (cedulaEncuestador == null || cedulaEncuestador.trim().isEmpty()) {
                cs.setNull(1, Types.VARCHAR);
            } else {
                cs.setString(1, cedulaEncuestador.trim());
            }
            setFecha(cs, 2, desde);
            setFecha(cs, 3, hasta);
            cs.registerOutParameter(4, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                while (rs.next()) {
                    EncuestaPendiente e = new EncuestaPendiente();
                    e.setIdHogar(rs.getString("id_hogar"));
                    e.setCedula(rs.getString("cedula"));
                    e.setEncuestador(rs.getString("encuestador"));
                    e.setMunicipio(rs.getString("municipio"));
                    Date f = rs.getDate("ultima_visita");
                    e.setUltimaVisita(f == null ? null : f.toLocalDate());
                    Date r = rs.getDate("fecha_revision");
                    e.setFechaRevision(r == null ? null : r.toLocalDate());
                    e.setEstado(rs.getString("estado"));
                    lista.add(e);
                }
            }
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
        return lista;
    }

    /** El supervisor abre la encuesta: pasa de "Sin revisar" a "En revision". */
    public void iniciarRevision(String idHogar) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_auditoria.iniciar_revision(?)}")) {
            cs.setString(1, idHogar);
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    /** Da el dictamen: completo = true (Aprobado) o completo = false (Rechazado). */
    public void dictaminar(String idHogar, boolean completo) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_auditoria.dictaminar(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setString(2, completo ? "Aprobado" : "Rechazado");
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    private static void setFecha(CallableStatement cs, int pos, LocalDate f) throws SQLException {
        if (f == null) {
            cs.setNull(pos, Types.DATE);
        } else {
            cs.setDate(pos, Date.valueOf(f));
        }
    }
}
