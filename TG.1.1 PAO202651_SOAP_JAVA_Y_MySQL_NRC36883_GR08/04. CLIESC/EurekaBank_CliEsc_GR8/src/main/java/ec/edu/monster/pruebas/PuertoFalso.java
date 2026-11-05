package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ws.Cuenta;
import ec.edu.monster.cliente.ws.Movimiento;
import ec.edu.monster.cliente.ws.Respuesta;
import ec.edu.monster.cliente.ws.RespuestaCuentas;
import ec.edu.monster.cliente.ws.RespuestaMovimientos;
import ec.edu.monster.cliente.ws.RespuestaSesion;
import ec.edu.monster.cliente.ws.RespuestaTransaccion;
import ec.edu.monster.cliente.ws.WSEurekaBank;
import jakarta.xml.ws.WebServiceException;
import java.math.BigDecimal;

/**
 * Reemplaza el stub SOAP: devuelve respuestas armadas en la prueba y registra los parámetros recibidos,
 * sin hacer peticiones a Payara.
 */
class PuertoFalso implements WSEurekaBank {

    int codigo = 200;
    String mensaje = "ok";
    boolean sinConexion;

    String operacion;
    String tokenRecibido;
    String cuentaRecibida;
    double importeRecibido;

    @Override
    public RespuestaSesion iniciarSesion(String usuario, String clave) {
        registrar("iniciarSesion", null, null, 0);
        RespuestaSesion respuesta = completar(new RespuestaSesion());
        respuesta.setToken("token-de-prueba");
        respuesta.setUsuario(usuario);
        respuesta.setEmpleado("Monster EurekaBank");
        respuesta.setMinutosVigencia(30);
        return respuesta;
    }

    @Override
    public Respuesta cambiarClave(String token, String claveActual, String claveNueva) {
        registrar("cambiarClave", token, null, 0);
        return completar(new Respuesta());
    }

    @Override
    public Respuesta cerrarSesion(String token) {
        registrar("cerrarSesion", token, null, 0);
        return completar(new Respuesta());
    }

    @Override
    public RespuestaCuentas listarCuentas(String token) {
        registrar("listarCuentas", token, null, 0);
        RespuestaCuentas respuesta = completar(new RespuestaCuentas());
        if (codigo >= 400) {
            return respuesta;
        }
        RespuestaCuentas.Cuentas lista = new RespuestaCuentas.Cuentas();
        lista.getCuenta().add(cuenta("00100001", "Carlos Andrés Pérez Salazar", "ACTIVO"));
        lista.getCuenta().add(cuenta("00100003", "Diego Sebastián Naranjo Toapanta", "ANULADO"));
        respuesta.setCuentas(lista);
        return respuesta;
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String token, String cuenta) {
        registrar("consultarMovimientos", token, cuenta, 0);
        RespuestaMovimientos respuesta = completar(new RespuestaMovimientos());
        if (codigo >= 400) {
            return respuesta;
        }
        Cuenta datos = new Cuenta();
        datos.setNumero(cuenta);
        datos.setCliente("Carlos Andrés Pérez Salazar");
        datos.setCedula("1712345675");
        datos.setMoneda("USD");
        datos.setSimboloMoneda("$");
        datos.setSucursal("Matriz Quito");
        datos.setEstado("ACTIVO");
        datos.setSaldo(new BigDecimal("565.50"));
        datos.setFechaApertura("2026-01-15");
        datos.setContadorMovimientos(2);
        respuesta.setCuenta(datos);

        RespuestaMovimientos.Movimientos lista = new RespuestaMovimientos.Movimientos();
        lista.getMovimiento().add(movimiento(1, "001", "Apertura de cuenta", "INGRESO", "631.00", null));
        lista.getMovimiento().add(movimiento(2, "009", "Transferencia enviada", "SALIDA", "65.50", "00200001"));
        respuesta.setMovimientos(lista);
        return respuesta;
    }

    @Override
    public RespuestaTransaccion registrarDeposito(String token, String cuenta, double importe) {
        registrar("registrarDeposito", token, cuenta, importe);
        return transaccion();
    }

    @Override
    public RespuestaTransaccion registrarRetiro(String token, String cuenta, double importe) {
        registrar("registrarRetiro", token, cuenta, importe);
        return transaccion();
    }

    @Override
    public RespuestaTransaccion registrarTransferencia(String token, String cuentaOrigen, String cuentaDestino,
            double importe) {
        registrar("registrarTransferencia", token, cuentaOrigen + "->" + cuentaDestino, importe);
        return transaccion();
    }

    private RespuestaTransaccion transaccion() {
        RespuestaTransaccion respuesta = completar(new RespuestaTransaccion());
        respuesta.setSaldo(new BigDecimal("591.00"));
        respuesta.setNumeroMovimiento(8);
        return respuesta;
    }

    private void registrar(String nombre, String token, String cuenta, double importe) {
        if (sinConexion) {
            throw new WebServiceException("java.net.ConnectException: Connection refused");
        }
        operacion = nombre;
        tokenRecibido = token;
        cuentaRecibida = cuenta;
        importeRecibido = importe;
    }

    private <T extends Respuesta> T completar(T respuesta) {
        respuesta.setCodigo(codigo);
        respuesta.setMensaje(mensaje);
        return respuesta;
    }

    private static Cuenta cuenta(String numero, String cliente, String estado) {
        Cuenta datos = new Cuenta();
        datos.setNumero(numero);
        datos.setCliente(cliente);
        datos.setEstado(estado);
        datos.setSaldo(BigDecimal.ZERO);
        datos.setFechaApertura("2026-01-15");
        return datos;
    }

    private static Movimiento movimiento(int numero, String codigo, String tipo, String accion, String importe,
            String referencia) {
        Movimiento movimiento = new Movimiento();
        movimiento.setNumero(numero);
        movimiento.setFechaHora("2026-11-04 09:15:07");
        movimiento.setCodigoTipo(codigo);
        movimiento.setTipo(tipo);
        movimiento.setAccion(accion);
        movimiento.setImporte(new BigDecimal(importe));
        movimiento.setCuentaReferencia(referencia);
        movimiento.setEmpleado("0001");
        return movimiento;
    }
}
