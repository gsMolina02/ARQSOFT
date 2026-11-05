package ec.edu.monster.cliente.dto;

/**
 * Resultado de iniciarSesion: el token que se envía como parámetro en cada operación posterior.
 */
public record RespuestaSesion(int codigo, String mensaje, String token, String usuario, String empleado,
        int minutosVigencia) {
}
