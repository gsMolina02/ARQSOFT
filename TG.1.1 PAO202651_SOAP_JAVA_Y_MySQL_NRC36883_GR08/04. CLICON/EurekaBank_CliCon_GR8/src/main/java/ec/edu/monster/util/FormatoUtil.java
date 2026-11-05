package ec.edu.monster.util;

import ec.edu.monster.cliente.dto.CuentaDTO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class FormatoUtil {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private FormatoUtil() {
    }

    /**
     * Por ejemplo $1,250.50 (siempre con dos decimales).
     */
    public static String formatearMonto(BigDecimal valor, String simbolo) {
        BigDecimal monto = valor == null ? BigDecimal.ZERO : valor;
        return (monto.signum() < 0 ? "-" : "") + simbolo + numero(monto.abs());
    }

    public static String formatearMonto(BigDecimal valor) {
        return formatearMonto(valor, "$");
    }

    /**
     * Monto con signo según la acción del movimiento: +$100.00 o -$50.00.
     */
    public static String formatearMovimiento(BigDecimal importe, boolean esIngreso, String simbolo) {
        return (esIngreso ? "+" : "-") + simbolo + numero(importe == null ? BigDecimal.ZERO : importe.abs());
    }

    public static String formatearFechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "-" : fechaHora.format(FORMATO_FECHA_HORA);
    }

    public static String formatearFecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO_FECHA);
    }

    /**
     * Texto de la cuenta en la lista desplegable: 00100001 · Carlos Pérez (y el estado si no está activa).
     */
    public static String formatearOpcionCuenta(CuentaDTO cuenta) {
        return cuenta.numero() + " · " + cuenta.cliente() + (cuenta.estaActiva() ? "" : " (" + cuenta.estado() + ")");
    }

    // DecimalFormat no es seguro entre hilos (el cliente web atiende varias peticiones a la vez)
    private static String numero(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return formato.format(valor.setScale(2, RoundingMode.HALF_UP));
    }
}
