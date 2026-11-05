package ec.edu.monster.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Un movimiento de la cuenta. cuentaReferencia es la otra cuenta de una transferencia (null en los demás).
 */
public record MovimientoDTO(int numero, LocalDateTime fechaHora, String codigoTipo, String tipo, String accion,
        BigDecimal importe, String cuentaReferencia, String empleado) {

    public boolean esIngreso() {
        return "INGRESO".equals(accion);
    }
}
