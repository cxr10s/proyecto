/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Controladores.RevisionDAO;
import Modelo.EncuestaPendiente;
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
public class NoRevisados extends javax.swing.JFrame implements Refrescable {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NoRevisados.class.getName());

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RevisionDAO dao = new RevisionDAO();
    private List<EncuestaPendiente> filas = new ArrayList<>();
    private final javax.swing.JTextField txfCedula = new javax.swing.JTextField(12);
    private final DatePicker dpDesde = new DatePicker();
    private final DatePicker dpHasta = new DatePicker();

    /**
     * Creates new form NoRevisados
     */
    public NoRevisados() {
        initComponents();
        configurar();
    }

    /**
     * Encuestas completas que esperan revisión, de TODOS los encuestadores.
     * Doble clic en una fila: abre el menú del hogar para el supervisor (HogarSupervisor).
     */
    private void configurar() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        // Tabla: columnas nuevas, sin edición y de una sola fila a la vez
        TabEncuestadoresPen.setModel(new DefaultTableModel(new Object[][]{},
                new String[]{"ID HOGAR", "ENCUESTADOR", "CEDULA", "MUNICIPIO", "ULTIMA VISITA", "ESTADO"}) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        });
        TabEncuestadoresPen.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TabEncuestadoresPen.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirSeleccionada();
                }
            }
        });
        jLabel5.setText("Encuestas pendientes por revision");
        BtnVolver.addActionListener(e -> dispose());

        // Filtros (código del encuestador y rango de fechas de la última visita)
        javax.swing.JPanel filtros = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        filtros.add(new javax.swing.JLabel("Cédula del encuestador:"));
        filtros.add(txfCedula);
        filtros.add(new javax.swing.JLabel("Última visita desde:"));
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
        pie.add(BtnVolver);
        centro.add(pie, java.awt.BorderLayout.SOUTH);
        contenido.add(centro, java.awt.BorderLayout.CENTER);

        setContentPane(contenido);
        pack();
        setLocationRelativeTo(null);

        cargarTabla();
    }

    /** Vuelve a leer de la base de datos las encuestas pendientes (respeta los filtros). */
    @Override
    public void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) TabEncuestadoresPen.getModel();
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
            filas = dao.listarPendientes(txfCedula.getText(), desde, hasta);
            for (EncuestaPendiente e : filas) {
                modelo.addRow(new Object[]{
                    e.getIdHogar(),
                    e.getEncuestador(),
                    e.getCedula(),
                    e.getMunicipio(),
                    e.getUltimaVisita() == null ? "" : e.getUltimaVisita().format(FECHA),
                    e.getEstado()
                });
            }
            setTitle("Encuestas pendientes por revisión (" + filas.size() + ")");
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las encuestas:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Doble clic: el supervisor abre la encuesta (pasa a "En revision") y se muestra el menú del hogar. */
    private void abrirSeleccionada() {
        int fila = TabEncuestadoresPen.getSelectedRow();
        if (fila < 0) {
            return;
        }
        EncuestaPendiente e = filas.get(TabEncuestadoresPen.convertRowIndexToModel(fila));
        try {
            dao.iniciarRevision(e.getIdHogar());
            HogarSupervisor hogar = new HogarSupervisor(this, e.getIdHogar(), e.getEncuestador());
            setVisible(false);
            hogar.setVisible(true);
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo abrir la encuesta:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            cargarTabla();
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
        BtnVolver = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabEncuestadoresPen = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

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
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 533, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 676, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
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

        BtnVolver.setBackground(new java.awt.Color(0, 153, 255));
        BtnVolver.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnVolver.setText("VOLVER");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel5.setText("Encuestadores pendientes por revision");

        TabEncuestadoresPen.setModel(new javax.swing.table.DefaultTableModel(
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(TabEncuestadoresPen);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(BtnVolver)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(BtnVolver)
                .addContainerGap(90, Short.MAX_VALUE))
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
        java.awt.EventQueue.invokeLater(() -> new NoRevisados().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnVolver;
    private javax.swing.JTable TabEncuestadoresPen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
