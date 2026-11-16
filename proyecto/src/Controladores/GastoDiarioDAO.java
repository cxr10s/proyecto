package Controladores;

import Modelo.ConexionBD;
import Modelo.Errores;
import Modelo.GastoDiario;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de la tabla GASTOS_DIARIOS_PERSONALES. Todo el CRUD se hace con el paquete PL/SQL pkg_gasto.
 *
 * - id_gastos_diarios (GD001...) y total_renglones los calculan los triggers de la base de datos.
 * - numero_orden (1, 2, 3...) lo calcula el paquete, por hogar.
 * - dia_semana se calcula solo a partir de la fecha.
 * - La unidad de medida, la forma de adquisición, el lugar de compra y la frecuencia son listas
 *   que viven en la ventana: la base de datos guarda el TEXTO de la opción elegida.
 *   (Los campos id* de GastoDiario llevan ese texto; los nombre* llevan el mismo.)
 *
 * Los métodos son los mismos de antes: GastosDiarios y AgregarDatosGastosDiarios no cambian.
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class GastoDiarioDAO {

    // Código de oracle.jdbc.OracleTypes.CURSOR (así no depende de la versión del driver)
    private static final int CURSOR = -10;

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    // ------------------------------------------------------------------
    // AGREGAR (el número de orden se calcula solo)
    // ------------------------------------------------------------------
    public void agregar(GastoDiario g) throws SQLException {
        Connection con = conectar();
        String sql = "{call pkg_gasto.agregar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = con.prepareCall(sql)) {
            int i = 1;
            cs.setString(i++, g.getIdHogar());
            cs.setDate(i++, Date.valueOf(g.getFecha()));
            cs.setString(i++, g.getNombreArticulo());
            cs.setInt(i++, g.getCantidad());
            cs.setString(i++, g.getIdMedida());
            cs.setString(i++, g.getEquivalencia());
            cs.setString(i++, g.getIdFormaAdquisicion());
            cs.setString(i++, g.getIdLugarCompra());
            cs.setLong(i++, g.getCosto());
            cs.setString(i++, g.getIdFrecuencia());
            cs.setString(i++, g.getTipoGasto());
            cs.registerOutParameter(i, Types.NUMERIC);   // 12: número de orden
            cs.execute();
            g.setNumOrden(cs.getInt(12));
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana GastosDiarios)
    // ------------------------------------------------------------------
    public List<GastoDiario> listar(String idHogar) throws SQLException {
        List<GastoDiario> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_gasto.listar(?, ?)}")) {
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
    public GastoDiario buscar(String idHogar, int numOrden) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_gasto.buscar(?, ?, ?)}")) {
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
    // MODIFICAR (el gasto se identifica por idHogar + numOrden)
    // ------------------------------------------------------------------
    public void modificar(GastoDiario g) throws SQLException {
        Connection con = conectar();
        String sql = "{call pkg_gasto.modificar(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cs = con.prepareCall(sql)) {
            int i = 1;
            cs.setString(i++, g.getIdHogar());
            cs.setInt(i++, g.getNumOrden());
            cs.setDate(i++, Date.valueOf(g.getFecha()));
            cs.setString(i++, g.getNombreArticulo());
            cs.setInt(i++, g.getCantidad());
            cs.setString(i++, g.getIdMedida());
            cs.setString(i++, g.getEquivalencia());
            cs.setString(i++, g.getIdFormaAdquisicion());
            cs.setString(i++, g.getIdLugarCompra());
            cs.setLong(i++, g.getCosto());
            cs.setString(i++, g.getIdFrecuencia());
            cs.setString(i, g.getTipoGasto());
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR (el total de renglones se recalcula solo)
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, int numOrden) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_gasto.eliminar(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setInt(2, numOrden);
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    private GastoDiario mapear(ResultSet rs, String idHogar) throws SQLException {
        GastoDiario g = new GastoDiario();
        g.setIdHogar(idHogar);
        g.setNumOrden(rs.getInt("numero_orden"));
        Date f = rs.getDate("fecha");
        g.setFecha(f == null ? null : f.toLocalDate());
        g.setNombreArticulo(rs.getString("nombre_articulo"));
        g.setCantidad(rs.getInt("cantidad"));
        // La base guarda el texto de la opción elegida en la ventana; los campos id* y nombre* llevan el mismo texto
        String medida = rs.getString("unidad_medida");
        g.setIdMedida(medida);
        g.setNombreMedida(medida);
        g.setEquivalencia(rs.getString("equivalencia"));
        String forma = rs.getString("forma_adquisicion");
        g.setIdFormaAdquisicion(forma);
        g.setNombreForma(forma);
        String lugar = rs.getString("lugar_compra");
        g.setIdLugarCompra(lugar);
        g.setNombreLugar(lugar);
        g.setCosto(rs.getLong("costo"));
        String frecuencia = rs.getString("frecuencia_compra");
        g.setIdFrecuencia(frecuencia);
        g.setNombreFrecuencia(frecuencia);
        g.setTipoGasto(rs.getString("gastos_hogar_o_personal"));
        return g;
    }
}
