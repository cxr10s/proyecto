package Modelo;

/**
 * Modelo de la tabla IDENTIFICADOR (identificación del hogar).
 * Los campos municipio, estado y validacion solo se usan para mostrar
 * la tabla de la ventana Inicio (vienen de MUNICIPIO y ENCUESTA).
 */
public class Identificador {

    private String idHogar;
    private String idRegion;
    private String idDepartamento;
    private String idMunicipio;
    private String clase;
    private String sector;
    private String seccion;
    private String manzana;
    private String segmento;
    private String ciclo;
    private String numEdificacion;
    private String numVivienda;
    private String totalHogares;
    private String numHogar;
    private String totalPersonas;
    private String barrioCentroVereda;
    private String direccion;
    private String telefono;
    private String numEncuesta;

    // Solo para mostrar en la tabla de Inicio
    private String municipio;
    private String estado;
    private String validacion;

    public Identificador() {
    }

    public String getIdHogar() { return idHogar; }
    public void setIdHogar(String idHogar) { this.idHogar = idHogar; }

    public String getIdRegion() { return idRegion; }
    public void setIdRegion(String idRegion) { this.idRegion = idRegion; }

    public String getIdDepartamento() { return idDepartamento; }
    public void setIdDepartamento(String idDepartamento) { this.idDepartamento = idDepartamento; }

    public String getIdMunicipio() { return idMunicipio; }
    public void setIdMunicipio(String idMunicipio) { this.idMunicipio = idMunicipio; }

    public String getClase() { return clase; }
    public void setClase(String clase) { this.clase = clase; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getSeccion() { return seccion; }
    public void setSeccion(String seccion) { this.seccion = seccion; }

    public String getManzana() { return manzana; }
    public void setManzana(String manzana) { this.manzana = manzana; }

    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }

    public String getCiclo() { return ciclo; }
    public void setCiclo(String ciclo) { this.ciclo = ciclo; }

    public String getNumEdificacion() { return numEdificacion; }
    public void setNumEdificacion(String numEdificacion) { this.numEdificacion = numEdificacion; }

    public String getNumVivienda() { return numVivienda; }
    public void setNumVivienda(String numVivienda) { this.numVivienda = numVivienda; }

    public String getTotalHogares() { return totalHogares; }
    public void setTotalHogares(String totalHogares) { this.totalHogares = totalHogares; }

    public String getNumHogar() { return numHogar; }
    public void setNumHogar(String numHogar) { this.numHogar = numHogar; }

    public String getTotalPersonas() { return totalPersonas; }
    public void setTotalPersonas(String totalPersonas) { this.totalPersonas = totalPersonas; }

    public String getBarrioCentroVereda() { return barrioCentroVereda; }
    public void setBarrioCentroVereda(String barrioCentroVereda) { this.barrioCentroVereda = barrioCentroVereda; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getNumEncuesta() { return numEncuesta; }
    public void setNumEncuesta(String numEncuesta) { this.numEncuesta = numEncuesta; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getValidacion() { return validacion; }
    public void setValidacion(String validacion) { this.validacion = validacion; }
}
