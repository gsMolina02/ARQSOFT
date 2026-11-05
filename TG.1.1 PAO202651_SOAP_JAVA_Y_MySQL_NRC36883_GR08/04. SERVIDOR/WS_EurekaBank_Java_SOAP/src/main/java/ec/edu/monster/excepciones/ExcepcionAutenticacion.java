package ec.edu.monster.excepciones;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Credenciales incorrectas o token ausente, inválido o vencido (código 401).
 */
public class ExcepcionAutenticacion extends ExcepcionServicio {

    public ExcepcionAutenticacion(String mensaje) {
        super(HttpServletResponse.SC_UNAUTHORIZED, mensaje);
    }
}
