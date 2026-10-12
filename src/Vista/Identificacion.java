/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Vista;

import Modelo.Identificador;
import Modelo.IdentificadorDAO;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author ASUS
 */
public class Identificacion extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Identificacion.class.getName());

    /**
     * Creates new form InterfazEncuestador
     */
    private final Inicio padre;               // ventana principal (puede ser null)
    private final String idHogarEditar;       // null = modo agregar, con valor = modo modificar
    private final IdentificadorDAO dao = new IdentificadorDAO();

    public Identificacion() {
        this(null, null);
    }

    public Identificacion(Inicio padre, String idHogarEditar) {
        this.padre = padre;
        this.idHogarEditar = idHogarEditar;
        initComponents();

        // Cerrar este formulario NO debe cerrar toda la aplicación
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Región / departamento / municipio: fijos y bloqueados (ya están deshabilitados)
        TxfRegion.setText("Oriental");
        TxfDepartamento.setText("Santander");
        TxfMunicipio.setText("Lebrija");

        BntGuardar.addActionListener(e -> guardar());
        BtnCancelar.addActionListener(e -> cancelar());

        // Se cierre por GUARDAR, CANCELAR o la X: se vuelve a la pantalla principal
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                volverAlInicio();
            }
        });

        if (idHogarEditar != null) {
            setTitle("Modificar hogar");
            cargarDatos();
        } else {
            setTitle("Agregar hogar");
        }
    }

    // ------------------------------------------------------------------
    // Botones
    // ------------------------------------------------------------------
    private void guardar() {
        Identificador h = leerFormulario();
        if (h == null) {
            return;   // ya se mostró el mensaje de validación
        }
        try {
            if (idHogarEditar == null) {
                dao.agregar(h);
                JOptionPane.showMessageDialog(this, "Hogar guardado correctamente.");
            } else {
                h.setIdHogar(idHogarEditar);
                dao.modificar(h);
                JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.");
            }
            dispose();   // dispara windowClosed -> vuelve a Inicio y refresca la tabla
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        dispose();   // vuelve a Inicio sin guardar nada
    }

    private void volverAlInicio() {
        if (padre != null) {
            padre.cargarTabla();
            padre.setVisible(true);
        }
    }

    // ------------------------------------------------------------------
    // Formulario <-> objeto
    // ------------------------------------------------------------------
    /** Llena el formulario con los datos del hogar a modificar. */
    private void cargarDatos() {
        try {
            Identificador h = dao.buscarPorId(idHogarEditar);
            if (h == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el hogar " + idHogarEditar + ".");
                dispose();
                return;
            }
            CmbClase.setSelectedItem(h.getClase());
            RdbSeccion.setSelected("Urbano".equalsIgnoreCase(h.getSector()));
            TxfSeccion.setText(h.getSeccion());
            TxfManzana.setText(h.getManzana());
            TxfSegmento.setText(h.getSegmento());
            TxfCiclo.setText(h.getCiclo());
            TxfNumero_identificacion.setText(h.getNumEdificacion());
            TxfNumero_vivienda.setText(h.getNumVivienda());
            TxfTotal_hogares.setText(h.getTotalHogares());
            TxfNumero_hogar.setText(h.getNumHogar());
            TxfTotal_persona.setText(h.getTotalPersonas());
            Cmb_Barrio_centro.setSelectedItem(h.getBarrioCentroVereda());
            TxfDireccion.setText(h.getDireccion());
            TxfTelefono.setText(h.getTelefono());
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los datos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    /** Valida y arma el objeto. Devuelve null si algo está mal. */
    private Identificador leerFormulario() {
        String clase = String.valueOf(CmbClase.getSelectedItem()).trim();
        if (clase.isEmpty()) {
            aviso("Seleccione la clase.");
            CmbClase.requestFocus();
            return null;
        }
        if (vacio(TxfSeccion, "Sección") || vacio(TxfManzana, "Manzana")
                || vacio(TxfSegmento, "Segmento") || vacio(TxfCiclo, "Ciclo")
                || vacio(TxfNumero_identificacion, "Número de identificación de vivienda")
                || vacio(TxfNumero_vivienda, "Número de vivienda")
                || vacio(TxfTotal_hogares, "Total hogares de la vivienda")
                || vacio(TxfNumero_hogar, "Número del hogar")
                || vacio(TxfTotal_persona, "Total de personas en el hogar")
                || vacio(TxfDireccion, "Dirección")
                || vacio(TxfTelefono, "Teléfono")) {
            return null;
        }
        if (noNumerico(TxfTotal_hogares, "Total hogares de la vivienda")
                || noNumerico(TxfNumero_hogar, "Número del hogar")
                || noNumerico(TxfTotal_persona, "Total de personas en el hogar")) {
            return null;
        }

        Identificador h = new Identificador();
        h.setClase(clase);
        h.setSector(RdbSeccion.isSelected() ? "Urbano" : "Rural");
        h.setSeccion(TxfSeccion.getText().trim());
        h.setManzana(TxfManzana.getText().trim());
        h.setSegmento(TxfSegmento.getText().trim());
        h.setCiclo(TxfCiclo.getText().trim());
        h.setNumEdificacion(TxfNumero_identificacion.getText().trim());
        h.setNumVivienda(TxfNumero_vivienda.getText().trim());
        h.setTotalHogares(TxfTotal_hogares.getText().trim());
        h.setNumHogar(TxfNumero_hogar.getText().trim());
        h.setTotalPersonas(TxfTotal_persona.getText().trim());
        h.setBarrioCentroVereda(String.valueOf(Cmb_Barrio_centro.getSelectedItem()));
        h.setDireccion(TxfDireccion.getText().trim());
        h.setTelefono(TxfTelefono.getText().trim());
        return h;
    }

    private boolean vacio(JTextField campo, String nombre) {
        if (campo.getText().trim().isEmpty()) {
            aviso("El campo \"" + nombre + "\" es obligatorio.");
            campo.requestFocus();
            return true;
        }
        return false;
    }

    private boolean noNumerico(JTextField campo, String nombre) {
        if (!campo.getText().trim().matches("\\d+")) {
            aviso("El campo \"" + nombre + "\" debe contener solo números.");
            campo.requestFocus();
            return true;
        }
        return false;
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Datos incompletos", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        CmbClase = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        RdbSeccion = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        TxfSeccion = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        TxfManzana = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        TxfSegmento = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        TxfCiclo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        TxfNumero_identificacion = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        TxfNumero_vivienda = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        TxfTotal_hogares = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        TxfNumero_hogar = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        TxfTotal_persona = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        Cmb_Barrio_centro = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        TxfDireccion = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        TxfTelefono = new javax.swing.JTextField();
        TxfDepartamento = new javax.swing.JTextField();
        TxfMunicipio = new javax.swing.JTextField();
        TxfRegion = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        BntGuardar = new javax.swing.JButton();
        BtnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 36)); // NOI18N
        jLabel1.setText("Identificacion del hogar");
        jLabel1.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel2.setText("Region :");

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel3.setText("Departamento :");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel7.setText("Municipio :");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel8.setText("Clase");

        CmbClase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Clase 1", "Clase 2", "Clase 3", " " }));
        CmbClase.addActionListener(this::CmbClaseActionPerformed);

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel9.setText("Sector :");

        RdbSeccion.setText("Urbano\n");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel10.setText("Seccion :");

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel11.setText("Manzana :");

        jLabel12.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel12.setText("Segmento :");

        TxfSegmento.addActionListener(this::TxfSegmentoActionPerformed);

        jLabel13.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel13.setText("Ciclo :");

        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel14.setText("Numero de identificacion de vivienda :");

        TxfNumero_identificacion.addActionListener(this::TxfNumero_identificacionActionPerformed);

        jLabel15.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel15.setText("Numero De Vivienda :");

        jLabel16.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel16.setText("Total hogares de la vivenda :");

        jLabel17.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel17.setText("Numero del hogar :");

        TxfNumero_hogar.addActionListener(this::TxfNumero_hogarActionPerformed);

        jLabel18.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel18.setText("Total de personas en el hogar :");
        jLabel18.setToolTipText("");

        jLabel19.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel19.setText("Barrio, centro poblado o verda :");

        Cmb_Barrio_centro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Barrio", "Centro poblado", "Vereda" }));

        jLabel20.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel20.setText("Direccion de la vivienda o finca :");

        jLabel21.setFont(new java.awt.Font("Times New Roman", 0, 16)); // NOI18N
        jLabel21.setText("Telefono :");

        TxfDepartamento.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TxfDepartamento.setText("Santander");
        TxfDepartamento.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        TxfDepartamento.setEnabled(false);

        TxfMunicipio.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TxfMunicipio.setText("Lebrija");
        TxfMunicipio.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        TxfMunicipio.setEnabled(false);

        TxfRegion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TxfRegion.setText("Centro");
        TxfRegion.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        TxfRegion.setEnabled(false);
        TxfRegion.addActionListener(this::TxfRegionActionPerformed);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/dane (2).png"))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel5.setText("MATEO ALEJANDRO RODRÍGUEZ MARTÍNEZ");

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel6.setText("1015162895");

        jLabel22.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel22.setText("ENCUESTADOR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(22, 22, 22))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addGap(17, 17, 17)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel22)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        BntGuardar.setBackground(new java.awt.Color(51, 255, 51));
        BntGuardar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BntGuardar.setText("GUARDAR");

        BtnCancelar.setBackground(new java.awt.Color(255, 51, 51));
        BtnCancelar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        BtnCancelar.setText("CANCELAR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(RdbSeccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TxfDepartamento)
                            .addComponent(TxfRegion)
                            .addComponent(TxfMunicipio)
                            .addComponent(CmbClase, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(TxfSeccion)
                            .addComponent(TxfManzana, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(619, 619, 619)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(TxfSegmento, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                    .addComponent(TxfCiclo))
                                .addGap(242, 242, 242))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(TxfNumero_identificacion, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                                            .addComponent(TxfNumero_vivienda)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(21, 21, 21)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(TxfTotal_persona, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(TxfNumero_hogar, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(0, 0, Short.MAX_VALUE))
                                                    .addComponent(TxfDireccion)
                                                    .addComponent(TxfTelefono)
                                                    .addComponent(Cmb_Barrio_centro, 0, 184, Short.MAX_VALUE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(TxfTotal_hogares)))))
                                .addGap(58, 58, 58))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(BntGuardar)
                        .addGap(36, 36, 36)
                        .addComponent(BtnCancelar)
                        .addGap(45, 45, 45))))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(TxfNumero_identificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(TxfNumero_vivienda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(TxfTotal_hogares, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TxfNumero_hogar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(TxfTotal_persona, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(TxfSegmento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(TxfCiclo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addGap(18, 18, 18))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(TxfRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(13, 13, 13)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3)
                                    .addComponent(TxfDepartamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(TxfMunicipio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CmbClase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(34, 34, 34)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RdbSeccion))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(TxfSeccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(TxfManzana, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(Cmb_Barrio_centro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(TxfDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(TxfTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(83, 83, 83)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BntGuardar)
                    .addComponent(BtnCancelar))
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TxfSegmentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxfSegmentoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxfSegmentoActionPerformed

    private void CmbClaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbClaseActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbClaseActionPerformed

    private void TxfNumero_identificacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxfNumero_identificacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxfNumero_identificacionActionPerformed

    private void TxfNumero_hogarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxfNumero_hogarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxfNumero_hogarActionPerformed

    private void TxfRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxfRegionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxfRegionActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Identificacion().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BntGuardar;
    private javax.swing.JButton BtnCancelar;
    private javax.swing.JComboBox<String> CmbClase;
    private javax.swing.JComboBox<String> Cmb_Barrio_centro;
    private javax.swing.JRadioButton RdbSeccion;
    private javax.swing.JTextField TxfCiclo;
    private javax.swing.JTextField TxfDepartamento;
    private javax.swing.JTextField TxfDireccion;
    private javax.swing.JTextField TxfManzana;
    private javax.swing.JTextField TxfMunicipio;
    private javax.swing.JTextField TxfNumero_hogar;
    private javax.swing.JTextField TxfNumero_identificacion;
    private javax.swing.JTextField TxfNumero_vivienda;
    private javax.swing.JTextField TxfRegion;
    private javax.swing.JTextField TxfSeccion;
    private javax.swing.JTextField TxfSegmento;
    private javax.swing.JTextField TxfTelefono;
    private javax.swing.JTextField TxfTotal_hogares;
    private javax.swing.JTextField TxfTotal_persona;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollBar jScrollBar1;
    // End of variables declaration//GEN-END:variables
}
