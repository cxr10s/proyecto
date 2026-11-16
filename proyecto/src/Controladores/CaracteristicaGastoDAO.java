package Controladores;

import Modelo.CaracteristicaGasto;
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
 * DAO de la tabla CARACTERISTICAS_GASTOS. Todo el CRUD se hace con el paquete PL/SQL pkg_caracteristica.
 *
 * - id_caracteristicas: automático (CG001, CG002, ...), lo genera un trigger de la base de datos.
 * - alimento: código del grupo ("01" ... "25"); nombre_alimento: su nombre (máx. 100 caracteres).
 * - Un alimento solo puede registrarse una vez por hogar (para cambiarlo se usa MODIFICAR).
 * - La frecuencia se guarda como TEXTO en una sola columna (frec_gastos): "Nunca", "Diario",
 *   "Semanal", ... Es el mismo texto de CaracteristicaGasto.FREC_NOMBRES.
 * - val_ultima_compra y comprara_de_nuevo solo se preguntan si la frecuencia es mensual o mayor;
 *   si no aplica, comprara_de_nuevo queda en "NA" y el valor en NULL.
 *
 * Los métodos son los mismos de antes: las ventanas no cambian por esto.
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class CaracteristicaGastoDAO {

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
    // AGREGAR
    // ------------------------------------------------------------------
    public void agregar(CaracteristicaGasto g) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_caracteristica.agregar(?, ?, ?, ?, ?, ?, ?)}")) {
            int i = 1;
            cs.setString(i++, g.getIdHogar());
            cs.setString(i++, g.getCodigoAlimento());
            cs.setString(i++, recortar(g.getNombreAlimento(), 100));
            cs.setString(i++, textoFrecuencia(g.getFrecuencia()));
            setValor(cs, i++, g.getValorUltimaCompra());
            cs.setString(i++, g.getComprara());
            setTexto(cs, i, g.getObservaciones());
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // LISTAR (tabla de la ventana CaracteristicasDelGastoHogar)
    // ------------------------------------------------------------------
    public List<CaracteristicaGasto> listar(String idHogar) throws SQLException {
        List<CaracteristicaGasto> lista = new ArrayList<>();
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_caracteristica.listar(?, ?)}")) {
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
    public CaracteristicaGasto buscar(String idHogar, String codigoAlimento) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_caracteristica.buscar(?, ?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setString(2, codigoAlimento);
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
    // MODIFICAR (el registro se identifica por idHogar + codigoAlimento)
    // ------------------------------------------------------------------
    public void modificar(CaracteristicaGasto g) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_caracteristica.modificar(?, ?, ?, ?, ?, ?)}")) {
            int i = 1;
            cs.setString(i++, g.getIdHogar());
            cs.setString(i++, g.getCodigoAlimento());
            cs.setString(i++, textoFrecuencia(g.getFrecuencia()));
            setValor(cs, i++, g.getValorUltimaCompra());
            cs.setString(i++, g.getComprara());
            setTexto(cs, i, g.getObservaciones());
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR
    // ------------------------------------------------------------------
    public void eliminar(String idHogar, String codigoAlimento) throws SQLException {
        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_caracteristica.eliminar(?, ?)}")) {
            cs.setString(1, idHogar);
            cs.setString(2, codigoAlimento);
            cs.execute();
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
    }

    // ------------------------------------------------------------------
    // Auxiliares
    // ------------------------------------------------------------------
    private CaracteristicaGasto mapear(ResultSet rs, String idHogar) throws SQLException {
        CaracteristicaGasto g = new CaracteristicaGasto();
        g.setIdHogar(idHogar);
        g.setCodigoAlimento(rs.getString("alimento"));
        g.setNombreAlimento(rs.getString("nombre_alimento"));
        g.setFrecuencia(indiceFrecuencia(rs.getString("frec_gastos")));
        long valor = rs.getLong("val_ultima_compra");
        g.setValorUltimaCompra(rs.wasNull() ? null : valor);
        g.setComprara(rs.getString("comprara_de_nuevo"));
        g.setObservaciones(rs.getString("observaciones"));
        return g;
    }

    /** Texto que se guarda en la base de datos para el índice de frecuencia del formulario. */
    private static String textoFrecuencia(int indice) {
        if (indice < 0 || indice >= CaracteristicaGasto.FREC_NOMBRES.length) {
            return null;
        }
        return CaracteristicaGasto.FREC_NOMBRES[indice];
    }

    /** Índice de frecuencia (0 = Nunca ... 8 = Esporádico) para el texto guardado; -1 si no coincide. */
    private static int indiceFrecuencia(String texto) {
        if (texto == null) {
            return -1;
        }
        String buscado = texto.trim();
        for (int i = 0; i < CaracteristicaGasto.FREC_NOMBRES.length; i++) {
            if (CaracteristicaGasto.FREC_NOMBRES[i].equalsIgnoreCase(buscado)) {
                return i;
            }
        }
        return -1;
    }

    private static String recortar(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max) : s;
    }

    private static void setValor(CallableStatement cs, int pos, Long valor) throws SQLException {
        if (valor == null) {
            cs.setNull(pos, Types.NUMERIC);
        } else {
            cs.setLong(pos, valor);
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
