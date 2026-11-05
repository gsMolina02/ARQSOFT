package ec.edu.monster.cliente;

/**
 * Error al consumir el servicio. Guarda el código de la respuesta SOAP para que la vista pueda
 * distinguir un problema de sesión (401) de un dato inválido (400) o una regla de negocio (409).
 * Código 0 indica que no hubo respuesta del servidor.
 */
public class ExcepcionClienteWS extends RuntimeException {

    public static final int SIN_CONEXION = 0;

    private final int codigoEstado;

    public ExcepcionClienteWS(int codigoEstado, String mensaje) {
        super(mensaje);
        this.codigoEstado = codigoEstado;
    }

    public int getCodigoEstado() {
        return codigoEstado;
    }

    public boolean esErrorAutenticacion() {
        return codigoEstado == 401;
    }

    public boolean esErrorConexion() {
        return codigoEstado == SIN_CONEXION;
    }
}
