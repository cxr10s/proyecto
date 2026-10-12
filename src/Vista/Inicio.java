/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.Identificador;
import Modelo.IdentificadorDAO;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author menes
 */
public class Inicio extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Inicio.class.getName());

    /**
     * Creates new form Inicio
     */
    private final IdentificadorDAO dao = new IdentificadorDAO();

    public Inicio() {
        initComponents();
        setLocationRelativeTo(null);
        configurarTabla();
        cargarTabla();
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        TabDatos_Hogar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {                 // doble clic
                    int fila = TabDatos_Hogar.rowAtPoint(e.getPoint());
                    if (fila >= 0) {
                        abrirHogar(String.valueOf(TabDatos_Hogar.getValueAt(fila, 1)));   // columna ID HOGAR
                    }
                }
            }
        });
    }

    /** Elimina el hogar seleccionado en la tabla (con confirmación). */
    private void eliminarSeleccionado() {
        int fila = TabDatos_Hogar.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un hogar de la tabla para eliminarlo.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String municipio = String.valueOf(TabDatos_Hogar.getValueAt(fila, 0));
        String idHogar = String.valueOf(TabDatos_Hogar.getValueAt(fila, 1));

        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el hogar " + idHogar + " (" + municipio + ")?\n"
                + "Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.eliminar(idHogar);
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Hogar " + idHogar + " eliminado correctamente.");
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            String mensaje = (ex.getErrorCode() == 2292)   // ORA-02292: tiene registros hijos
                    ? "No se puede eliminar: el hogar ya tiene datos asociados\n"
                    + "(visitas, gastos u otras secciones de la encuesta)."
                    : "No se pudo eliminar:\n" + ex.getMessage();
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Reemplaza las 40 filas vacías del diseñador por un modelo de 4 columnas sin edición. */
    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"MUNICIPIO", "ID HOGAR", "ESTADO", "VALIDACION"}) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        TabDatos_Hogar.setModel(modelo);
        TabDatos_Hogar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /** Vuelve a leer los hogares de la base de datos y los muestra en la tabla. */
    public void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) TabDatos_Hogar.getModel();
        modelo.setRowCount(0);
        try {
            List<Identificador> hogares = dao.listar();
            for (Identificador h : hogares) {
                modelo.addRow(new Object[]{
                    h.getMunicipio(), h.getIdHogar(), h.getEstado(), h.getValidacion()
                });
            }
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los hogares:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirHogar(String idHogar) {
        Hogar ventana = new Hogar(this, idHogar);
        setVisible(false);          // al cerrar Hogar, él mismo vuelve a mostrar Inicio
        ventana.setVisible(true);
    }

    private void abrirFormulario(String idHogarEditar) {
        Identificacion form = new Identificacion(this, idHogarEditar);
        setVisible(false);          // al cerrar el formulario, él mismo vuelve a mostrar Inicio
        form.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ButAgregar_Hogar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        BtnModificar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabDatos_Hogar = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("INICIO");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(0, 153, 255));

        ButAgregar_Hogar.setBackground(new java.awt.Color(153, 153, 153));
        ButAgregar_Hogar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        ButAgregar_Hogar.setText("AGREGAR HOGAR");
        ButAgregar_Hogar.addActionListener(this::ButAgregar_HogarActionPerformed);

        btnEliminar.setBackground(new java.awt.Color(255, 51, 51));
        btnEliminar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        btnEliminar.setText("ELIMINAR");

        BtnModificar.setBackground(new java.awt.Color(255, 153, 0));
        BtnModificar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnModificar.setText("MODIFICAR");
        BtnModificar.addActionListener(this::BtnModificarActionPerformed);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel1.setText("MATEO ALEJANDRO RODRÍGUEZ MARTÍNEZ");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel2.setText("1015162895");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel3.setText("ENCUESTADOR");

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dane (2).png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 497, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TabDatos_Hogar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "MUNICIPIO", "ID HOGAR", "ESTADO", "VALIDACION"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TabDatos_Hogar);

        jPanel2.setBackground(new java.awt.Color(0, 204, 255));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel4.setText("PARA INGRESAR A LA ENCUESTA DE UN HOGAR DEBE DARLE DOBLE CLICK EN EL HOGAR  EN EL CUAL SE QUIERE REALIZAR LA ENCUESTA");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel4))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(ButAgregar_Hogar))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(btnEliminar)
                        .addGap(36, 36, 36)
                        .addComponent(BtnModificar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButAgregar_Hogar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar)
                    .addComponent(BtnModificar))
                .addGap(44, 44, 44))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButAgregar_HogarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButAgregar_HogarActionPerformed
        abrirFormulario(null);   // modo agregar
    }//GEN-LAST:event_ButAgregar_HogarActionPerformed

    private void BtnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnModificarActionPerformed
        int fila = TabDatos_Hogar.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un hogar de la tabla para modificarlo.",
                    "Modificar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idHogar = String.valueOf(TabDatos_Hogar.getValueAt(fila, 1));   // columna ID HOGAR
        abrirFormulario(idHogar);   // modo modificar
    }//GEN-LAST:event_BtnModificarActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Inicio().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnModificar;
    private javax.swing.JButton ButAgregar_Hogar;
    private javax.swing.JTable TabDatos_Hogar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
