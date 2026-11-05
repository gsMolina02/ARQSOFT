package ec.edu.monster.cliente.dto;

import java.math.BigDecimal;

/**
 * Depósito, retiro o transferencia: saldo nuevo y número del movimiento registrado.
 */
public record RespuestaTransaccion(int codigo, String mensaje, BigDecimal saldo, int numeroMovimiento) {
}
