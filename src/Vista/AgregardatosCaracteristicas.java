/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.CaracteristicaGasto;
import Modelo.CaracteristicaGastoDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import java.text.ParseException;
import javax.swing.JOptionPane;

/**
 *
 * @author ASUS
 */
public class AgregardatosCaracteristicas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AgregardatosCaracteristicas.class.getName());

    /**
     * Creates new form AgregardatosCaracteristicas
     */
    private final CaracteristicasDelGastoHogar padre;   // ventana de la lista (puede ser null)
    private final String idHogar;                       // hogar al que pertenece el alimento
    private final String codigoEditar;                  // null = agregar; con valor ("01".."25") = modificar
    private final CaracteristicaGastoDAO dao = new CaracteristicaGastoDAO();
    private javax.swing.JRadioButton[] radiosFrecuencia;   // en el mismo orden que CaracteristicaGasto.FREC_*

    public AgregardatosCaracteristicas() {
        this(null, null, null);
    }

    public AgregardatosCaracteristicas(CaracteristicasDelGastoHogar padre, String idHogar, String codigoEditar) {
        this.padre = padre;
        this.idHogar = idHogar;
        this.codigoEditar = codigoEditar;
        initComponents();
        
    // Configurar el spinner
    SpiValorCompra.setModel(
        new javax.swing.SpinnerNumberModel(0, 0, 10000000, 10000)
    );

    // Grupo de frecuencia de compra
    javax.swing.ButtonGroup grupoFrecuencia =
        new javax.swing.ButtonGroup();

    grupoFrecuencia.add(RdbNunca); // Nunca
    grupoFrecuencia.add(RdbDiario); // Diario
    grupoFrecuencia.add(RdbVarias_Veces); // Varias veces por semana
    grupoFrecuencia.add(RdbSemanal); // Semanal
    grupoFrecuencia.add(RdbQuincenal); // Quincenal
    grupoFrecuencia.add(RdbMensual); // Mensual
    grupoFrecuencia.add(RdbBimestral); // Bimestral
    grupoFrecuencia.add(RdbTrimestral); // Trimestral
    grupoFrecuencia.add(RdbEsporadico); // Esporádico

    // Grupo de compra durante los próximos 14 días
    javax.swing.ButtonGroup grupoCompra =
        new javax.swing.ButtonGroup();

    grupoCompra.add(RdbSi); // Sí
    grupoCompra.add(RdbNo); // No

    // Ocultar componentes adicionales al iniciar
    mostrarComponentesAdicionales(false);
    
    //actualizar visibilidad cuando se cambie de frecuencia (constructor)
    RdbNunca.addActionListener(e -> actualizarVisibilidad());
    RdbDiario.addActionListener(e -> actualizarVisibilidad());
    RdbVarias_Veces.addActionListener(e -> actualizarVisibilidad());
    RdbSemanal.addActionListener(e -> actualizarVisibilidad());
    RdbQuincenal.addActionListener(e -> actualizarVisibilidad());
    RdbMensual.addActionListener(e -> actualizarVisibilidad());
    RdbBimestral.addActionListener(e -> actualizarVisibilidad());
    RdbTrimestral.addActionListener(e -> actualizarVisibilidad());
    RdbEsporadico.addActionListener(e -> actualizarVisibilidad());

    // --- Conexión con la lista y con la base de datos ---
    radiosFrecuencia = new javax.swing.JRadioButton[]{RdbNunca, RdbDiario, RdbVarias_Veces,
        RdbSemanal, RdbQuincenal, RdbMensual, RdbBimestral, RdbTrimestral, RdbEsporadico};
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);   // cerrar NO cierra la aplicación
    setLocationRelativeTo(null);
    BtnGuardar.addActionListener(e -> guardar());      // GUARDAR
    BtnVolver.addActionListener(e -> dispose());      // VOLVER (CANCELAR ya cierra desde su método)

    // Se cierre por GUARDAR, CANCELAR, VOLVER o la X: se vuelve a la lista y se refresca
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            if (padre != null) {
                padre.cargarTabla();
                padre.setVisible(true);
            }
        }
    });

    if (codigoEditar != null) {
        setTitle("Modificar alimento - Hogar " + idHogar);
        CobAlimento.setEnabled(false);       // el alimento identifica el registro: no se cambia
        cargarDatos();
    } else if (idHogar != null) {
        setTitle("Agregar alimento - Hogar " + idHogar);
    }
    }
    //ocultar componentes cuando no se seleccione los que se requiere para mostrarse
    private void mostrarComponentesAdicionales(boolean mostrar) {
    jScrollPane1.setVisible(mostrar);
    jLabel1.setVisible(mostrar);
    SpiValorCompra.setVisible(mostrar);
    jLabel5.setVisible(mostrar);
    RdbSi.setVisible(mostrar);
    RdbNo.setVisible(mostrar);
    }
    
    //este metodo es para comprobar la frecuencia seleccionada (frecuencia en el constructor)
    private void actualizarVisibilidad() {

    boolean mostrar =
        RdbMensual.isSelected() || // Mensual
        RdbBimestral.isSelected() || // Bimestral
        RdbTrimestral.isSelected() || // Trimestral
        RdbEsporadico.isSelected();   // Esporádico

    mostrarComponentesAdicionales(mostrar);
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        CobAlimento = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        RdbNunca = new javax.swing.JRadioButton();
        RdbDiario = new javax.swing.JRadioButton();
        RdbVarias_Veces = new javax.swing.JRadioButton();
        RdbSemanal = new javax.swing.JRadioButton();
        RdbQuincenal = new javax.swing.JRadioButton();
        RdbMensual = new javax.swing.JRadioButton();
        RdbBimestral = new javax.swing.JRadioButton();
        RdbTrimestral = new javax.swing.JRadioButton();
        RdbEsporadico = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        RdbSi = new javax.swing.JRadioButton();
        RdbNo = new javax.swing.JRadioButton();
        BtnCancelar = new javax.swing.JButton();
        BtnGuardar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        SpiValorCompra = new javax.swing.JSpinner();
        BtnVolver = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TxAObservaciones = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Agregar caracteristica");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        jLabel2.setText("Caracteristica de los gastos en alimentos de los hogares");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 19)); // NOI18N
        jLabel3.setText("Alimento o grupos de alimento :");

        CobAlimento.setFont(new java.awt.Font("Times New Roman", 0, 15)); // NOI18N
        CobAlimento.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pan, arepas, bollos y almojábanas", "Galletas de sal y de dulce", "Arroz, pastas alimenticias, avena, maíz, harinas y otros cereales (para el desayuno)", "Carne de res, cerdo, hueso y vísceras", "Pollo, otras aves y menudencias", "Salchichas, jamón, mortadela, salchichón y otras carnes frías preparadas (incluidos los embutidos vegetarianos)", "Pescado de río, mar y otros productos marinos (frescos, congelados y enlatados)", "Leche (animal y vegetal)", "Queso (animal y vegetal)", "Otros derivados de la leche: yogur, kumis, etc.", "Huevos", "Aceites y grasas: aceite, manteca, mantequilla, margarina, etc.", "Frutas", "Verduras y hortalizas: tomate, cebolla, arveja, espinaca, apio, zanahoria, etc.", "Granos secos: fríjol, lenteja, garbanzo, etc.", "Enlatados y encurtidos: arveja, fríjol, maíz, espárragos, zanahoria, habichuela, etc.", "Plátano, yuca, arracacha, ñame, papa y otros tubérculos", "Azúcar, panela y otros endulzantes", "Mermeladas, arequipe, bocadillos, compotas, dulces, helados, chocolatinas, ponquecitos, etc.", "Salsas, mayonesa, mostaza, vinagre, etc.", "Sal y condimentos", "Café, chocolate, otras bebidas con cacao (milo, chocolisto), té y hierbas aromáticas", "Papas fritas, chitos, maicitos, patacones, besitos, maní, etc.", "Agua, gaseosas, refrescos, jugos, té frío y otras bebidas no alcohólicas", "Alimentos y bebidas preparados fuera, del hogar, para consumir dentro o fuera, del hogar" }));
        CobAlimento.addActionListener(this::CobAlimentoActionPerformed);

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel4.setText("Frecuencia de compra :");

        RdbNunca.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbNunca.setText("Nunca");

        RdbDiario.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbDiario.setText("Diario");
        RdbDiario.addActionListener(this::RdbDiarioActionPerformed);

        RdbVarias_Veces.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbVarias_Veces.setText("Varias veces por semana");

        RdbSemanal.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbSemanal.setText("Semanal");

        RdbQuincenal.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbQuincenal.setText("Quincenal");

        RdbMensual.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbMensual.setText("Mensual");

        RdbBimestral.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbBimestral.setText("Bimestral");

        RdbTrimestral.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbTrimestral.setText("Trimestral");
        RdbTrimestral.addActionListener(this::RdbTrimestralActionPerformed);

        RdbEsporadico.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        RdbEsporadico.setText("Esporadico");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Si la frecuencia de compra del alimento \no grupo de alimentos es superior a la \nbisemana (mensual, bimestral, trimestral\no esporádica) pregunte para cada uno de ellos:");
        jTextArea1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextArea1.setEnabled(false);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("¿Cuál fue el valor de la última compra?");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setText("¿Comprará este alimento o grupo de alimentos durante los 14 días que dura la encuesta?");

        RdbSi.setText("Si");

        RdbNo.setText("No");

        BtnCancelar.setBackground(new java.awt.Color(255, 51, 51));
        BtnCancelar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnCancelar.setText("CANCELAR");
        BtnCancelar.addActionListener(this::BtnCancelarActionPerformed);

        BtnGuardar.setBackground(new java.awt.Color(51, 255, 51));
        BtnGuardar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnGuardar.setText("GUARDAR");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dane (2).png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel7.setText("MATEO ALEJANDRO RODRÍGUEZ MARTÍNEZ");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel8.setText("1015162895");

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel9.setText("ENCUESTADOR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 587, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9))
                    .addComponent(jLabel6))
                .addGap(0, 9, Short.MAX_VALUE))
        );

        BtnVolver.setBackground(new java.awt.Color(0, 153, 255));
        BtnVolver.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnVolver.setText("VOLVER");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Observaciones");

        TxAObservaciones.setColumns(20);
        TxAObservaciones.setRows(5);
        jScrollPane2.setViewportView(TxAObservaciones);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(BtnVolver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnGuardar)
                .addGap(36, 36, 36)
                .addComponent(BtnCancelar)
                .addGap(45, 45, 45))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(110, 110, 110)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CobAlimento, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(RdbNunca, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbDiario, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbVarias_Veces, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbSemanal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbQuincenal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbMensual, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbBimestral, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbTrimestral, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(RdbEsporadico, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 576, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SpiValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(RdbSi, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(RdbNo, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(98, 98, 98))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CobAlimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(66, 66, 66)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RdbNunca)
                    .addComponent(RdbDiario)
                    .addComponent(RdbVarias_Veces)
                    .addComponent(RdbSemanal)
                    .addComponent(RdbQuincenal)
                    .addComponent(RdbMensual)
                    .addComponent(RdbBimestral)
                    .addComponent(RdbTrimestral)
                    .addComponent(RdbEsporadico))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(SpiValorCompra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(RdbSi)
                            .addComponent(RdbNo))
                        .addGap(13, 13, 13)))
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnGuardar)
                    .addComponent(BtnCancelar)
                    .addComponent(BtnVolver))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CobAlimentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CobAlimentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CobAlimentoActionPerformed

    private void RdbDiarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RdbDiarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RdbDiarioActionPerformed

    private void RdbTrimestralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RdbTrimestralActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RdbTrimestralActionPerformed

    private void BtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCancelarActionPerformed
        dispose();   // CANCELAR: vuelve a la lista sin guardar
    }//GEN-LAST:event_BtnCancelarActionPerformed

    /** Llena el formulario con el alimento a modificar. */
    private void cargarDatos() {
        try {
            CaracteristicaGasto g = dao.buscar(idHogar, codigoEditar);
            if (g == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el alimento.");
                dispose();
                return;
            }
            int indice = Integer.parseInt(codigoEditar) - 1;
            if (indice >= 0 && indice < CobAlimento.getItemCount()) {
                CobAlimento.setSelectedIndex(indice);
            }
            if (g.getFrecuencia() >= 0 && g.getFrecuencia() < radiosFrecuencia.length) {
                radiosFrecuencia[g.getFrecuencia()].setSelected(true);
            }
            actualizarVisibilidad();    // muestra valor y "¿comprará?" si la frecuencia lo pide
            if (g.getValorUltimaCompra() != null) {
                SpiValorCompra.setValue((int) Math.min(g.getValorUltimaCompra(), Integer.MAX_VALUE));
            }
            RdbSi.setSelected("Si".equals(g.getComprara()));
            RdbNo.setSelected("No".equals(g.getComprara()));
        } catch (SQLException | NumberFormatException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los datos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    /** Valida el formulario y guarda (agregar o modificar según el modo). */
    private void guardar() {
        if (idHogar == null) {
            JOptionPane.showMessageDialog(this, "Abra esta ventana desde un hogar.",
                    "Guardar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int frecuencia = -1;
        for (int i = 0; i < radiosFrecuencia.length; i++) {
            if (radiosFrecuencia[i].isSelected()) {
                frecuencia = i;
                break;
            }
        }
        if (frecuencia < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione la frecuencia de compra.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long valor = null;
        String comprara = CaracteristicaGasto.NO_APLICA;
        if (CaracteristicaGasto.requiereDetalle(frecuencia)) {
            try {
                SpiValorCompra.commitEdit();     // por si escribió el número y no presionó Enter
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(this, "Revise el valor de la última compra.",
                        "Datos incorrectos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            long v = ((Number) SpiValorCompra.getValue()).longValue();
            if (v <= 0) {
                JOptionPane.showMessageDialog(this, "Indique el valor de la última compra (mayor que 0).",
                        "Datos incorrectos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!RdbSi.isSelected() && !RdbNo.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Indique si comprará este alimento durante los 14 días de la encuesta.",
                        "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            valor = v;
            comprara = RdbSi.isSelected() ? "Si" : "No";
        }

        CaracteristicaGasto g = new CaracteristicaGasto();
        g.setIdHogar(idHogar);
        if (codigoEditar != null) {
            g.setCodigoAlimento(codigoEditar);
        } else {
            g.setCodigoAlimento(String.format("%02d", CobAlimento.getSelectedIndex() + 1));
        }
        g.setNombreAlimento(String.valueOf(CobAlimento.getSelectedItem()));
        g.setFrecuencia(frecuencia);
        g.setValorUltimaCompra(valor);
        g.setComprara(comprara);

        try {
            if (codigoEditar == null) {
                dao.agregar(g);
                JOptionPane.showMessageDialog(this, "Alimento guardado correctamente.");
            } else {
                dao.modificar(g);
                JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.");
            }
            dispose();   // dispara windowClosed -> vuelve a la lista y la refresca
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "No se pudo guardar:\n" + ex.getMessage(),
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
        java.awt.EventQueue.invokeLater(() -> new AgregardatosCaracteristicas().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnCancelar;
    private javax.swing.JButton BtnGuardar;
    private javax.swing.JButton BtnVolver;
    private javax.swing.JComboBox<String> CobAlimento;
    private javax.swing.JRadioButton RdbBimestral;
    private javax.swing.JRadioButton RdbDiario;
    private javax.swing.JRadioButton RdbEsporadico;
    private javax.swing.JRadioButton RdbMensual;
    private javax.swing.JRadioButton RdbNo;
    private javax.swing.JRadioButton RdbNunca;
    private javax.swing.JRadioButton RdbQuincenal;
    private javax.swing.JRadioButton RdbSemanal;
    private javax.swing.JRadioButton RdbSi;
    private javax.swing.JRadioButton RdbTrimestral;
    private javax.swing.JRadioButton RdbVarias_Veces;
    private javax.swing.JSpinner SpiValorCompra;
    private javax.swing.JTextArea TxAObservaciones;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
