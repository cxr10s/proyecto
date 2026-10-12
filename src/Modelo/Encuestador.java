package Modelo;

/** Encuestador que inicia sesión (fila de la tabla ENCUESTADOR, sin la contraseña). */
public class Encuestador {

    private final String cedula;
    private final String nombres;
    private final String apellidos;
    private final String usuario;
    private final String cedulaSupervisor;

    public Encuestador(String cedula, String nombres, String apellidos, String usuario, String cedulaSupervisor) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.usuario = usuario;
        this.cedulaSupervisor = cedulaSupervisor;
    }

    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getUsuario() { return usuario; }
    public String getCedulaSupervisor() { return cedulaSupervisor; }

    public String getNombreCompleto() {
        return ((nombres == null ? "" : nombres.trim()) + " " + (apellidos == null ? "" : apellidos.trim())).trim();
    }
}
