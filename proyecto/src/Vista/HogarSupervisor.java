/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Controladores.RevisionDAO;
import Modelo.Sesion;
import Modelo.Usuario;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * Menú de un hogar para el SUPERVISOR
 *
 * @author ASUS
 */
public class HogarSupervisor extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HogarSupervisor.class.getName());

    private JFrame padre;                       // ventana de la lista desde la que se abrió
    private String idHogar;
    private boolean soloConsulta;               // true = encuesta ya revisada: solo se mira, no se dictamina
    private final RevisionDAO dao = new RevisionDAO();

    /**
     * Constructor 
     */
    public HogarSupervisor() {
        initComponents();
    }

    /**
     * Constructor que se usa en la aplicación.
     *
     * @param padre ventana de la lista desde la que se abre
     * @param idHogar número del hogar a revisar
     * @param encuestador nombre del encuestador dueño de la encuesta
     */
    public HogarSupervisor(JFrame padre, String idHogar, String encuestador) {
        this(padre, idHogar, encuestador, false);
    }

    /**
     * @param soloConsulta true para una encuesta ya revisada (viene de "Revisados"): se pueden ver
     *                     las secciones, pero no se muestra el dictamen ni el botón Guardar.
     */
    public HogarSupervisor(JFrame padre, String idHogar, String encuestador, boolean soloConsulta) {
        this.padre = padre;
        this.idHogar = idHogar;
        this.soloConsulta = soloConsulta;

        initComponents();
        configurarLogica(encuestador);
        if (soloConsulta) {
            aplicarModoConsulta();
        }

        setLocationRelativeTo(null);
    }

    /** Encuesta ya revisada: se oculta el dictamen (Completo/Incompleto y Guardar); solo queda ver. */
    private void aplicarModoConsulta() {
        setTitle("Consulta del hogar " + idHogar + " (revisada)");
        jTextField3.setText("Resultado cuadernillo: Completo (aprobado)");
        RdbComplet.setVisible(false);
        RdbIncompleto.setVisible(false);
        jScrollPane1.setVisible(false);
        btnGuardar.setVisible(false);
        pack();
    }

  
    // Lógica 
   
    private void configurarLogica(String encuestador) {
        // Al cerrar (VOLVER, GUARDAR o la X) no se termina el programa: se regresa a la lista
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Revisión del hogar " + idHogar);

        // Datos de la pantalla
        jTextField1.setText("Hogar numero : " + idHogar);
        jTextField2.setText("Encuestador: " + (encuestador == null ? "-" : encuestador));

        Usuario u = Sesion.getUsuario();
        jLabel3.setText(u == null ? "" : u.getNombreCompleto().toUpperCase());
        jLabel4.setText(u == null ? "" : u.getCedula());
        jLabel5.setText("SUPERVISOR");

        // Textos de los botones de sección 
        BtnCaractersiticaGasto.setText("CONTROL DE VISITAS");     
        jButton1.setText("CARACTERÍSTICAS DEL GASTO");            
        jButton2.setText("GASTOS DIARIOS");                       
        jButton3.setText("COMIDAS PREPARADAS FUERA DEL HOGAR");   

        // Solo se puede marcar una opción
        buttonGroup1.add(RdbComplet);   // Completo
        buttonGroup1.add(RdbIncompleto);   // Incompleto

        // Botones de las secciones del cuadernillo 
        BtnCaractersiticaGasto.addActionListener(e -> abrirVentana(() -> new ControlVisitas(this, idHogar)));
        // jButton1 ya tiene su listener generado por el diseñador: jButton1ActionPerformed
        jButton2.addActionListener(e -> abrirVentana(() -> new GastosDiarios(this, idHogar)));
        jButton3.addActionListener(e -> abrirVentana(() -> new ComidasPreparadasFueraDelHogar(this, idHogar)));

        // Guardar el resultado y volver
        btnGuardar.addActionListener(e -> guardarResultado());
        BtnVolver.addActionListener(e -> dispose());

        // Se cierre por VOLVER, por GUARDAR o por la X: se regresa a la lista, ya actualizada
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                if (padre != null) {
                    if (padre instanceof Refrescable) {
                        ((Refrescable) padre).cargarTabla();
                    }
                    padre.setVisible(true);
                }
            }
        });
    }

    /**
     * Abre una sección del cuadernillo (modo consulta) y oculta esta ventana hasta que se cierre.
     */
    private void abrirVentana(Supplier<JFrame> fabrica) {
        try {
            JFrame ventana = fabrica.get();
            ventana.setLocationRelativeTo(null);
            setVisible(false);
            ventana.setVisible(true);
        } catch (Throwable ex) {
            setVisible(true);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo abrir la pantalla:\n" + ex,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * GUARDAR: da el dictamen (Completo = Aprobado, Incompleto = Rechazado).
     */
    private void guardarResultado() {
        if (soloConsulta) {
            return;     // una encuesta revisada no se vuelve a dictaminar
        }
        if (!RdbComplet.isSelected() && !RdbIncompleto.isSelected()) {
            JOptionPane.showMessageDialog(this, "Elija Completo o Incompleto.",
                    "Resultado del cuadernillo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean completo = RdbComplet.isSelected();
        String pregunta = completo
                ? "¿Confirma que la encuesta del hogar " + idHogar + " está COMPLETA?\n"
                + "Quedará aprobada y el encuestador ya no podrá modificarla."
                : "¿Confirma que la encuesta del hogar " + idHogar + " está INCOMPLETA?\n"
                + "Se devolverá al encuestador para que la corrija.";
        int opcion = JOptionPane.showConfirmDialog(this, pregunta, "Resultado del cuadernillo",
                JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            dao.dictaminar(idHogar, completo);
            JOptionPane.showMessageDialog(this,
                    completo ? "Encuesta aprobada." : "Encuesta devuelta al encuestador.",
                    "Resultado del cuadernillo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Código generado por el diseñador (no se modifica)
    // ------------------------------------------------------------------

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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        BtnCaractersiticaGasto = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        RdbComplet = new javax.swing.JRadioButton();
        RdbIncompleto = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btnGuardar = new javax.swing.JButton();
        BtnVolver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(4, 4, 4)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(17, 17, 17)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        jTextField1.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        jTextField1.setText("Hogar numero :");
        jTextField1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField1.setEnabled(false);

        jTextField2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        jTextField2.setText("Encuestador: ");
        jTextField2.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField2.setEnabled(false);

        BtnCaractersiticaGasto.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        BtnCaractersiticaGasto.setText("CARACTERSITICAS DEL GASTO");

        jButton1.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jButton1.setText("CARACTERSITICAS DEL GASTO");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jButton2.setText("GASTOS DIARIOS");

        jButton3.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jButton3.setText("COMIDAS PREPARADAS FUERA DEL HOGAR");

        jTextField3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTextField3.setText("Resultado cuadernillo");
        jTextField3.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextField3.setEnabled(false);

        RdbComplet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        RdbComplet.setText("Completo");

        RdbIncompleto.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        RdbIncompleto.setText("Incompleto");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setText("Completo: aprueba la encuesta y el encuestador ya no podra modificarla.\nIncompleta: la devuelve al encuestador para que la corrija\n");
        jTextArea1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextArea1.setEnabled(false);
        jScrollPane1.setViewportView(jTextArea1);

        btnGuardar.setBackground(new java.awt.Color(0, 255, 51));
        btnGuardar.setText("Guardar");

        BtnVolver.setBackground(new java.awt.Color(51, 204, 255));
        BtnVolver.setText("Volver");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BtnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(RdbComplet, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(RdbIncompleto, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BtnCaractersiticaGasto, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                            .addComponent(jTextField2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
                        .addGap(36, 36, 36))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnCaractersiticaGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RdbComplet, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RdbIncompleto, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnVolver, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Botón de la derecha, arriba: CARACTERÍSTICAS DEL GASTO. */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        abrirVentana(() -> new CaracteristicasDelGastoHogar(this, idHogar));
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnCaractersiticaGasto;
    private javax.swing.JButton BtnVolver;
    private javax.swing.JRadioButton RdbComplet;
    private javax.swing.JRadioButton RdbIncompleto;
    private javax.swing.JButton btnGuardar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
