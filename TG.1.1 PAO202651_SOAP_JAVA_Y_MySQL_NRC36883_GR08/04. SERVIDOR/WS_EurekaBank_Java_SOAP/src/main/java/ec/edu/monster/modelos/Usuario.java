package ec.edu.monster.modelos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Credenciales de acceso de un empleado. La contraseña se guarda como hash SHA-256 en hexadecimal.
 */
public class Usuario {

    public static final String ESTADO_ACTIVO = "ACTIVO";

    private final String nombreUsuario;
    private final String codigoEmpleado;
    private final String nombreEmpleado;
    private final String hashClave;
    private final String estado;

    public Usuario(String nombreUsuario, String codigoEmpleado, String nombreEmpleado, String hashClave, String estado) {
        this.nombreUsuario = nombreUsuario;
        this.codigoEmpleado = codigoEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.hashClave = hashClave;
        this.estado = estado;
    }

    public boolean estaActivo() {
        return ESTADO_ACTIVO.equals(estado);
    }

    public boolean verificarClave(String clave) {
        byte[] calculado = calcularHash(clave).getBytes(StandardCharsets.US_ASCII);
        byte[] guardado = hashClave.trim().toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII);
        // Comparación en tiempo constante: no revela cuántos caracteres coinciden
        return MessageDigest.isEqual(calculado, guardado);
    }

    public static String calcularHash(String clave) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(sha256.digest(clave.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            // Toda JVM trae SHA-256: no ocurre en la práctica
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
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

    public String getHashClave() {
        return hashClave;
    }

    public String getEstado() {
        return estado;
    }
}
