package Modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * El resultado de uno de los 5 reportes del supervisor: título, columnas y filas.
 *
 * tipos (uno por columna):
 *   'T' texto, 'N' número entero, 'M' dinero ($), 'D' decimal con 1 cifra, 'P' porcentaje.
 * sumar (uno por columna): si es true, en la pantalla se agrega una fila TOTAL con la suma.
 */
public class ResultadoReporte {

    private final int numero;
    private final String titulo;
    private final String descripcion;
    private final String[] columnas;
    private final char[] tipos;
    private final boolean[] sumar;
    private final List<Object[]> filas = new ArrayList<>();

    public ResultadoReporte(int numero, String titulo, String descripcion, String[] columnas,
            char[] tipos, boolean[] sumar) {
        this.numero = numero;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.columnas = columnas;
        this.tipos = tipos;
        this.sumar = sumar;
    }

    public int getNumero() { return numero; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String[] getColumnas() { return columnas; }
    public char[] getTipos() { return tipos; }
    public boolean[] getSumar() { return sumar; }
    public List<Object[]> getFilas() { return filas; }
}
