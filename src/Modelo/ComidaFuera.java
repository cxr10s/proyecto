package Modelo;

/**
 * Modelo de la tabla COMIDAS_FUERA_HOGAR (una fila = una comida preparada fuera del hogar).
 * idHogar es el hogar; el DAO busca solo su num_encuesta (ENC001...) para guardarlo.
 * Los campos "nombre..." solo se llenan al listar (vienen de las tablas de catálogo).
 */
public class ComidaFuera {

    public static final String DESAYUNO = "D";
    public static final String ALMUERZO = "A";
    public static final String CENA = "C";
    public static final String ONCES = "O";

    private String idHogar;
    private int numOrden;
    private String descripcion;
    private String tipoComida;          // D, A, C u O (solo una)
    private int cantidad;
    private String idFormaAdquisicion;
    private String idLugarCompra;
    private long costo;
    private String idFrecuencia;
    private String tipoGasto;           // "1" = del hogar, "2" = personal
    private boolean domicilio;

    // Solo para mostrar en la tabla
    private String nombreForma;
    private String nombreLugar;
    private String nombreFrecuencia;

    public ComidaFuera() {
    }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public int getNumOrden() { return numOrden; }
    public void setNumOrden(int numOrden) { this.numOrden = numOrden; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoComida() { return tipoComida; }
    public void setTipoComida(String tipoComida) { this.tipoComida = tipoComida; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

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

    public boolean isDomicilio() { return domicilio; }
    public void setDomicilio(boolean domicilio) { this.domicilio = domicilio; }

    public String getNombreForma() { return nombreForma; }
    public void setNombreForma(String nombreForma) { this.nombreForma = nombreForma; }

    public String getNombreLugar() { return nombreLugar; }
    public void setNombreLugar(String nombreLugar) { this.nombreLugar = nombreLugar; }

    public String getNombreFrecuencia() { return nombreFrecuencia; }
    public void setNombreFrecuencia(String nombreFrecuencia) { this.nombreFrecuencia = nombreFrecuencia; }
}
