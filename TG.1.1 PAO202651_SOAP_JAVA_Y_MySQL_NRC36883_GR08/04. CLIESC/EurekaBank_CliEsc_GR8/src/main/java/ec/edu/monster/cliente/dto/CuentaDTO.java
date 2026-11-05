package ec.edu.monster.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaDTO(String numero, String cliente, String cedula, String moneda, String simboloMoneda,
        String sucursal, String estado, BigDecimal saldo, LocalDate fechaApertura, int contadorMovimientos) {

    public boolean estaActiva() {
        return "ACTIVO".equals(estado);
    }
}
