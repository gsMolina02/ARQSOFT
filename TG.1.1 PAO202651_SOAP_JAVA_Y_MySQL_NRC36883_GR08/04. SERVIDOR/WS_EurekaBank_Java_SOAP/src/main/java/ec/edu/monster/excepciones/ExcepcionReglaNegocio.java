package ec.edu.monster.excepciones;

import jakarta.servlet.http.HttpServletResponse;

/**
 * La operación incumple una regla de negocio: cuenta no activa, saldo insuficiente... (código 409).
 */
public class ExcepcionReglaNegocio extends ExcepcionServicio {

    public ExcepcionReglaNegocio(String mensaje) {
        super(HttpServletResponse.SC_CONFLICT, mensaje);
    }
}
