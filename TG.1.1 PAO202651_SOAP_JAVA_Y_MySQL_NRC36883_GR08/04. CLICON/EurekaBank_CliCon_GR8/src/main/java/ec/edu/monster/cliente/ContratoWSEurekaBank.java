package ec.edu.monster.cliente;

import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import java.math.BigDecimal;

/**
 * Operaciones que expone el servicio SOAP WSEurekaBank.
 * Los controladores dependen de esta interfaz y no de los stubs generados por wsimport.
 */
public interface ContratoWSEurekaBank {

    /** Nombre del empleado de la sesión actual (null sin sesión). */
    String getEmpleado();

    boolean haySesionActiva();

    // iniciarSesion(usuario, clave)
    RespuestaSesion iniciarSesion(String usuario, String clave);

    // cambiarClave(token, claveActual, claveNueva)
    String cambiarClave(String claveActual, String claveNueva);

    // cerrarSesion(token)
    String cerrarSesion();

    // listarCuentas(token)
    RespuestaCuentas listarCuentas();

    // consultarMovimientos(token, cuenta)
    RespuestaMovimientos consultarMovimientos(String cuenta);

    // registrarDeposito(token, cuenta, importe)
    RespuestaTransaccion registrarDeposito(String cuenta, BigDecimal importe);

    // registrarRetiro(token, cuenta, importe)
    RespuestaTransaccion registrarRetiro(String cuenta, BigDecimal importe);

    // registrarTransferencia(token, cuentaOrigen, cuentaDestino, importe)
    RespuestaTransaccion registrarTransferencia(String cuentaOrigen, String cuentaDestino, BigDecimal importe);
}
