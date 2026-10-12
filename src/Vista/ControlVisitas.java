/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.ControlVisita;
import Modelo.ControlVisitaDAO;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author menes
 */
public class ControlVisitas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ControlVisitas.class.getName());
    
    private final Hogar padre;      // ventana Hogar desde la que se abrió (puede ser null)
    private final String idHogar;   // hogar al que pertenecen estos datos
    private final ControlVisitaDAO dao = new ControlVisitaDAO();
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    public ControlVisitas() {
        this(null, null);
    }

    public ControlVisitas(Hogar padre, String idHogar) {
        this.padre = padre;
        this.idHogar = idHogar;
        initComponents();
        configurarNavegacion();
        TabDatos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        BtnEliminar.addActionListener(e -> eliminarSeleccionada());
        cargarTabla();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        BtnAgregar_datos = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabDatos = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        BtnEliminar = new javax.swing.JButton();
        BtnModificar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        BtnVolver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Control visitas");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        jLabel1.setText("Control de visitas");

        BtnAgregar_datos.setBackground(new java.awt.Color(153, 153, 153));
        BtnAgregar_datos.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnAgregar_datos.setText("AGREGAR DATOS");
        BtnAgregar_datos.addActionListener(this::BtnAgregar_datosActionPerformed);

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Duracion de la visita");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel8.setText("FECHA DE LA PROXIMA VISITA");

        TabDatos.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        TabDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "NUMERO DE VISITA", "FECHA", "HORA ", "JORNADA", "HORA ", "JORNADA", "RESULTADO DE LA ENTREVISTA", "PERSONAS Y/O PARTES DEL CUADERNILLO PENDIENTE POR DILIGENCIAR", "DIA", "HORA"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TabDatos.setToolTipText("");
        jScrollPane1.setViewportView(TabDatos);
        if (TabDatos.getColumnModel().getColumnCount() > 0) {
            TabDatos.getColumnModel().getColumn(0).setPreferredWidth(80);
            TabDatos.getColumnModel().getColumn(1).setPreferredWidth(60);
            TabDatos.getColumnModel().getColumn(2).setPreferredWidth(60);
            TabDatos.getColumnModel().getColumn(3).setPreferredWidth(60);
            TabDatos.getColumnModel().getColumn(4).setPreferredWidth(60);
            TabDatos.getColumnModel().getColumn(5).setPreferredWidth(60);
            TabDatos.getColumnModel().getColumn(6).setPreferredWidth(150);
            TabDatos.getColumnModel().getColumn(7).setPreferredWidth(400);
        }

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel6.setText("INICIO");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel7.setText("TERMINACION");

        BtnEliminar.setBackground(new java.awt.Color(255, 51, 51));
        BtnEliminar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnEliminar.setText("ELIMINAR");

        BtnModificar.setBackground(new java.awt.Color(255, 153, 0));
        BtnModificar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnModificar.setText("MODIFICAR");
        BtnModificar.addActionListener(this::BtnModificarActionPerformed);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dane (2).png"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel3.setText("MATEO ALEJANDRO RODRÍGUEZ MARTÍNEZ");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel4.setText("1015162895");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel9.setText("ENCUENTADOR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 579, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 575, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(17, 17, 17)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        BtnVolver.setBackground(new java.awt.Color(0, 153, 255));
        BtnVolver.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnVolver.setText("VOLVER");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnAgregar_datos)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addGap(309, 309, 309)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(116, 116, 116)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addGap(22, 22, 22))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnVolver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnModificar)
                .addGap(38, 38, 38)
                .addComponent(BtnEliminar)
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel5))
                    .addComponent(BtnAgregar_datos))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnVolver)
                    .addComponent(BtnModificar)
                    .addComponent(BtnEliminar))
                .addGap(42, 42, 42))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnAgregar_datosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAgregar_datosActionPerformed
        abrirFormulario(null);   // modo agregar
    }//GEN-LAST:event_BtnAgregar_datosActionPerformed

    private void BtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnModificarActionPerformed
        int fila = TabDatos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una visita de la tabla para modificarla.",
                    "Modificar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        abrirFormulario(String.valueOf(TabDatos.getValueAt(fila, 0)));   // columna NUMERO DE VISITA
    }//GEN-LAST:event_BtnModificarActionPerformed

    /** Cerrar esta ventana no cierra la aplicación: VOLVER y la X regresan a Hogar. */
    private void configurarNavegacion() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        if (idHogar != null) {
            setTitle("Control de visitas - Hogar " + idHogar);
        }
        BtnVolver.addActionListener(e -> dispose());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (padre != null) {
                    padre.setVisible(true);
                }
            }
        });
    }

    /** Vuelve a leer las visitas del hogar y las muestra en la tabla. */
    public void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) TabDatos.getModel();
        modelo.setRowCount(0);
        if (idHogar == null) {
            return;
        }
        try {
            List<ControlVisita> visitas = dao.listar(idHogar);
            for (ControlVisita v : visitas) {
                modelo.addRow(new Object[]{
                    v.getNumVisita(),
                    v.getFecha() == null ? "" : v.getFecha().format(FECHA),
                    v.getHoraInicio() == null ? "" : v.getHoraInicio().format(HORA),
                    v.getJornadaInicio(),
                    v.getHoraFin() == null ? "" : v.getHoraFin().format(HORA),
                    v.getJornadaFin(),
                    v.getResultado(),
                    v.getPendiente(),
                    v.getDiaProxima() == null ? "" : v.getDiaProxima().format(FECHA),
                    v.getHoraProxima() == null ? "" : v.getHoraProxima().format(HORA)
                });
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar las visitas:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Abre el formulario: numVisitaEditar null = agregar; con valor = modificar. */
    private void abrirFormulario(String numVisitaEditar) {
        if (idHogar == null) {
            JOptionPane.showMessageDialog(this, "Abra esta ventana desde un hogar.",
                    "Control de visitas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ControlVisitaAgregar form = new ControlVisitaAgregar(this, idHogar, numVisitaEditar);
        setVisible(false);          // al cerrar el formulario, él mismo vuelve a mostrar esta ventana
        form.setVisible(true);
    }

    /** Elimina la visita seleccionada (con confirmación). */
    private void eliminarSeleccionada() {
        int fila = TabDatos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una visita de la tabla para eliminarla.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String numVisita = String.valueOf(TabDatos.getValueAt(fila, 0));

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar la visita " + numVisita + " del hogar " + idHogar + "?\n"
                + "Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.eliminar(idHogar, Integer.parseInt(numVisita));
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Visita " + numVisita + " eliminada correctamente.");
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo eliminar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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
        java.awt.EventQueue.invokeLater(() -> new ControlVisitas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnAgregar_datos;
    private javax.swing.JButton BtnEliminar;
    private javax.swing.JButton BtnModificar;
    private javax.swing.JButton BtnVolver;
    private javax.swing.JTable TabDatos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
