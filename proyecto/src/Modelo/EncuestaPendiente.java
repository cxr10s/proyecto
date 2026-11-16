package Modelo;

import java.time.LocalDate;

/** Una fila de la lista que ve el supervisor: una encuesta (hogar) y quién la hizo. */
public class EncuestaPendiente {

    private String idHogar;
    private String cedula;          // cédula del encuestador
    private String encuestador;     // nombre completo del encuestador
    private String municipio;
    private LocalDate ultimaVisita; // puede ser null (todavía sin visitas)
    private String estado;          // Sin revisar / En revision / Aprobado / Rechazado
    private LocalDate fechaRevision; // solo en las encuestas ya revisadas (puede ser null)

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public String getEncuestador() { return encuestador; }
    public void setEncuestador(String encuestador) { this.encuestador = encuestador; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }
    public LocalDate getUltimaVisita() { return ultimaVisita; }
    public void setUltimaVisita(LocalDate ultimaVisita) { this.ultimaVisita = ultimaVisita; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDate fechaRevision) { this.fechaRevision = fechaRevision; }
}
