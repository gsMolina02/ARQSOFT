package ec.edu.monster.modelo;

/**
 * Datos de la página "Configurar servidor".
 */
public class ServidorModelo {

    private final String direccion;
    private final String urlActual;
    private final boolean usaVariableEntorno;
    private String error;
    private String exito;
    private boolean ofrecerGuardarSinConexion;

    public ServidorModelo(String direccion, String urlActual, boolean usaVariableEntorno) {
        this.direccion = direccion;
        this.urlActual = urlActual;
        this.usaVariableEntorno = usaVariableEntorno;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getUrlActual() {
        return urlActual;
    }

    public boolean isUsaVariableEntorno() {
        return usaVariableEntorno;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getExito() {
        return exito;
    }

    public void setExito(String exito) {
        this.exito = exito;
    }

    /** La prueba falló: el botón "Guardar" pasa a ser "Guardar de todas formas". */
    public boolean isOfrecerGuardarSinConexion() {
        return ofrecerGuardarSinConexion;
    }

    public void setOfrecerGuardarSinConexion(boolean ofrecerGuardarSinConexion) {
        this.ofrecerGuardarSinConexion = ofrecerGuardarSinConexion;
    }
}
