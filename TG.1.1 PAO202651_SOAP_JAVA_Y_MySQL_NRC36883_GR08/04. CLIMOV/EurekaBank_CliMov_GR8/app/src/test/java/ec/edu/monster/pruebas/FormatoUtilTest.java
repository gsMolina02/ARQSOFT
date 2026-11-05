package ec.edu.monster.pruebas;

import ec.edu.monster.util.FormatoUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FormatoUtilTest {

    @Test
    void deberiaFormatearMontosConSeparadorDeMilesYDosDecimales() {
        assertEquals("$1,250.50", FormatoUtil.formatearMonto(new BigDecimal("1250.5")));
        assertEquals("$0.00", FormatoUtil.formatearMonto(BigDecimal.ZERO));
        assertEquals("-$5.00", FormatoUtil.formatearMonto(new BigDecimal("-5"), "$"));
    }

    @Test
    void deberiaMostrarElSignoSegunLaAccionDelMovimiento() {
        assertEquals("+$100.50", FormatoUtil.formatearMovimiento(new BigDecimal("100.50"), true, "$"));
        assertEquals("-$65.50", FormatoUtil.formatearMovimiento(new BigDecimal("65.5"), false, "$"));
    }

    @Test
    void deberiaFormatearFechas() {
        assertEquals("04/11/2026 09:15:07", FormatoUtil.formatearFechaHora(LocalDateTime.of(2026, 11, 4, 9, 15, 7)));
        assertEquals("15/01/2026", FormatoUtil.formatearFecha(LocalDate.of(2026, 1, 15)));
        assertEquals("-", FormatoUtil.formatearFecha(null));
    }
}
