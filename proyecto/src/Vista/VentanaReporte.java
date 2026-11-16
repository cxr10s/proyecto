package Vista;

import Modelo.ResultadoReporte;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Muestra un reporte del supervisor en una tabla (con fila TOTAL en las columnas que se suman)
 * y permite exportarlo a CSV (se abre directo en Excel). Está escrita a mano (no tiene .form).
 */
public class VentanaReporte extends JDialog {

    private static final Locale CO = new Locale("es", "CO");

    private final ResultadoReporte reporte;
    private final List<String[]> filasCsv = new ArrayList<>();   // lo que se exporta (con la fila TOTAL)

    public VentanaReporte(JFrame padre, ResultadoReporte reporte) {
        super(padre, "Reporte " + reporte.getNumero() + ": " + reporte.getTitulo(), true);
        this.reporte = reporte;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(0, 8));
        getContentPane().add(construirTitulo(), BorderLayout.NORTH);
        getContentPane().add(construirTabla(), BorderLayout.CENTER);
        getContentPane().add(construirPie(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(760, 420));
        setLocationRelativeTo(padre);
    }

    // ------------------------------------------------------------------
    private JPanel construirTitulo() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBorder(BorderFactory.createEmptyBorder(12, 16, 0, 16));
        JLabel titulo = new JLabel("Reporte " + reporte.getNumero() + ": " + reporte.getTitulo());
        titulo.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        JLabel descripcion = new JLabel("<html>" + reporte.getDescripcion() + "</html>");
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(titulo, BorderLayout.NORTH);
        p.add(descripcion, BorderLayout.CENTER);
        return p;
    }

    private JScrollPane construirTabla() {
        String[] columnas = reporte.getColumnas();
        char[] tipos = reporte.getTipos();
        boolean[] sumar = reporte.getSumar();

        DefaultTableModel modelo = new DefaultTableModel(new Object[][]{}, columnas) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        long[] sumas = new long[columnas.length];
        boolean haySumas = false;
        for (boolean s : sumar) {
            haySumas = haySumas || s;
        }

        for (Object[] fila : reporte.getFilas()) {
            String[] visible = new String[columnas.length];
            String[] csv = new String[columnas.length];
            for (int c = 0; c < columnas.length; c++) {
                visible[c] = formatear(fila[c], tipos[c], true);
                csv[c] = formatear(fila[c], tipos[c], false);
                if (sumar[c] && fila[c] instanceof Number) {
                    sumas[c] += ((Number) fila[c]).longValue();
                }
            }
            modelo.addRow(visible);
            filasCsv.add(csv);
        }

        if (haySumas && !reporte.getFilas().isEmpty()) {
            String[] visible = new String[columnas.length];
            String[] csv = new String[columnas.length];
            visible[0] = "TOTAL";
            csv[0] = "TOTAL";
            for (int c = 1; c < columnas.length; c++) {
                if (sumar[c]) {
                    visible[c] = formatear(new BigDecimal(sumas[c]), tipos[c], true);
                    csv[c] = formatear(new BigDecimal(sumas[c]), tipos[c], false);
                } else {
                    visible[c] = "";
                    csv[c] = "";
                }
            }
            modelo.addRow(visible);
            filasCsv.add(csv);
        }

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(24);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int c = 0; c < columnas.length; c++) {
            if (tipos[c] != 'T') {
                tabla.getColumnModel().getColumn(c).setCellRenderer(derecha);
            }
        }

        // La fila TOTAL (la última, si hay sumas) se pinta distinta
        final boolean conTotal = haySumas && !reporte.getFilas().isEmpty();
        final int ultima = modelo.getRowCount() - 1;
        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                    boolean foco, int fila, int columna) {
                java.awt.Component c = super.getTableCellRendererComponent(t, valor, sel, foco, fila, columna);
                boolean esTotal = conTotal && fila == ultima;
                c.setFont(c.getFont().deriveFont(esTotal ? Font.BOLD : Font.PLAIN));
                if (!sel) {
                    c.setBackground(esTotal ? new Color(230, 230, 230) : Color.WHITE);
                }
                setHorizontalAlignment(reporte.getTipos()[columna] == 'T' ? SwingConstants.LEFT : SwingConstants.RIGHT);
                return c;
            }
        };
        for (int c = 0; c < columnas.length; c++) {
            tabla.getColumnModel().getColumn(c).setCellRenderer(render);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        scroll.setPreferredSize(new Dimension(900, 360));
        return scroll;
    }

    private JPanel construirPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

        int n = reporte.getFilas().size();
        JLabel resumen = new JLabel(n == 0 ? "No hay datos para este reporte."
                : n + (n == 1 ? " fila" : " filas"));
        resumen.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pie.add(resumen, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton exportar = new JButton("EXPORTAR A CSV");
        exportar.setEnabled(n > 0);
        exportar.addActionListener(e -> exportar());
        JButton cerrar = new JButton("CERRAR");
        cerrar.setBackground(new Color(0, 204, 255));
        cerrar.addActionListener(e -> dispose());
        botones.add(exportar);
        botones.add(cerrar);
        pie.add(botones, BorderLayout.EAST);
        return pie;
    }

    // ------------------------------------------------------------------
    /** Texto de una celda. visible = true: con formato para la pantalla; false: para el CSV. */
    private static String formatear(Object valor, char tipo, boolean visible) {
        if (valor == null) {
            return "";
        }
        switch (tipo) {
            case 'N':
                return String.valueOf(((Number) valor).longValue());
            case 'M':
                long dinero = ((Number) valor).longValue();
                return visible ? String.format(CO, "$ %,d", dinero) : String.valueOf(dinero);
            case 'D':
                return String.format(CO, "%.1f", ((Number) valor).doubleValue());
            case 'P':
                return visible ? String.format(CO, "%.1f %%", ((Number) valor).doubleValue())
                        : String.format(CO, "%.1f", ((Number) valor).doubleValue());
            default:
                return valor.toString().trim();
        }
    }

    /** Guarda el reporte en un archivo CSV (separado por punto y coma, para abrirlo en Excel). */
    private void exportar() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte");
        selector.setSelectedFile(new File("reporte" + reporte.getNumero() + ".csv"));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getPath() + ".csv");
        }
        if (archivo.exists() && JOptionPane.showConfirmDialog(this,
                "El archivo ya existe. ¿Desea reemplazarlo?", "Guardar reporte",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try (Writer w = new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8)) {
            w.write('\uFEFF');   // para que Excel respete las tildes
            w.write(linea(reporte.getColumnas()));
            for (String[] fila : filasCsv) {
                w.write(linea(fila));
            }
            JOptionPane.showMessageDialog(this, "Reporte guardado en:\n" + archivo.getAbsolutePath(),
                    "Guardar reporte", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el archivo:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String linea(String[] celdas) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < celdas.length; i++) {
            if (i > 0) {
                sb.append(';');
            }
            String c = celdas[i] == null ? "" : celdas[i];
            if (c.contains(";") || c.contains("\"") || c.contains("\n")) {
                c = "\"" + c.replace("\"", "\"\"") + "\"";
            }
            sb.append(c);
        }
        return sb.append("\r\n").toString();
    }
}
