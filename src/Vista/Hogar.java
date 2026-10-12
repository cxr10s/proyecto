/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.IdentificadorDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class Hogar extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Hogar.class.getName());

    private final Inicio padre;      // ventana principal (puede ser null)
    private final String idHogar;    // hogar que se está trabajando
    private final IdentificadorDAO dao = new IdentificadorDAO();

    public Hogar() {
        this(null, null);
    }

    public Hogar(Inicio padre, String idHogar) {
        this.padre = padre;
        this.idHogar = idHogar;
        initComponents();

        // Cerrar esta ventana NO debe cerrar toda la aplicación
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // instrucciones para que solo permita elegir solo un radiobottom
        buttonGroup1.add(RdbNadie);
        buttonGroup1.add(RdbEnc_Completa);
        buttonGroup1.add(RdbRechazo);
        buttonGroup1.add(RdbVacante);
        buttonGroup1.add(RdbEnc_Incompleta);
        buttonGroup1.add(RdbAusente);
        buttonGroup1.add(RdbOcupado);
        buttonGroup1.add(RdbOtro);

        // Muestra el número de hogar, ej: "Hogar numero : 001"
        if (idHogar != null) {
            jTextField1.setText("Hogar numero : " + idHogar);
        }

        // VOLVER cierra esta ventana y regresa a Inicio
        BtnVolver.addActionListener(e -> dispose());

        // Botones que abren las demás ventanas del formulario
        BtnControl.addActionListener(e -> abrirVentana(new ControlVisitas(this, idHogar)));
        BtnCaracteristica.addActionListener(e -> abrirVentana(new CaracteristicasDelGastoHogar(this, idHogar)));
        BtnGastos.addActionListener(e -> abrirVentana(new GastosDiarios(this, idHogar)));
        BtnComidas_Preparadas.addActionListener(e -> abrirVentana(new ComidasPreparadasFueraDelHogar(this, idHogar)));

        // GUARDAR: guarda el resultado del cuadernillo (se ve en VALIDACION de Inicio)
        BtnGuardar.addActionListener(e -> guardarResultado());
        cargarResultado();

        // Se cierre por VOLVER o por la X: se vuelve a la pantalla principal
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (Hogar.this.padre != null) {
                    Hogar.this.padre.cargarTabla();
                    Hogar.this.padre.setVisible(true);
                }
            }
        });
    }

    private javax.swing.JRadioButton[] radiosResultado() {
        return new javax.swing.JRadioButton[]{RdbNadie, RdbEnc_Completa, RdbRechazo, RdbVacante,
            RdbEnc_Incompleta, RdbAusente, RdbOcupado, RdbOtro};
    }

    /** Marca el radio que corresponde al resultado que el hogar ya tiene guardado. */
    private void cargarResultado() {
        if (idHogar == null) {
            return;
        }
        try {
            String actual = dao.obtenerResultado(idHogar);
            for (javax.swing.JRadioButton radio : radiosResultado()) {
                if (radio.getText().trim().equals(actual)) {
                    radio.setSelected(true);
                }
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar el resultado del cuadernillo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Guarda el resultado elegido en la base de datos. */
    private void guardarResultado() {
        if (idHogar == null) {
            JOptionPane.showMessageDialog(this, "Abra esta ventana desde un hogar.",
                    "Guardar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String resultado = null;
        for (javax.swing.JRadioButton radio : radiosResultado()) {
            if (radio.isSelected()) {
                resultado = radio.getText().trim();
            }
        }
        if (resultado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione el resultado del cuadernillo.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            dao.actualizarResultado(idHogar, resultado);
            JOptionPane.showMessageDialog(this,
                    "Resultado guardado: " + resultado + "\nSe verá en la columna VALIDACION de Inicio.");
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Oculta Hogar y muestra la ventana pedida; al cerrarla, ella vuelve a mostrar Hogar. */
    private void abrirVentana(javax.swing.JFrame ventana) {
        setVisible(false);
        ventana.setVisible(true);
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        jLabel10 = new javax.swing.JLabel();
        BtnControl = new javax.swing.JButton();
        BtnCaracteristica = new javax.swing.JButton();
        BtnGastos = new javax.swing.JButton();
        BtnComidas_Preparadas = new javax.swing.JButton();
        RdbNadie = new javax.swing.JRadioButton();
        RdbEnc_Completa = new javax.swing.JRadioButton();
        RdbRechazo = new javax.swing.JRadioButton();
        RdbVacante = new javax.swing.JRadioButton();
        RdbEnc_Incompleta = new javax.swing.JRadioButton();
        RdbAusente = new javax.swing.JRadioButton();
        RdbOcupado = new javax.swing.JRadioButton();
        RdbOtro = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        BtnVolver = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        BtnGuardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hogar");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel10.setText("Resultado cuadernillo :");

        BtnControl.setBackground(new java.awt.Color(204, 204, 204));
        BtnControl.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnControl.setText("Control de visita");

        BtnCaracteristica.setBackground(new java.awt.Color(204, 204, 204));
        BtnCaracteristica.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnCaracteristica.setText("Caractersitica del gasto del hogar");

        BtnGastos.setBackground(new java.awt.Color(204, 204, 204));
        BtnGastos.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnGastos.setText("Gastos diarios");

        BtnComidas_Preparadas.setBackground(new java.awt.Color(204, 204, 204));
        BtnComidas_Preparadas.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnComidas_Preparadas.setText("Comidas preparadas fuera del hogar");

        RdbNadie.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbNadie.setText("Nadie en el hogar");

        RdbEnc_Completa.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbEnc_Completa.setText("Encuesta completa");
        RdbEnc_Completa.addActionListener(this::RdbEnc_CompletaActionPerformed);

        RdbRechazo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbRechazo.setText("Rechazo");

        RdbVacante.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbVacante.setText("Vacante");

        RdbEnc_Incompleta.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbEnc_Incompleta.setText("Encuesta incompleta");
        RdbEnc_Incompleta.addActionListener(this::RdbEnc_IncompletaActionPerformed);

        RdbAusente.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbAusente.setText("Ausente temporal");

        RdbOcupado.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbOcupado.setText("Ocupado");
        RdbOcupado.addActionListener(this::RdbOcupadoActionPerformed);

        RdbOtro.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        RdbOtro.setText("Otro motivo");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dane (2).png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel3.setText("MATEO ALEJANDRO RODRÍGUEZ MARTÍNEZ");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel4.setText("1015162895");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel5.setText("ENCUESTADOR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 628, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addGap(22, 22, 22))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(26, 26, 26))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel3)
                        .addGap(17, 17, 17)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        BtnVolver.setBackground(new java.awt.Color(0, 153, 255));
        BtnVolver.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnVolver.setText("VOLVER");

        jTextField1.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        jTextField1.setText("Hogar numero :");
        jTextField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField1.setEnabled(false);
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        BtnGuardar.setBackground(new java.awt.Color(51, 255, 51));
        BtnGuardar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnGuardar.setText("GUARDAR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(134, 134, 134)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(RdbEnc_Incompleta)
                                    .addComponent(RdbEnc_Completa))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(RdbNadie)
                                    .addComponent(RdbAusente, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(RdbOcupado, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(24, 24, 24)
                                        .addComponent(RdbOtro, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(RdbRechazo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(24, 24, 24)
                                        .addComponent(RdbVacante, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(BtnComidas_Preparadas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                                .addComponent(BtnGastos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(BtnCaracteristica, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(BtnControl, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(BtnVolver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnGuardar)
                .addGap(129, 129, 129))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(375, 375, 375)
                        .addComponent(jLabel10))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(BtnControl)
                        .addGap(18, 18, 18)
                        .addComponent(BtnCaracteristica, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnGastos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtnComidas_Preparadas)
                        .addGap(134, 134, 134)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(RdbEnc_Completa)
                            .addComponent(RdbNadie)
                            .addComponent(RdbRechazo)
                            .addComponent(RdbVacante))
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(RdbEnc_Incompleta)
                            .addComponent(RdbAusente)
                            .addComponent(RdbOcupado)
                            .addComponent(RdbOtro))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnVolver)
                    .addComponent(BtnGuardar))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void RdbEnc_CompletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RdbEnc_CompletaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RdbEnc_CompletaActionPerformed

    private void RdbEnc_IncompletaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RdbEnc_IncompletaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RdbEnc_IncompletaActionPerformed

    private void RdbOcupadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RdbOcupadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RdbOcupadoActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Hogar().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnCaracteristica;
    private javax.swing.JButton BtnComidas_Preparadas;
    private javax.swing.JButton BtnControl;
    private javax.swing.JButton BtnGastos;
    private javax.swing.JButton BtnGuardar;
    private javax.swing.JButton BtnVolver;
    private javax.swing.JRadioButton RdbAusente;
    private javax.swing.JRadioButton RdbEnc_Completa;
    private javax.swing.JRadioButton RdbEnc_Incompleta;
    private javax.swing.JRadioButton RdbNadie;
    private javax.swing.JRadioButton RdbOcupado;
    private javax.swing.JRadioButton RdbOtro;
    private javax.swing.JRadioButton RdbRechazo;
    private javax.swing.JRadioButton RdbVacante;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
