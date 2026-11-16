/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.EncuestaPendiente;
import Controladores.RevisionDAO;
import com.github.lgooddatepicker.components.DatePicker;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author menes
 */
public class Revisados extends javax.swing.JFrame implements Refrescable {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Revisados.class.getName());

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RevisionDAO dao = new RevisionDAO();
    private List<EncuestaPendiente> filas = new ArrayList<>();
    private final javax.swing.JTextField txfCedula = new javax.swing.JTextField(12);
    private final DatePicker dpDesde = new DatePicker();
    private final DatePicker dpHasta = new DatePicker();

    /**
     * Creates new form Revisados
     */
    public Revisados() {
        initComponents();
        configurar();
    }

    /**
     * Encuestas completas que el supervisor ya revisó y aprobó, de TODOS los encuestadores.
     * Doble clic en una fila: abre el menú del hogar en modo consulta (HogarSupervisor, solo ver).
     */
    private void configurar() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // Tabla: sin edición y de una sola fila a la vez
        jTable1.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"ID HOGAR", "ENCUESTADOR", "CEDULA", "MUNICIPIO", "ULTIMA VISITA", "FECHA REVISION", "ESTADO"}) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSeleccionada();
                }
            }
        });
        jLabel5.setText("Encuestas revisadas");
        BtnVolverRev.addActionListener(e -> dispose());

        // Filtros (cédula del encuestador y rango de fechas de la revisión)
        javax.swing.JPanel filtros = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        filtros.add(new javax.swing.JLabel("Cédula del encuestador:"));
        filtros.add(txfCedula);
        filtros.add(new javax.swing.JLabel("Revisada desde:"));
        filtros.add(dpDesde);
        filtros.add(new javax.swing.JLabel("hasta:"));
        filtros.add(dpHasta);
        javax.swing.JButton btnFiltrar = new javax.swing.JButton("FILTRAR");
        btnFiltrar.addActionListener(e -> cargarTabla());
        filtros.add(btnFiltrar);
        javax.swing.JButton btnLimpiar = new javax.swing.JButton("LIMPIAR");
        btnLimpiar.addActionListener(e -> {
            txfCedula.setText("");
            dpDesde.clear();
            dpHasta.clear();
            cargarTabla();
        });
        filtros.add(btnLimpiar);
        txfCedula.addActionListener(e -> cargarTabla());

        // Se reacomodan los componentes que ya existen (encabezado, título, tabla y VOLVER)
        // para poder poner los filtros entre el título y la tabla sin tocar el diseño generado
        javax.swing.JPanel contenido = new javax.swing.JPanel(new java.awt.BorderLayout());
        contenido.add(jPanel1, java.awt.BorderLayout.NORTH);

        javax.swing.JPanel centro = new javax.swing.JPanel(new java.awt.BorderLayout(6, 6));
        centro.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 10, 20));
        javax.swing.JPanel arriba = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
        arriba.add(jLabel5, java.awt.BorderLayout.NORTH);
        arriba.add(filtros, java.awt.BorderLayout.CENTER);
        centro.add(arriba, java.awt.BorderLayout.NORTH);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(1000, 380));
        centro.add(jScrollPane1, java.awt.BorderLayout.CENTER);
        javax.swing.JPanel pie = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 10));
        pie.add(BtnVolverRev);
        centro.add(pie, java.awt.BorderLayout.SOUTH);
        contenido.add(centro, java.awt.BorderLayout.CENTER);

        setContentPane(contenido);
        pack();
        setLocationRelativeTo(null);

        cargarTabla();
    }

    /** Vuelve a leer de la base de datos las encuestas revisadas (respeta los filtros). */
    @Override
    public void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        filas = new ArrayList<>();

        LocalDate desde = dpDesde.getDate();
        LocalDate hasta = dpHasta.getDate();
        if (desde != null && hasta != null && hasta.isBefore(desde)) {
            JOptionPane.showMessageDialog(this, "La fecha final no puede ser anterior a la inicial.",
                    "Filtro de fechas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            filas = dao.listarRevisadas(txfCedula.getText(), desde, hasta);
            for (EncuestaPendiente e : filas) {
                modelo.addRow(new Object[]{
                    e.getIdHogar(),
                    e.getEncuestador(),
                    e.getCedula(),
                    e.getMunicipio(),
                    e.getUltimaVisita() == null ? "" : e.getUltimaVisita().format(FECHA),
                    e.getFechaRevision() == null ? "" : e.getFechaRevision().format(FECHA),
                    e.getEstado()
                });
            }
            setTitle("Encuestas revisadas (" + filas.size() + ")");
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las encuestas:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Doble clic: se abre el menú del hogar en modo consulta (no cambia el estado de la encuesta). */
    private void abrirSeleccionada() {
        int fila = jTable1.getSelectedRow();
        if (fila < 0) {
            return;
        }
        EncuestaPendiente e = filas.get(jTable1.convertRowIndexToModel(fila));
        try {
            HogarSupervisor hogar = new HogarSupervisor(this, e.getIdHogar(), e.getEncuestador(), true);
            setVisible(false);
            hogar.setVisible(true);
        } catch (Throwable ex) {
            setVisible(true);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo abrir la pantalla:\n" + ex,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        BtnVolverRev = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/dane (2).png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel2.setText("JUAN DIEGO GUTIÉRREZ CASTRO");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel3.setText("1026529849");

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel4.setText("SUPERVISOR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 563, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        BtnVolverRev.setBackground(new java.awt.Color(0, 153, 255));
        BtnVolverRev.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnVolverRev.setText("VOLVER");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 26)); // NOI18N
        jLabel5.setText("Encuestadores en estado de revisado");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "NOMBRE DEL ENCUESTADOR", "ID", "ESTADO"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel5)
                        .addGap(0, 1071, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(BtnVolverRev)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(BtnVolverRev)
                .addContainerGap(89, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
        java.awt.EventQueue.invokeLater(() -> new Revisados().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnVolverRev;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
