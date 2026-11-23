package Modelo;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.Normalizer;
import javax.swing.JLabel;

/**
 * Usuario con sesión iniciada (se llena en el Login).
 *
 * - En la base de datos la sesión vive en el paquete pkg_sesion (la abre pkg_login.autenticar).
 *   Si la conexión se reabre, ConexionBD llama a restaurar() para abrirla otra vez.
 * - Mientras haya sesión, cada ventana que se abre cambia los textos fijos de su encabezado
 *   (nombre y cédula escritos a mano en el diseño) por los del usuario que inició sesión.
 */
public final class Sesion {

    // Textos fijos que traen los diseños de las ventanas (encuestador y supervisor)
    private static final String[] NOMBRES_DISENO = {
        "MATEO ALEJANDRO RODRIGUEZ MARTINEZ",
        "JUAN DIEGO GUTIERREZ CASTRO",
        "LAURA FERNANDA GOMEZ SALAZAR",
        "ANDRES FELIPE ROJAS CASTELLANOS",
        "DIANA CAROLINA PEREZ VARGAS",
        "JUAN SEBASTIAN TORRES MORALES"
    };
    private static final String[] CEDULAS_DISENO = {
        "1015162895",
        "1026529849",
        "1095842310",
        "1098765432",
        "1097412589",
        "1096325874"
    };

    private static Usuario actual;
    private static boolean escuchando = false;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        actual = usuario;
        instalarEncabezado();
    }

    /** Cierra la sesión en Java y en la base de datos. */
    public static void cerrar() {
        if (actual != null) {
            try {
                Connection con = ConexionBD.getInstancia().getConnection();
                if (con != null) {
                    try (CallableStatement cs = con.prepareCall("{call pkg_login.cerrar}")) {
                        cs.execute();
                    }
                }
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar la sesión en la base de datos: " + e.getMessage());
            }
        }
        actual = null;
    }

    /** La llama ConexionBD después de reconectar: vuelve a abrir la sesión en la base de datos. */
    public static void restaurar(Connection con) {
        if (actual == null) {
            return;
        }
        try (CallableStatement cs = con.prepareCall("{call pkg_login.reanudar(?)}")) {
            cs.setString(1, actual.getCedula());
            cs.execute();
        } catch (SQLException e) {
            System.out.println("No se pudo restaurar la sesión en la base de datos: " + e.getMessage());
        }
    }

    public static Usuario getUsuario() {
        return actual;
    }

    /** Cédula del usuario con sesión, o null si nadie ha iniciado sesión. */
    public static String getCedula() {
        return actual == null ? null : actual.getCedula();
    }

    public static String getRol() {
        return actual == null ? null : actual.getRol();
    }

    public static boolean esSupervisor() {
        return actual != null && actual.esSupervisor();
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

    /** Reemplaza el nombre y la cédula fijos del diseño por los del usuario con sesión. */
    public static void actualizarEncabezado(Container contenedor) {
        if (actual == null) {
            return;
        }
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JLabel) {
                JLabel etiqueta = (JLabel) c;
                String texto = etiqueta.getText();
                if (texto != null) {
                    if (esNombreDeDiseno(texto)) {
                        etiqueta.setText(actual.getNombreCompleto().toUpperCase());
                    } else if (esCedulaDeDiseno(texto)) {
                        etiqueta.setText(actual.getCedula());
                    }
                }
            }
            if (c instanceof Container) {
                actualizarEncabezado((Container) c);
            }
        }
    }

    private static boolean esNombreDeDiseno(String texto) {
        String limpio = sinTildes(texto);
        for (String nombre : NOMBRES_DISENO) {
            if (limpio.equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    private static boolean esCedulaDeDiseno(String texto) {
        String limpio = texto.trim();
        for (String cedula : CEDULAS_DISENO) {
            if (limpio.equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    private static String sinTildes(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "").trim().toUpperCase();
    }
}
