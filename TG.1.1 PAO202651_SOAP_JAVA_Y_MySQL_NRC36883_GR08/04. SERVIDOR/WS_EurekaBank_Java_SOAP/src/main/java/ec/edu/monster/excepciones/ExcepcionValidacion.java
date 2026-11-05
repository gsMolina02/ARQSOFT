package ec.edu.monster.excepciones;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Datos con formato incorrecto (código 400).
 */
public class ExcepcionValidacion extends ExcepcionServicio {

    public ExcepcionValidacion(String mensaje) {
        super(HttpServletResponse.SC_BAD_REQUEST, mensaje);
    }
}
