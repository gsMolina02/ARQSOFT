package ec.edu.monster.controlador;

import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.modelo.ExcepcionCampo;
import ec.edu.monster.modelo.OperacionesModelo;
import ec.edu.monster.modelo.TipoOperacion;
import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * CU-05 a CU-07 desde la página: valida el formulario de la pestaña elegida (indicando el campo con error)
 * y registra la operación. Las reglas de negocio (cuenta activa, saldo, moneda) las decide el servidor.
 */
public final class FormularioOperacion {

    private FormularioOperacion() {
    }

    /**
     * @throws ExcepcionCampo si un dato del formulario no tiene el formato correcto
     */
    public static RespuestaTransaccion registrar(CuentaControlador cuentaControlador, OperacionesModelo modelo) {
        String cuenta = validar("cuenta", () -> ValidacionUtil.validarCuenta(modelo.getCuenta()));

        if (modelo.getOperacion() == TipoOperacion.TRANSFERENCIA) {
            String destino = validar("destino", () -> ValidacionUtil.validarCuenta(modelo.getCuentaDestino()));
            validar("destino", () -> {
                ValidacionUtil.validarCuentasDistintas(cuenta, destino);
                return destino;
            });
            BigDecimal importe = validar("importe", () -> ValidacionUtil.interpretarImporte(modelo.getImporte()));
            return cuentaControlador.transferir(cuenta, destino, importe);
        }

        BigDecimal importe = validar("importe", () -> ValidacionUtil.interpretarImporte(modelo.getImporte()));
        return switch (modelo.getOperacion()) {
            case DEPOSITO -> cuentaControlador.depositar(cuenta, importe);
            case RETIRO -> cuentaControlador.retirar(cuenta, importe);
            default -> throw new IllegalArgumentException("La consulta de movimientos no registra ninguna operación");
        };
    }

    private static <T> T validar(String campo, Supplier<T> validacion) {
        try {
            return validacion.get();
        } catch (IllegalArgumentException ex) {
            throw new ExcepcionCampo(campo, ex.getMessage());
        }
    }
}
