package Controladores;

import Modelo.ConexionBD;
import Modelo.Errores;
import Modelo.ResultadoReporte;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Los 5 reportes del supervisor. Cada uno sale de la base de datos con un procedimiento del
 * paquete PL/SQL pkg_reporte (reporte1 ... reporte5); aquí solo se leen y se describen
 * (títulos y columnas). Solo el supervisor puede pedirlos.
 *
 * Este DAO no cierra la conexión (es la del Singleton).
 */
public class ReporteDAO {

    // Código de oracle.jdbc.OracleTypes.CURSOR (así no depende de la versión del driver)
    private static final int CURSOR = -10;

    /** Nombres que se muestran en los botones de la ventana Reporte. */
    public static final String[] TITULOS = {
        "Encuestas por encuestador y estado",
        "Resultado del cuadernillo",
        "Gasto por hogar",
        "Visitas por encuestador y jornada",
        "Gasto por lugar de compra"
    };

    private static final String[] DESCRIPCIONES = {
        "Cuántas encuestas tiene cada encuestador, cuántas están completas y en qué estado de revisión están.",
        "Cuántas encuestas hay por cada resultado del cuadernillo, con su porcentaje sobre el total.",
        "Lo gastado en cada hogar en gastos diarios y en comidas preparadas fuera del hogar (de mayor a menor).",
        "Visitas de cada encuestador según la jornada en que empezaron (la jornada sale de la hora de inicio).",
        "Lo gastado en cada lugar de compra, sumando gastos diarios y comidas fuera del hogar."
    };

    private static final String[][] COLUMNAS = {
        {"Cédula", "Encuestador", "Total encuestas", "Completas", "No completas", "Sin revisar",
            "En revisión", "Aprobadas", "Rechazadas"},
        {"Resultado del cuadernillo", "Cantidad", "Porcentaje"},
        {"Hogar", "Encuestador", "Estado", "Gastos registrados", "Total gastos", "Comidas registradas",
            "Total comidas", "Total general"},
        {"Cédula", "Encuestador", "Visitas", "Mañana", "Tarde", "Noche", "Hogares visitados",
            "Promedio por hogar"},
        {"Lugar de compra", "Registros", "Total gastado", "Porcentaje"}
    };

    private static final String[] TIPOS = {"TTNNNNNNN", "TNP", "TTTNMNMM", "TTNNNNND", "TNMP"};

    private static final String[] SUMAR = {"FFTTTTTTT", "FTF", "FFFTTTTT", "FFTTTTTF", "FTTF"};

    private Connection conectar() throws SQLException {
        Connection con = ConexionBD.getInstancia().getConnection();
        if (con == null) {
            throw new SQLException("No se pudo establecer la conexión con la base de datos.");
        }
        return con;
    }

    /** Ejecuta el reporte (1 a 5) y devuelve sus filas. */
    public ResultadoReporte generar(int numero) throws SQLException {
        if (numero < 1 || numero > 5) {
            throw new IllegalArgumentException("Los reportes van del 1 al 5.");
        }
        int i = numero - 1;
        boolean[] sumar = new boolean[SUMAR[i].length()];
        for (int k = 0; k < sumar.length; k++) {
            sumar[k] = SUMAR[i].charAt(k) == 'T';
        }
        ResultadoReporte r = new ResultadoReporte(numero, TITULOS[i], DESCRIPCIONES[i], COLUMNAS[i],
                TIPOS[i].toCharArray(), sumar);

        Connection con = conectar();
        try (CallableStatement cs = con.prepareCall("{call pkg_reporte.reporte" + numero + "(?)}")) {
            cs.registerOutParameter(1, CURSOR);
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                int columnas = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int c = 0; c < columnas; c++) {
                        fila[c] = rs.getObject(c + 1);
                    }
                    r.getFilas().add(fila);
                }
            }
        } catch (SQLException e) {
            throw Errores.traducir(e);
        }
        return r;
    }
}
