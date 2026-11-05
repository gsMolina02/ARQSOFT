package ec.edu.monster.ws;

import ec.edu.monster.datos.CuentaDAOJdbc;
import ec.edu.monster.datos.FabricaConexion;
import ec.edu.monster.datos.FabricaConexionJndi;
import ec.edu.monster.datos.MovimientoDAOJdbc;
import ec.edu.monster.datos.SesionDAOJdbc;
import ec.edu.monster.datos.UsuarioDAOJdbc;
import ec.edu.monster.modelos.SesionActiva;
import ec.edu.monster.modelos.dto.Respuesta;
import ec.edu.monster.modelos.dto.RespuestaCuentas;
import ec.edu.monster.modelos.dto.RespuestaMovimientos;
import ec.edu.monster.modelos.dto.RespuestaSesion;
import ec.edu.monster.modelos.dto.RespuestaTransaccion;
import ec.edu.monster.seguridad.ServicioSeguridad;
import ec.edu.monster.servicios.ServicioCuenta;
import ec.edu.monster.servicios.ServicioTransaccion;
import ec.edu.monster.util.DireccionesRed;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import java.time.Clock;

/**
 * Servicio SOAP de EurekaBank (Jakarta XML Web Services sobre Payara 7):
 * http://IP:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank?wsdl
 * <p>
 * El token viaja como parámetro de cada operación. Toda respuesta trae codigo y mensaje
 * (200/201 éxito, 400 validación, 401 sesión o credenciales, 404 cuenta inexistente,
 * 409 regla de negocio, 500 error interno). Esta clase solo valida el token y delega en los servicios.
 */
@WebService(serviceName = "WSEurekaBank", portName = "WSEurekaBankPort", targetNamespace = "http://ws.monster.edu.ec/")
public class WSEurekaBank {

    @Resource
    private WebServiceContext contexto;

    private final ServicioSeguridad seguridad;
    private final ServicioCuenta servicioCuenta;
    private final ServicioTransaccion servicioTransaccion;

    public WSEurekaBank() {
        this(new FabricaConexionJndi(InicioAplicacion.ORIGEN_DATOS), Clock.systemDefaultZone());
    }

    public WSEurekaBank(FabricaConexion fabrica, Clock reloj) {
        this(new ServicioSeguridad(fabrica, new UsuarioDAOJdbc(), new SesionDAOJdbc(), reloj),
                new ServicioCuenta(fabrica, new CuentaDAOJdbc(), new MovimientoDAOJdbc()),
                new ServicioTransaccion(fabrica, new CuentaDAOJdbc(), new MovimientoDAOJdbc(), reloj));
    }

    public WSEurekaBank(ServicioSeguridad seguridad, ServicioCuenta servicioCuenta,
            ServicioTransaccion servicioTransaccion) {
        this.seguridad = seguridad;
        this.servicioCuenta = servicioCuenta;
        this.servicioTransaccion = servicioTransaccion;
    }

    /**
     * CU-01: valida las credenciales contra la base de datos, registra la sesión y retorna el token.
     */
    @WebMethod(operationName = "iniciarSesion")
    public RespuestaSesion iniciarSesion(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "clave") String clave) {
        return ManejadorExcepciones.atender("iniciarSesion", RespuestaSesion::new,
                () -> seguridad.iniciarSesion(usuario, clave, ipCliente()));
    }

    /**
     * CU-02: verifica la contraseña actual y guarda el hash de la nueva.
     */
    @WebMethod(operationName = "cambiarClave")
    public Respuesta cambiarClave(
            @WebParam(name = "token") String token,
            @WebParam(name = "claveActual") String claveActual,
            @WebParam(name = "claveNueva") String claveNueva) {
        return ManejadorExcepciones.atender("cambiarClave", Respuesta::new, () -> {
            SesionActiva sesion = seguridad.validarToken(token);
            return seguridad.cambiarClave(sesion, claveActual, claveNueva);
        });
    }

    /**
     * CU-03: registra la fecha y hora de salida y cierra la sesión.
     */
    @WebMethod(operationName = "cerrarSesion")
    public Respuesta cerrarSesion(@WebParam(name = "token") String token) {
        return ManejadorExcepciones.atender("cerrarSesion", Respuesta::new, () -> {
            SesionActiva sesion = seguridad.validarToken(token);
            return seguridad.cerrarSesion(sesion);
        });
    }

    /**
     * Todas las cuentas con su estado, para que los clientes ofrezcan una lista desplegable del número de cuenta.
     */
    @WebMethod(operationName = "listarCuentas")
    public RespuestaCuentas listarCuentas(@WebParam(name = "token") String token) {
        return ManejadorExcepciones.atender("listarCuentas", RespuestaCuentas::new, () -> {
            seguridad.validarToken(token);
            return servicioCuenta.listarCuentas();
        });
    }

    /**
     * CU-04: datos y saldo de la cuenta con sus movimientos.
     */
    @WebMethod(operationName = "consultarMovimientos")
    public RespuestaMovimientos consultarMovimientos(
            @WebParam(name = "token") String token,
            @WebParam(name = "cuenta") String cuenta) {
        return ManejadorExcepciones.atender("consultarMovimientos", RespuestaMovimientos::new, () -> {
            seguridad.validarToken(token);
            return servicioCuenta.consultarMovimientos(cuenta);
        });
    }

    /**
     * CU-05: acredita el importe y registra el movimiento 003.
     */
    @WebMethod(operationName = "registrarDeposito")
    public RespuestaTransaccion registrarDeposito(
            @WebParam(name = "token") String token,
            @WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "importe") double importe) {
        return ManejadorExcepciones.atender("registrarDeposito", RespuestaTransaccion::new, () -> {
            SesionActiva sesion = seguridad.validarToken(token);
            return servicioTransaccion.registrarDeposito(cuenta, importe, sesion);
        });
    }

    /**
     * CU-06: debita el importe si hay saldo suficiente y registra el movimiento 004.
     */
    @WebMethod(operationName = "registrarRetiro")
    public RespuestaTransaccion registrarRetiro(
            @WebParam(name = "token") String token,
            @WebParam(name = "cuenta") String cuenta,
            @WebParam(name = "importe") double importe) {
        return ManejadorExcepciones.atender("registrarRetiro", RespuestaTransaccion::new, () -> {
            SesionActiva sesion = seguridad.validarToken(token);
            return servicioTransaccion.registrarRetiro(cuenta, importe, sesion);
        });
    }

    /**
     * CU-07: registra los movimientos 009 (origen) y 008 (destino) en una sola transacción.
     */
    @WebMethod(operationName = "registrarTransferencia")
    public RespuestaTransaccion registrarTransferencia(
            @WebParam(name = "token") String token,
            @WebParam(name = "cuentaOrigen") String cuentaOrigen,
            @WebParam(name = "cuentaDestino") String cuentaDestino,
            @WebParam(name = "importe") double importe) {
        return ManejadorExcepciones.atender("registrarTransferencia", RespuestaTransaccion::new, () -> {
            SesionActiva sesion = seguridad.validarToken(token);
            return servicioTransaccion.registrarTransferencia(cuentaOrigen, cuentaDestino, importe, sesion);
        });
    }

    // IP de la computadora o celular que inició la sesión; en las pruebas no hay contexto SOAP
    private String ipCliente() {
        try {
            if (contexto != null) {
                Object peticion = contexto.getMessageContext().get(MessageContext.SERVLET_REQUEST);
                if (peticion instanceof HttpServletRequest http) {
                    return DireccionesRed.ipCliente(http.getRemoteAddr());
                }
            }
        } catch (IllegalStateException ex) {
            // Fuera de una petición SOAP no hay MessageContext
        }
        return DireccionesRed.ipCliente(null);
    }
}
