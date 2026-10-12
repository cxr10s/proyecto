/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.Encuestador;
import Modelo.EncuestadorDAO;
import Modelo.Sesion;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author menes
 */



public class Login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());

    /**
     * Creates new form LoginUDIVisual
     */
    public Login() {
        initComponents();
        // Fondo y panel
        ponerFondo();
        redondearPanel();
        //contraseña
        TxfContrasena.setEchoChar('⦁');
         // Configuración de los campos y el botón
        configurarCampos();
        //configurarBoton();

        // Inicio de sesión: botón, Enter en la contraseña y Enter en el usuario (pasa a la contraseña)
        ButIniciar_Sesion.addActionListener(e -> iniciarSesion());
        TxfContrasena.addActionListener(e -> iniciarSesion());
        setLocationRelativeTo(null);
    }

    /** Valida el usuario y la contraseña contra la tabla ENCUESTADOR y abre la pantalla de inicio. */
    private void iniciarSesion() {
        String usuario = TxfUsuario.getText().trim();
        String clave = new String(TxfContrasena.getPassword());
        if (usuario.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el usuario y la contraseña.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ButIniciar_Sesion.setEnabled(false);
        try {
            Encuestador encuestador = new EncuestadorDAO().autenticar(usuario, clave);
            if (encuestador == null) {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.",
                        "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
                TxfContrasena.setText("");
                TxfContrasena.requestFocusInWindow();
                return;
            }
            Sesion.iniciar(encuestador);     // antes de abrir Inicio: él ya necesita saber quién es
            new Inicio().setVisible(true);
            dispose();                       // cierra el Login (la aplicación sigue en Inicio)
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo iniciar sesión:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            ButIniciar_Sesion.setEnabled(true);
        }
    }
    
    //esta metodo hace que el fondo se visualize mejor como background
    private void ponerFondo() {

    java.net.URL recurso =
        getClass().getResource("/imagenes/fondo1.jpg");

    if (recurso == null) {
        javax.swing.JOptionPane.showMessageDialog(
            this,
            "No se encontró /imagenes/fondo1.jpg"
        );
        return;
    }

    final java.awt.Image imagen =
        new javax.swing.ImageIcon(recurso).getImage();

    // Usamos el panel que ya creaste desde Design.
    jPanel1.setOpaque(false);

    // Dibujamos el fondo en el contenedor principal.
    javax.swing.JPanel fondo = new javax.swing.JPanel() {
        @Override
        protected void paintComponent(java.awt.Graphics g) {
            super.paintComponent(g);

            g.drawImage(
                imagen,
                0, 0,
                getWidth(), getHeight(),
                this
            );
        }
    };

    fondo.setLayout(new java.awt.GridBagLayout());

    // Quitamos los componentes del contenedor principal
    // y los colocamos dentro del panel de fondo.
    java.awt.Container contenido = getContentPane();

    java.awt.Component[] componentes =
        contenido.getComponents();

    contenido.removeAll();

    for (java.awt.Component componente : componentes) {
        if (componente != jPanel1) {
            contenido.add(componente);
        }
    }

    // Colocamos el panel de fondo y luego el panel del login.
    setContentPane(fondo);

    java.awt.GridBagConstraints gbc =
        new java.awt.GridBagConstraints();

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = java.awt.GridBagConstraints.NONE;
    gbc.anchor = java.awt.GridBagConstraints.CENTER;

    fondo.add(jPanel1, gbc);

    fondo.revalidate();
    fondo.repaint();
}
    private void redondearPanel() {
    jPanel2.setOpaque(false);

    jPanel2.setBackground(new java.awt.Color(255, 255, 255));

    jPanel2.setUI(new javax.swing.plaf.PanelUI() {
        @Override
        public void update(
                java.awt.Graphics g,
                javax.swing.JComponent c) {

            java.awt.Graphics2D g2 =
                    (java.awt.Graphics2D) g.create();

            g2.setRenderingHint(
                    java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(c.getBackground());

            g2.fillRoundRect(
                    0, 0,
                    c.getWidth(),
                    c.getHeight(),
                    30, 30
            );

            g2.dispose();
        }
    });
}
    
private void redondearCampo(javax.swing.JTextField campo) {
    campo.setOpaque(false);

    campo.setBorder(
        javax.swing.BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(
                new java.awt.Color(180, 180, 180), 1, true
            ) {
                @Override
                public void paintBorder(
                    java.awt.Component c,
                    java.awt.Graphics g,
                    int x, int y, int width, int height) {

                    java.awt.Graphics2D g2 =
                        (java.awt.Graphics2D) g.create();

                    g2.setRenderingHint(
                        java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON
                    );

                    g2.setColor(getLineColor());
                    g2.drawRoundRect(
                    x + 1, y + 1, width - 3, height - 3, 35, 35
                    );
                    g2.dispose();
                }
            },
            javax.swing.BorderFactory.createEmptyBorder(8, 12, 8, 12)
        )
    );
}
    
    private void configurarCampos() {
    redondearCampo(TxfUsuario);
    redondearCampo(TxfContrasena);

    RabMostrar_Contraseña.setOpaque(false);

    // Mostrar u ocultar la contraseña
    RabMostrar_Contraseña.addActionListener(e -> {
        if (RabMostrar_Contraseña.isSelected()) {
            TxfContrasena.setEchoChar((char) 0);
        } else {
            TxfContrasena.setEchoChar('⦁');
        }
    });
}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TxfUsuario = new javax.swing.JTextField();
        ButIniciar_Sesion = new javax.swing.JButton();
        TxfContrasena = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        RabMostrar_Contraseña = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login");

        jPanel1.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel1.setText("Bienvenido al sistema");

        TxfUsuario.addActionListener(this::TxfUsuarioActionPerformed);

        ButIniciar_Sesion.setBackground(new java.awt.Color(0, 204, 255));
        ButIniciar_Sesion.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        ButIniciar_Sesion.setText("Iniciar Sesion");

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setText("Usuario");

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setText("Contraseña");

        RabMostrar_Contraseña.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        RabMostrar_Contraseña.setText("Mostrar contraseña");
        RabMostrar_Contraseña.addActionListener(this::RabMostrar_ContraseñaActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(121, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(119, 119, 119))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TxfContrasena, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                    .addComponent(TxfUsuario, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator3)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(RabMostrar_Contraseña)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(133, 133, 133)
                .addComponent(ButIniciar_Sesion, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addGap(23, 23, 23)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TxfUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel3)
                .addGap(12, 12, 12)
                .addComponent(TxfContrasena, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RabMostrar_Contraseña)
                .addGap(12, 12, 12)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ButIniciar_Sesion, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(330, 330, 330)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(267, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 49, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TxfUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxfUsuarioActionPerformed
        TxfContrasena.requestFocusInWindow();   // Enter en el usuario: pasa a la contraseña
    }//GEN-LAST:event_TxfUsuarioActionPerformed

    private void RabMostrar_ContraseñaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RabMostrar_ContraseñaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RabMostrar_ContraseñaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
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
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ButIniciar_Sesion;
    private javax.swing.JRadioButton RabMostrar_Contraseña;
    private javax.swing.JPasswordField TxfContrasena;
    private javax.swing.JTextField TxfUsuario;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
