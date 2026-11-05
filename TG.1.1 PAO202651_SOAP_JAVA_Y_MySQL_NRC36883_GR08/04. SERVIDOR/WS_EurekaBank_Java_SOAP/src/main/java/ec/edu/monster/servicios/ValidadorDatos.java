package ec.edu.monster.servicios;

import ec.edu.monster.excepciones.ExcepcionValidacion;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * CU-09: formato del número de cuenta y del importe (código 400 si no se cumple).
 */
public final class ValidadorDatos {

    /** Mayor valor que admite la columna decimal(12,2) de saldo e importe. */
    public static final BigDecimal IMPORTE_MAXIMO = new BigDecimal("9999999999.99");

    public static final String MENSAJE_CUENTA = "El número de cuenta debe tener 8 dígitos";
    public static final String MENSAJE_IMPORTE = "El importe debe ser un número mayor a cero con hasta dos decimales";
    public static final String MENSAJE_IMPORTE_MAXIMO = "El importe no puede superar 9999999999.99";

    private static final Pattern PATRON_CUENTA = Pattern.compile("^[0-9]{8}$");

    private ValidadorDatos() {
    }

    public static String validarCuenta(String numero) {
        String texto = numero == null ? "" : numero.trim();
        if (!PATRON_CUENTA.matcher(texto).matches()) {
            throw new ExcepcionValidacion(MENSAJE_CUENTA);
        }
        return texto;
    }

    /**
     * El importe llega como double (contrato del ERS) y se convierte con BigDecimal.valueOf, que usa la
     * representación decimal más corta del número: 10.1 es exactamente 10.1 y 10.555 tiene tres decimales.
     * Si el cliente no envía el importe, el servicio SOAP recibe 0 y se rechaza igual.
     */
    public static BigDecimal validarImporte(double importe) {
        if (!Double.isFinite(importe) || importe <= 0) {
            throw new ExcepcionValidacion(MENSAJE_IMPORTE);
        }
        BigDecimal valor = BigDecimal.valueOf(importe);
        if (valor.stripTrailingZeros().scale() > 2) {
            throw new ExcepcionValidacion(MENSAJE_IMPORTE);
        }
        if (valor.compareTo(IMPORTE_MAXIMO) > 0) {
            throw new ExcepcionValidacion(MENSAJE_IMPORTE_MAXIMO);
        }
        return valor.setScale(2);
    }
}
