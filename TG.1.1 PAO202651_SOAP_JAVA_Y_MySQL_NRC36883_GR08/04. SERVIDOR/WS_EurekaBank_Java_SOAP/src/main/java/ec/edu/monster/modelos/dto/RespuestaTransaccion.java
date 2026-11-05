package ec.edu.monster.modelos.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Resultado de un depósito, retiro o transferencia (CU-05, CU-06 y CU-07): el saldo que queda en la cuenta
 * (la de origen en una transferencia) y el número del movimiento registrado en ella.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaTransaccion", propOrder = {"saldo", "numeroMovimiento"})
public class RespuestaTransaccion extends Respuesta {

    private BigDecimal saldo;
    private int numeroMovimiento;

    public RespuestaTransaccion() {
    }

    public RespuestaTransaccion(int codigo, String mensaje, BigDecimal saldo, int numeroMovimiento) {
        super(codigo, mensaje);
        this.saldo = saldo;
        this.numeroMovimiento = numeroMovimiento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public int getNumeroMovimiento() {
        return numeroMovimiento;
    }
}
