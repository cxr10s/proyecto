package Controladores;

import Modelo.ComidaFuera;
import Modelo.ConexionBD;
import Modelo.Errores;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla COMIDAS_FUERA_HOGAR. Todo el CRUD se hace con el paquete PL/SQL pkg_comida.
 *
 * - id_comidas_fuera_hogar (CF001...) lo genera un trigger de la base de datos.
 * - num_orden (1, 2, 3...) lo calcula el paquete, por hogar.
 * - num_encuesta: el paquete lo busca a partir del hogar.
 * - Tipo de comida: la base guarda el texto (Desayuno, Almuerzo, Cena u Onces); la aplicación
 *   sigue usando los códigos D, A, C y O de ComidaFuera.
 * - Domicilio: "X" si lo adquirió a domicilio; vacío si no.
 * - La forma de adquisición, el lugar de compra y la frecuencia son listas que viven en la ventana:
 *   la base guarda el TEXTO de la opción elegida (los campos id* de ComidaFuera llevan ese texto
 *   y los nombre* llevan el mismo).
 *
 * Los métodos son los mismos de antes. Este DAO no cierra la conexión (es la del Singleton).
 */
public class ComidaFueraDAO {

    // Código de oracle.jdbc.OracleTypes.CURSOR (así no depende de la versión del driver)
    private static final int CURSOR = -10;
    private static final String MARCA = "X";

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
        String sql = "{call pkg_comida.agregar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = con.prepareCall(sql)) {
            int i = 1;
            cs.setString(i++, c.getIdHogar());
            cs.setString(i++, c.getDescripcion());
            cs.setString(i++, textoTipo(c.getTipoComida()));
            cs.setInt(i++, c.getCantidad());
            cs.setString(i++, c.getIdFormaAdquisicion());
            cs.setString(i++, c.getIdLugarCompra());
            cs.setLong(i++, c.getCosto());
            cs.setString(i++, c.getIdFrecuencia());
            cs.setString(i++, c.getTipoGasto());
            setMarca(cs, i++, c.isDomicilio());
            cs.registerOutParameter(i, Types.NUMERIC);   // 11: número de orden
            cs.execute();
            c.setNumOrden(cs.getInt(11));
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana ComidasPreparadasFueraDelHogar)
    // ------------------------------------------------------------------
    public List<ComidaFuera> listar(String idHogar) throws SQLException {
        List<ComidaFuera> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_comida.listar(?, ?)}")) {
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
    public ComidaFuera buscar(String idHogar, int numOrden) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_comida.buscar(?, ?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setInt(2, numOrden);
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
    // MODIFICAR (la comida se identifica por idHogar + numOrden)
    // ------------------------------------------------------------------
    public void modificar(ComidaFuera c) throws SQLException {
        Connection con = conectar();
        String sql = "{call pkg_comida.modificar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = con.prepareCall(sql)) {
            int i = 1;
            cs.setString(i++, c.getIdHogar());
            cs.setInt(i++, c.getNumOrden());
            cs.setString(i++, c.getDescripcion());
            cs.setString(i++, textoTipo(c.getTipoComida()));
            cs.setInt(i++, c.getCantidad());
            cs.setString(i++, c.getIdFormaAdquisicion());
            cs.setString(i++, c.getIdLugarCompra());
            cs.setLong(i++, c.getCosto());
            cs.setString(i++, c.getIdFrecuencia());
            cs.setString(i++, c.getTipoGasto());
            setMarca(cs, i, c.isDomicilio());
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, int numOrden) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_comida.eliminar(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setInt(2, numOrden);
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------
    private ComidaFuera mapear(ResultSet rs, String idHogar) throws SQLException {
        ComidaFuera c = new ComidaFuera();
        c.setIdHogar(idHogar);
        c.setNumOrden(rs.getInt("num_orden"));
        c.setDescripcion(rs.getString("descripcion_alimento"));
        c.setTipoComida(codigoTipo(rs.getString("tipo_comida")));
        c.setCantidad(rs.getInt("cantidad"));
        String forma = rs.getString("forma_adquisicion");
        c.setIdFormaAdquisicion(forma);
        c.setNombreForma(forma);
        String lugar = rs.getString("lugar_compra");
        c.setIdLugarCompra(lugar);
        c.setNombreLugar(lugar);
        c.setCosto(rs.getLong("costo"));
        String frecuencia = rs.getString("frecuencia_compra");
        c.setIdFrecuencia(frecuencia);
        c.setNombreFrecuencia(frecuencia);
        c.setTipoGasto(rs.getString("gasto_hogar_o_personal"));
        String dom = rs.getString("domicilio");
        c.setDomicilio(dom != null && !dom.trim().isEmpty());
        return c;
    }

    /** Código de la aplicación (D, A, C, O) -> texto que se guarda en la base de datos. */
    private static String textoTipo(String codigo) {
        if (ComidaFuera.DESAYUNO.equals(codigo)) {
            return "Desayuno";
        }
        if (ComidaFuera.ALMUERZO.equals(codigo)) {
            return "Almuerzo";
        }
        if (ComidaFuera.CENA.equals(codigo)) {
            return "Cena";
        }
        if (ComidaFuera.ONCES.equals(codigo)) {
            return "Onces";
        }
        return null;
    }

    /** Texto guardado en la base de datos -> código de la aplicación (D, A, C, O). */
    private static String codigoTipo(String texto) {
        if (texto == null) {
            return null;
        }
        switch (texto.trim().toLowerCase()) {
            case "desayuno": return ComidaFuera.DESAYUNO;
            case "almuerzo": return ComidaFuera.ALMUERZO;
            case "cena":     return ComidaFuera.CENA;
            case "onces":    return ComidaFuera.ONCES;
            default:         return null;
        }
    }

    /** Pone "X" si está marcado o NULL si no. */
    private static void setMarca(CallableStatement cs, int pos, boolean marcado) throws SQLException {
        if (marcado) {
            cs.setString(pos, MARCA);
        } else {
            cs.setNull(pos, Types.VARCHAR);
        }
    }
}
