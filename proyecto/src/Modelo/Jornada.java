package Modelo;

import java.time.LocalTime;

/**
 * Jornada según la hora. Nadie la digita: se calcula sola.
 *
 *   05:00 a 11:59  -> MAÑANA
 *   12:00 a 17:59  -> TARDE
 *   18:00 a 04:59  -> NOCHE   (incluye la madrugada)
 *
 * La base de datos aplica la misma regla (función fn_jornada y trigger de control_visitas) y es la
 * que decide lo que queda guardado; esta clase solo sirve para mostrar la jornada en pantalla
 * mientras se elige la hora. Si cambia un rango, hay que cambiarlo en los dos lados.
 */
public final class Jornada {

    public static final String MANANA = "MAÑANA";
    public static final String TARDE = "TARDE";
    public static final String NOCHE = "NOCHE";

    private Jornada() {
    }

    /** Jornada que corresponde a una hora (null si no hay hora). */
    public static String de(LocalTime hora) {
        if (hora == null) {
            return null;
        }
        int h = hora.getHour();
        if (h >= 5 && h < 12) {
            return MANANA;
        }
        if (h >= 12 && h < 18) {
            return TARDE;
        }
        return NOCHE;
    }

    /** La base guarda MANANA (sin tilde); en pantalla se muestra MAÑANA. */
    public static String paraMostrar(String valorEnBaseDeDatos) {
        if (valorEnBaseDeDatos == null) {
            return null;
        }
        String v = valorEnBaseDeDatos.trim().toUpperCase();
        return "MANANA".equals(v) ? MANANA : v;
    }
}
