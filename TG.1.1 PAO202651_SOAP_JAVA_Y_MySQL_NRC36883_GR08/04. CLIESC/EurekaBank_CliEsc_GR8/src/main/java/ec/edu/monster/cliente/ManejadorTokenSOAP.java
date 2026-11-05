package ec.edu.monster.cliente;

import java.io.Serializable;

/**
 * Guarda el token de la sesión y el nombre del empleado. En SOAP el token no viaja en una cabecera:
 * se envía como primer parámetro de cada operación protegida.
 * Es Serializable para que el cliente web lo guarde en la sesión del navegador (HttpSession).
 */
public class ManejadorTokenSOAP implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String MENSAJE_SIN_SESION = "No hay una sesión activa. Inicie sesión primero.";

    private String token;
    private String empleado;

    public void guardar(String token, String empleado) {
        this.token = token;
        this.empleado = empleado;
    }

    public String obtenerToken() {
        return token;
    }

    public String getEmpleado() {
        return empleado;
    }

    public boolean haySesionActiva() {
        return token != null && !token.isBlank();
    }

    public void limpiar() {
        token = null;
        empleado = null;
    }

    /**
     * Token para una operación protegida; sin sesión se responde como el servidor (401) sin llamarlo.
     */
    public String exigirToken() {
        if (!haySesionActiva()) {
            throw new ExcepcionClienteWS(401, MENSAJE_SIN_SESION);
        }
        return token;
    }
}
