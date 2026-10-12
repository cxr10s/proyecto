package Ejecutable;

import Vista.Login;

/**
 * Clase para ejecutar todo el proyecto: abre el Login y, desde ahí, el resto de las ventanas.
 *
 * @author ASUS
 */
public class EjecutableProyecto {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(EjecutableProyecto.class.getName());

    public static void main(String[] args) {
        // Mismo aspecto (Nimbus) que usan las ventanas
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        // El programa empieza en el Login
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }
}
