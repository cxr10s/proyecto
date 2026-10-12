package Modelo;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.text.Normalizer;
import javax.swing.JLabel;

/**
 * Encuestador con sesión iniciada (se llena en el Login).
 *
 * Además, mientras haya sesión, cada ventana que se abre cambia los textos fijos de su encabezado
 * (nombre y cédula que están escritos a mano en el diseño) por los del encuestador que inició sesión.
 */
public final class Sesion {

    // Textos fijos que tienen los diseños de las ventanas en el encabezado
    private static final String NOMBRE_DISENO = "MATEO ALEJANDRO RODRIGUEZ MARTINEZ";
    private static final String CEDULA_DISENO = "1015162895";

    private static Encuestador actual;
    private static boolean escuchando = false;

    private Sesion() {
    }

    public static void iniciar(Encuestador encuestador) {
        actual = encuestador;
        instalarEncabezado();
    }

    public static Encuestador getEncuestador() {
        return actual;
    }

    /** Cédula del encuestador con sesión, o null si nadie ha iniciado sesión. */
    public static String getCedula() {
        return actual == null ? null : actual.getCedula();
    }

    // ------------------------------------------------------------------
    // Encabezado de las ventanas
    // ------------------------------------------------------------------
    private static void instalarEncabezado() {
        if (escuchando) {
            return;
        }
        escuchando = true;
        Toolkit.getDefaultToolkit().addAWTEventListener(evento -> {
            if (evento.getID() == WindowEvent.WINDOW_OPENED) {
                Window ventana = ((WindowEvent) evento).getWindow();
                actualizarEncabezado(ventana);
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    /** Reemplaza el nombre y la cédula fijos del diseño por los del encuestador con sesión. */
    public static void actualizarEncabezado(Container contenedor) {
        if (actual == null) {
            return;
        }
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JLabel) {
                JLabel etiqueta = (JLabel) c;
                String texto = etiqueta.getText();
                if (texto != null) {
                    if (sinTildes(texto).equals(NOMBRE_DISENO)) {
                        etiqueta.setText(actual.getNombreCompleto().toUpperCase());
                    } else if (texto.trim().equals(CEDULA_DISENO)) {
                        etiqueta.setText(actual.getCedula());
                    }
                }
            }
            if (c instanceof Container) {
                actualizarEncabezado((Container) c);
            }
        }
    }

    private static String sinTildes(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").trim().toUpperCase();
    }
}
