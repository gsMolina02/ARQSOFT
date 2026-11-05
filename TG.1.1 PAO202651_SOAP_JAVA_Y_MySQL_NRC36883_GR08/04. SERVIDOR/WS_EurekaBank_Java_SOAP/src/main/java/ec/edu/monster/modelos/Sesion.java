package ec.edu.monster.modelos;

import java.time.LocalDateTime;

/**
 * Registro de una sesión iniciada por un usuario (tabla sesion).
 */
public class Sesion {

    private final String token;
    private final String nombreUsuario;
    private final LocalDateTime fechaIngreso;
    private final LocalDateTime ultimaActividad;
    private final LocalDateTime fechaSalida;
    private final String ip;

    public Sesion(String token, String nombreUsuario, LocalDateTime fechaIngreso, LocalDateTime ultimaActividad,
            LocalDateTime fechaSalida, String ip) {
        this.token = token;
        this.nombreUsuario = nombreUsuario;
        this.fechaIngreso = fechaIngreso;
        this.ultimaActividad = ultimaActividad;
        this.fechaSalida = fechaSalida;
        this.ip = ip;
    }

    public String getToken() {
        return token;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public String getIp() {
        return ip;
    }
}
