package Modelo;

/**
 * Modelo de la tabla CARACTERISTICAS_GASTOS (una fila = un alimento o grupo de alimentos de un hogar).
 * idHogar es el hogar; el DAO busca solo su num_encuesta (ENC001...) para guardarlo.
 */
public class CaracteristicaGasto {

    /** Columnas de frecuencia de la tabla, en el mismo orden de los radios del formulario. */
    public static final String[] FREC_COLUMNAS = {
        "frec_nunca", "frec_diario", "frec_varias_veces_por_semana", "frec_semanal",
        "frec_quincenal", "frec_mensual", "frec_bimestral", "frec_trimestral", "frec_esporadico"};

    /** Nombres para mostrar, en el mismo orden. */
    public static final String[] FREC_NOMBRES = {
        "Nunca", "Diario", "Varias veces por semana", "Semanal",
        "Quincenal", "Mensual", "Bimestral", "Trimestral", "Esporádico"};

    /** Valor de comprara_de_nuevo cuando la pregunta no aplica (frecuencia de la bisemana o menos). */
    public static final String NO_APLICA = "NA";

    private String idHogar;
    private String codigoAlimento;      // "01" ... "25" (posición en la lista del formulario)
    private String nombreAlimento;
    private int frecuencia;             // índice en FREC_COLUMNAS (0 = Nunca ... 8 = Esporádico)
    private Long valorUltimaCompra;     // solo si la frecuencia es mensual o mayor
    private String comprara;            // "Si", "No" o "NA"
    private String observaciones;

    public CaracteristicaGasto() {
    }

    /** Mensual, bimestral, trimestral o esporádico: se pregunta valor y si comprará de nuevo. */
    public static boolean requiereDetalle(int frecuencia) {
        return frecuencia >= 5;
    }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public String getCodigoAlimento() { return codigoAlimento; }
    public void setCodigoAlimento(String codigoAlimento) { this.codigoAlimento = codigoAlimento; }

    public String getNombreAlimento() { return nombreAlimento; }
    public void setNombreAlimento(String nombreAlimento) { this.nombreAlimento = nombreAlimento; }

    public int getFrecuencia() { return frecuencia; }
    public void setFrecuencia(int frecuencia) { this.frecuencia = frecuencia; }

    public Long getValorUltimaCompra() { return valorUltimaCompra; }
    public void setValorUltimaCompra(Long valorUltimaCompra) { this.valorUltimaCompra = valorUltimaCompra; }

    public String getComprara() { return comprara; }
    public void setComprara(String comprara) { this.comprara = comprara; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
