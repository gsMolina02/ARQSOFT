package ec.edu.monster.modelo;

import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.util.FormatoUtil;
import java.time.format.DateTimeFormatter;

/**
 * Fila de la tabla de movimientos con los textos ya formateados para la página.
 */
public class MovimientoFila {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final MovimientoDTO movimiento;
    private final String simbolo;

    public MovimientoFila(MovimientoDTO movimiento, String simbolo) {
        this.movimiento = movimiento;
        this.simbolo = simbolo;
    }

    public int getNumero() {
        return movimiento.numero();
    }

    public String getFecha() {
        return movimiento.fechaHora() == null ? "-" : movimiento.fechaHora().format(FECHA);
    }

    public String getHora() {
        return movimiento.fechaHora() == null ? "" : movimiento.fechaHora().format(HORA);
    }

    public String getTipo() {
        return movimiento.tipo();
    }

    /** "desde 00200001" o "hacia 00200001" en las transferencias; null en los demás movimientos. */
    public String getReferencia() {
        if (movimiento.cuentaReferencia() == null) {
            return null;
        }
        return (movimiento.esIngreso() ? "desde " : "hacia ") + movimiento.cuentaReferencia();
    }

    public String getImporte() {
        return FormatoUtil.formatearMovimiento(movimiento.importe(), movimiento.esIngreso(), simbolo);
    }

    public boolean isIngreso() {
        return movimiento.esIngreso();
    }

    /** Texto al pasar el mouse sobre la fila. */
    public String getDetalle() {
        return movimiento.accion() + " · registrado por el empleado " + movimiento.empleado();
    }
}
