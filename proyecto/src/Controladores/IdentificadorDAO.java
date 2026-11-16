package Controladores;

import Modelo.ConexionBD;
import Modelo.Errores;
import Modelo.Identificador;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla IDENTIFICADOR. Todo el CRUD se hace con el paquete PL/SQL pkg_hogar.
 *
 * Al agregar un hogar, el paquete crea primero su ENCUESTA (num_encuesta automático) y luego
 * el IDENTIFICADOR (id_hogar automático): si algo falla, Oracle no guarda nada.
 * Quién puede hacer qué lo controlan los triggers de la base de datos.
 */
public class IdentificadorDAO {

    // Código de oracle.jdbc.OracleTypes.CURSOR (así no depende de la versión del driver)
    private static final int CURSOR = -10;

    // Ubicación fija de esta versión: la aplicación trabaja solo en Bucaramanga, Santander.
    // Las vistas leen estas constantes; en la base el municipio se busca por su nombre.
    public static final String REGION = "Oriental";
    public static final String DEPARTAMENTO = "Santander";
    public static final String MUNICIPIO = "Bucaramanga";

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
        String sql = "{call pkg_hogar.agregar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall(sql)) {
            int i = 1;
            cs.setString(i++, MUNICIPIO);
            cs.setString(i++, h.getClase());
            cs.setString(i++, h.getSector());
            cs.setString(i++, h.getSeccion());
            cs.setString(i++, h.getManzana());
            cs.setString(i++, h.getSegmento());
            cs.setString(i++, h.getCiclo());
            cs.setString(i++, h.getNumEdificacion());
            cs.setString(i++, h.getNumVivienda());
            cs.setString(i++, h.getTotalHogares());
            cs.setString(i++, h.getNumHogar());
            cs.setString(i++, h.getTotalPersonas());
            cs.setString(i++, h.getBarrioCentroVereda());
            cs.setString(i++, h.getDireccion());
            cs.setString(i++, h.getTelefono());
            cs.registerOutParameter(i++, Types.VARCHAR);   // 16: id_hogar
            cs.registerOutParameter(i++, Types.VARCHAR);   // 17: num_encuesta
            cs.execute();

            h.setIdHogar(cs.getString(16));
            h.setNumEncuesta(cs.getString(17));
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana Inicio)
    // El encuestador ve sus hogares; el supervisor, todos.
    // ------------------------------------------------------------------
    public List<Identificador> listar() throws SQLException {
        List<Identificador> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_hogar.listar(?)}")) {
            cs.registerOutParameter(1, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) {
                    Identificador h = new Identificador();
                    h.setIdHogar(rs.getString("id_hogar"));
                    h.setMunicipio(rs.getString("municipio"));
                    h.setEstado(rs.getString("estado"));
                    h.setValidacion(rs.getString("resultado_encuesta"));
                    lista.add(h);
                }
            }
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
        return lista;
    }

    // ------------------------------------------------------------------
    // BUSCAR POR ID (para llenar la ventana de modificar)
    // ------------------------------------------------------------------
    public Identificador buscarPorId(String idHogar) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_hogar.buscar_por_id(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.registerOutParameter(2, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(2)) {
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
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // RESULTADO DEL CUADERNILLO (se guarda en ENCUESTA.resultado_encuesta;
    // es lo que Inicio muestra en la columna VALIDACION)
    // ------------------------------------------------------------------
    public String obtenerResultado(String idHogar) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_hogar.obtener_resultado(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            return cs.getString(2);
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    public void actualizarResultado(String idHogar, String resultado) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_hogar.actualizar_resultado(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setString(2, resultado);
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // MODIFICAR (todo menos municipio, id_hogar y num_encuesta)
    // ------------------------------------------------------------------
    public void modificar(Identificador h) throws SQLException {
        String sql = "{call pkg_hogar.modificar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall(sql)) {
            int i = 1;
            cs.setString(i++, h.getIdHogar());
            cs.setString(i++, h.getClase());
            cs.setString(i++, h.getSector());
            cs.setString(i++, h.getSeccion());
            cs.setString(i++, h.getManzana());
            cs.setString(i++, h.getSegmento());
            cs.setString(i++, h.getCiclo());
            cs.setString(i++, h.getNumEdificacion());
            cs.setString(i++, h.getNumVivienda());
            cs.setString(i++, h.getTotalHogares());
            cs.setString(i++, h.getNumHogar());
            cs.setString(i++, h.getTotalPersonas());
            cs.setString(i++, h.getBarrioCentroVereda());
            cs.setString(i++, h.getDireccion());
            cs.setString(i++, h.getTelefono());
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR (borra el hogar, lo que cuelga de su encuesta y la encuesta, en un solo paso)
    // ------------------------------------------------------------------
    public void eliminar(String idHogar) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_hogar.eliminar(?)}")) {
            cs.setString(1, idHogar);
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }
}
