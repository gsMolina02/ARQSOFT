package ec.edu.monster.excepciones;

import jakarta.servlet.http.HttpServletResponse;

/**
 * La cuenta solicitada no existe (código 404).
 */
public class ExcepcionNoEncontrado extends ExcepcionServicio {

    public ExcepcionNoEncontrado(String mensaje) {
        super(HttpServletResponse.SC_NOT_FOUND, mensaje);
    }
}
