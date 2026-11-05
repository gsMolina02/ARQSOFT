package ec.edu.monster.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validaciones que el cliente hace antes de llamar al servicio (las mismas reglas y mensajes del servidor),
 * para avisar al usuario sin esperar la respuesta de la red.
 */
public final class ValidacionUtil {

    public static final int LONGITUD_MINIMA_CLAVE = 6;
    public static final BigDecimal IMPORTE_MAXIMO = new BigDecimal("9999999999.99");

    public static final String MENSAJE_CUENTA = "El número de cuenta debe tener 8 dígitos";
    public static final String MENSAJE_IMPORTE = "El importe debe ser un número mayor a cero con hasta dos decimales";
    public static final String MENSAJE_IMPORTE_MAXIMO = "El importe no puede superar 9999999999.99";
    public static final String MENSAJE_CUENTAS_IGUALES = "La cuenta de origen y la de destino deben ser distintas";
    public static final String MENSAJE_CLAVE_NUEVA =
            "La nueva contraseña debe tener al menos 6 caracteres y ser distinta de la actual";
    public static final String MENSAJE_CONFIRMACION = "La confirmación no coincide con la nueva contraseña";
    public static final String MENSAJE_CLAVES_VACIAS = "Complete la contraseña actual, la nueva y su confirmación";

    private static final Pattern PATRON_CUENTA = Pattern.compile("^[0-9]{8}$");
    private static final Pattern PATRON_IMPORTE = Pattern.compile("^[0-9]{1,13}([.,][0-9]{1,2})?$");

    // Texto que puede estar a medio escribir (campos de Swing, JSP y Android)
    private static final Pattern PATRON_CUENTA_PARCIAL = Pattern.compile("^[0-9]{0,8}$");
    private static final Pattern PATRON_IMPORTE_PARCIAL = Pattern.compile("^[0-9]{0,13}([.,][0-9]{0,2})?$");

    private ValidacionUtil() {
    }

    public static String validarCuenta(String numero) {
        String texto = numero == null ? "" : numero.trim();
        if (!PATRON_CUENTA.matcher(texto).matches()) {
            throw new IllegalArgumentException(MENSAJE_CUENTA);
        }
        return texto;
    }

    /**
     * Acepta importes como 100, 25.5 o 25,50: solo dígitos, punto o coma decimal y hasta dos decimales.
     * Rechaza texto, signos, separadores de miles (1,000.50) y notación científica.
     */
    public static BigDecimal interpretarImporte(String texto) {
        String valor = texto == null ? "" : texto.trim();
        if (!PATRON_IMPORTE.matcher(valor).matches()) {
            throw new IllegalArgumentException(MENSAJE_IMPORTE);
        }
        return validarImporte(new BigDecimal(valor.replace(',', '.')));
    }

    public static BigDecimal validarImporte(BigDecimal importe) {
        if (importe == null || importe.signum() <= 0 || importe.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(MENSAJE_IMPORTE);
        }
        if (importe.compareTo(IMPORTE_MAXIMO) > 0) {
            throw new IllegalArgumentException(MENSAJE_IMPORTE_MAXIMO);
        }
        return importe;
    }

    public static boolean esCuentaParcial(String texto) {
        return PATRON_CUENTA_PARCIAL.matcher(texto == null ? "" : texto).matches();
    }

    public static boolean esImporteParcial(String texto) {
        return PATRON_IMPORTE_PARCIAL.matcher(texto == null ? "" : texto).matches();
    }

    public static void validarCuentasDistintas(String origen, String destino) {
        if (origen.equals(destino)) {
            throw new IllegalArgumentException(MENSAJE_CUENTAS_IGUALES);
        }
    }

    public static void validarCambioClave(String actual, String nueva, String confirmacion) {
        if (esVacio(actual) || esVacio(nueva) || esVacio(confirmacion)) {
            throw new IllegalArgumentException(MENSAJE_CLAVES_VACIAS);
        }
        if (!nueva.equals(confirmacion)) {
            throw new IllegalArgumentException(MENSAJE_CONFIRMACION);
        }
        if (nueva.length() < LONGITUD_MINIMA_CLAVE || nueva.equals(actual)) {
            throw new IllegalArgumentException(MENSAJE_CLAVE_NUEVA);
        }
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.isEmpty();
    }
}
