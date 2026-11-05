package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ContratoWSEurekaBank;
import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import java.math.BigDecimal;
import java.util.List;

/**
 * Implementación de prueba del contrato: registra qué operación se llamó y con qué datos,
 * sin hacer peticiones SOAP.
 */
class ContratoFalso implements ContratoWSEurekaBank {

    String operacionLlamada;
    String cuentaRecibida;
    String cuentaDestinoRecibida;
    BigDecimal importeRecibido;
    String claveNuevaRecibida;

    @Override
    public String getEmpleado() {
        return "Monster EurekaBank";
    }

    @Override
    public boolean haySesionActiva() {
        return true;
    }

    @Override
    public RespuestaSesion iniciarSesion(String usuario, String clave) {
        operacionLlamada = "iniciarSesion:" + usuario;
        return new RespuestaSesion(200, "Bienvenido", "token", usuario, "Monster EurekaBank", 30);
    }

    @Override
    public String cambiarClave(String claveActual, String claveNueva) {
        operacionLlamada = "cambiarClave";
        claveNuevaRecibida = claveNueva;
        return "Contraseña actualizada correctamente";
    }

    @Override
    public String cerrarSesion() {
        operacionLlamada = "cerrarSesion";
        return "Sesión cerrada correctamente";
    }

    @Override
    public RespuestaCuentas listarCuentas() {
        operacionLlamada = "listarCuentas";
        return new RespuestaCuentas(200, "ok", List.of(
                new CuentaDTO("00100001", "Carlos Andrés Pérez Salazar", "1712345675", "USD", "$", "Matriz Quito",
                        "ACTIVO", BigDecimal.TEN, null, 1)));
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String cuenta) {
        operacionLlamada = "consultarMovimientos";
        cuentaRecibida = cuenta;
        return new RespuestaMovimientos(200, "ok", null, List.of());
    }

    @Override
    public RespuestaTransaccion registrarDeposito(String cuenta, BigDecimal importe) {
        return registrar("registrarDeposito", cuenta, null, importe);
    }

    @Override
    public RespuestaTransaccion registrarRetiro(String cuenta, BigDecimal importe) {
        return registrar("registrarRetiro", cuenta, null, importe);
    }

    @Override
    public RespuestaTransaccion registrarTransferencia(String cuentaOrigen, String cuentaDestino, BigDecimal importe) {
        return registrar("registrarTransferencia", cuentaOrigen, cuentaDestino, importe);
    }

    private RespuestaTransaccion registrar(String operacion, String cuenta, String destino, BigDecimal importe) {
        operacionLlamada = operacion;
        cuentaRecibida = cuenta;
        cuentaDestinoRecibida = destino;
        importeRecibido = importe;
        return new RespuestaTransaccion(201, "ok", BigDecimal.ONE, 1);
    }
}
