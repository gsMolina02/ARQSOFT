package ec.edu.monster.modelos;

import java.time.LocalDateTime;

/**
 * Sesión abierta y vigente que acompaña a cada operación autenticada: identifica al empleado
 * que se registra en los movimientos.
 */
public class SesionActiva {

    private final String token;
    private final String nombreUsuario;
    private final String codigoEmpleado;
    private final String nombreEmpleado;
    private final LocalDateTime ultimaActividad;

    public SesionActiva(String token, String nombreUsuario, String codigoEmpleado, String nombreEmpleado,
            LocalDateTime ultimaActividad) {
        this.token = token;
        this.nombreUsuario = nombreUsuario;
        this.codigoEmpleado = codigoEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.ultimaActividad = ultimaActividad;
    }

    public String getToken() {
        return token;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }
}
