package Modelo;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convierte los errores de Oracle en mensajes legibles.
 *
 * Los RAISE_APPLICATION_ERROR de los paquetes y triggers llegan a Java como
 * "ORA-20003: ACCESO DENEGADO: ...\nORA-06512: en ...". Aquí se deja solo el texto útil.
 */
public final class Errores {

    private static final Pattern NEGOCIO = Pattern.compile("ORA-20\\d{3}: ([^\\n]*)");

    private Errores() {
    }

    public static SQLException traducir(SQLException e) {
        String mensaje = e.getMessage();
        if (mensaje == null) {
            return e;
        }
        Matcher m = NEGOCIO.matcher(mensaje);
        if (m.find()) {
            return new SQLException(m.group(1).trim(), e.getSQLState(), e.getErrorCode(), e);
        }
        return e;
    }
}
