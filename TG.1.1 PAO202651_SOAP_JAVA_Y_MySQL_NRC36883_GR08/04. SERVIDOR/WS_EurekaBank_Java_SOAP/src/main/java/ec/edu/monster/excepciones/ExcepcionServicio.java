package ec.edu.monster.excepciones;

/**
 * Error esperado del servicio: lleva el código con el que se responde al cliente dentro de la respuesta SOAP
 * (400 validación, 401 sesión o credenciales, 404 cuenta inexistente, 409 regla de negocio).
 */
public abstract class ExcepcionServicio extends RuntimeException {

    private final int codigo;

    protected ExcepcionServicio(int codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }
}
