package ec.edu.monster.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Textos que viajan en las respuestas SOAP: fechas en un formato fijo y montos con dos decimales.
 */
public final class FormatoUtil {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FormatoUtil() {
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? null : FECHA.format(fecha);
    }

    public static String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? null : FECHA_HORA.format(fechaHora);
    }

    /**
     * Valor con dos decimales (565.5 → 565.50), como se guarda en las columnas decimal(12,2).
     */
    public static BigDecimal dosDecimales(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Monto con el símbolo de la moneda: $565.50
     */
    public static String monto(String simbolo, BigDecimal valor) {
        return simbolo + dosDecimales(valor).toPlainString();
    }
}
