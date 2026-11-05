package ec.edu.monster.cliente.dto;

import java.util.List;

/**
 * Resultado de consultarMovimientos: datos de la cuenta y sus movimientos ordenados por número.
 */
public record RespuestaMovimientos(int codigo, String mensaje, CuentaDTO cuenta, List<MovimientoDTO> movimientos) {
}
