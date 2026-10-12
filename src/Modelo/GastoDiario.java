package Modelo;

import java.time.LocalDate;

/**
 * Modelo de la tabla GASTOS_DIARIOS_PERSONALES (una fila = un artículo o servicio).
 * idHogar es el hogar; el DAO busca solo su num_encuesta (ENC001...) para guardarlo.
 * Los campos "nombre..." solo se llenan al listar (vienen de las tablas de catálogo).
 */
public class GastoDiario {

    private String idHogar;
    private int numOrden;
    private LocalDate fecha;
    private String nombreArticulo;
    private int cantidad;
    private String idMedida;
    private String equivalencia;
    private String idFormaAdquisicion;
    private String idLugarCompra;
    private long costo;
    private String idFrecuencia;
    private String tipoGasto;          // "1" = del hogar, "2" = personal

    // Solo para mostrar en la tabla
    private String nombreMedida;
    private String nombreForma;
    private String nombreLugar;
    private String nombreFrecuencia;

    public GastoDiario() {
    }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public int getNumOrden() { return numOrden; }
    public void setNumOrden(int numOrden) { this.numOrden = numOrden; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getIdMedida() { return idMedida; }
    public void setIdMedida(String idMedida) { this.idMedida = idMedida; }

    public String getEquivalencia() { return equivalencia; }
    public void setEquivalencia(String equivalencia) { this.equivalencia = equivalencia; }

    public String getIdFormaAdquisicion() { return idFormaAdquisicion; }
    public void setIdFormaAdquisicion(String idFormaAdquisicion) { this.idFormaAdquisicion = idFormaAdquisicion; }

    public String getIdLugarCompra() { return idLugarCompra; }
    public void setIdLugarCompra(String idLugarCompra) { this.idLugarCompra = idLugarCompra; }

    public long getCosto() { return costo; }
    public void setCosto(long costo) { this.costo = costo; }

    public String getIdFrecuencia() { return idFrecuencia; }
    public void setIdFrecuencia(String idFrecuencia) { this.idFrecuencia = idFrecuencia; }

    public String getTipoGasto() { return tipoGasto; }
    public void setTipoGasto(String tipoGasto) { this.tipoGasto = tipoGasto; }

    public String getNombreMedida() { return nombreMedida; }
    public void setNombreMedida(String nombreMedida) { this.nombreMedida = nombreMedida; }

    public String getNombreForma() { return nombreForma; }
    public void setNombreForma(String nombreForma) { this.nombreForma = nombreForma; }

    public String getNombreLugar() { return nombreLugar; }
    public void setNombreLugar(String nombreLugar) { this.nombreLugar = nombreLugar; }

    public String getNombreFrecuencia() { return nombreFrecuencia; }
    public void setNombreFrecuencia(String nombreFrecuencia) { this.nombreFrecuencia = nombreFrecuencia; }
}
