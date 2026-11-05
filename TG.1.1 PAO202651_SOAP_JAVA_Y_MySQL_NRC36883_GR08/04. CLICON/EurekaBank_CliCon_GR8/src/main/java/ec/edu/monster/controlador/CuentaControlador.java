package ec.edu.monster.controlador;

import ec.edu.monster.cliente.ContratoWSEurekaBank;
import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;

/**
 * CU-04 Consultar movimientos, CU-05 Depósito, CU-06 Retiro y CU-07 Transferencia.
 * Valida el formato antes de llamar al servicio; las reglas de negocio (cuenta activa, saldo,
 * moneda) las decide el servidor.
 */
public class CuentaControlador {

    private final ContratoWSEurekaBank clienteWs;

    public CuentaControlador(ContratoWSEurekaBank clienteWs) {
        this.clienteWs = clienteWs;
    }

    /**
     * Cuentas que se ofrecen en la lista desplegable (todas, con su estado).
     */
    public RespuestaCuentas listarCuentas() {
        return clienteWs.listarCuentas();
    }

    public RespuestaMovimientos consultar(String cuenta) {
        return clienteWs.consultarMovimientos(ValidacionUtil.validarCuenta(cuenta));
    }

    public RespuestaTransaccion depositar(String cuenta, BigDecimal importe) {
        return clienteWs.registrarDeposito(ValidacionUtil.validarCuenta(cuenta), ValidacionUtil.validarImporte(importe));
    }

    public RespuestaTransaccion retirar(String cuenta, BigDecimal importe) {
        return clienteWs.registrarRetiro(ValidacionUtil.validarCuenta(cuenta), ValidacionUtil.validarImporte(importe));
    }

    public RespuestaTransaccion transferir(String cuentaOrigen, String cuentaDestino, BigDecimal importe) {
        String origen = ValidacionUtil.validarCuenta(cuentaOrigen);
        String destino = ValidacionUtil.validarCuenta(cuentaDestino);
        ValidacionUtil.validarCuentasDistintas(origen, destino);
        return clienteWs.registrarTransferencia(origen, destino, ValidacionUtil.validarImporte(importe));
    }
}
