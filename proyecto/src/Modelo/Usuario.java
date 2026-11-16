package Modelo;

/**
 * Usuario de la aplicación (encuestador o supervisor). Reemplaza a la clase Encuestador.
 */
public class Usuario {

    private final String cedula;
    private final String nombres;
    private final String apellidos;
    private final String rol;   // "encuestador" o "supervisor"

    public Usuario(String cedula, String nombres, String apellidos, String rol) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol == null ? "" : rol.trim().toLowerCase();
    }

    public String getCedula() { return cedula; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getRol() { return rol; }

    public boolean esSupervisor() { return "supervisor".equals(rol); }
    public boolean esEncuestador() { return "encuestador".equals(rol); }

    public String getNombreCompleto() {
        return (nombres + " " + apellidos).trim();
    }
}
