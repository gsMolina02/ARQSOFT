package ec.edu.monster.cliente.dto;

import java.util.List;

/**
 * Resultado de listarCuentas: todas las cuentas, para elegir el número en una lista desplegable.
 */
public record RespuestaCuentas(int codigo, String mensaje, List<CuentaDTO> cuentas) {
}
