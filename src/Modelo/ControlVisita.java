package Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Modelo de la tabla CONTROL_VISITAS (una fila = una visita a un hogar).
 * idHogar se guarda en la columna num_encuesta (que referencia identificador.id_hogar).
 */
public class ControlVisita {

    private String idHogar;
    private int numVisita;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private String jornadaInicio;
    private LocalTime horaFin;
    private String jornadaFin;
    private String resultado;
    private String pendiente;          // personas y/o partes del cuadernillo pendientes
    private LocalDate diaProxima;      // opcional
    private LocalTime horaProxima;     // opcional

    public ControlVisita() {
    }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public int getNumVisita() { return numVisita; }
    public void setNumVisita(int numVisita) { this.numVisita = numVisita; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public String getJornadaInicio() { return jornadaInicio; }
    public void setJornadaInicio(String jornadaInicio) { this.jornadaInicio = jornadaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getJornadaFin() { return jornadaFin; }
    public void setJornadaFin(String jornadaFin) { this.jornadaFin = jornadaFin; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getPendiente() { return pendiente; }
    public void setPendiente(String pendiente) { this.pendiente = pendiente; }

    public LocalDate getDiaProxima() { return diaProxima; }
    public void setDiaProxima(LocalDate diaProxima) { this.diaProxima = diaProxima; }

    public LocalTime getHoraProxima() { return horaProxima; }
    public void setHoraProxima(LocalTime horaProxima) { this.horaProxima = horaProxima; }
}
