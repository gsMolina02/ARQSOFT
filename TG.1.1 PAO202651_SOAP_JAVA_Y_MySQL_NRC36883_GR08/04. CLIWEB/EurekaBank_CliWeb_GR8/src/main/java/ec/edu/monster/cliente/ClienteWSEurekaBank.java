package ec.edu.monster.cliente;

import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.cliente.ws.WSEurekaBank;
import ec.edu.monster.cliente.ws.WSEurekaBank_Service;
import ec.edu.monster.util.ConfiguracionServidor;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Cliente del servicio SOAP WSEurekaBank. Usa los stubs que genera wsimport (paquete ec.edu.monster.cliente.ws)
 * apuntando a la dirección configurada, envía el token como parámetro y convierte cada respuesta con
 * codigo 400 o mayor en una ExcepcionClienteWS con el mensaje del servidor.
 */
public class ClienteWSEurekaBank implements ContratoWSEurekaBank {

    private static final int TIEMPO_CONEXION_MS = 5_000;
    private static final int TIEMPO_RESPUESTA_MS = 15_000;
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Leer el WSDL es costoso: el servicio se crea una sola vez y es seguro compartirlo entre hilos
    private static WSEurekaBank_Service servicio;

    private final String url;
    private final WSEurekaBank puerto;
    private final ManejadorTokenSOAP manejadorToken;

    // Variable de entorno WS_BASE_URL, dirección guardada por el usuario o localhost
    public ClienteWSEurekaBank() {
        this(new ConfiguracionServidor().getUrlActual());
    }

    public ClienteWSEurekaBank(String url) {
        this(url, new ManejadorTokenSOAP());
    }

    // El cliente web conserva el token en la sesión del navegador y crea un cliente por petición
    public ClienteWSEurekaBank(String url, ManejadorTokenSOAP manejadorToken) {
        this(url, crearPuerto(url.trim()), manejadorToken);
    }

    // El puerto se puede reemplazar en las pruebas para no depender del servidor
    public ClienteWSEurekaBank(String url, WSEurekaBank puerto, ManejadorTokenSOAP manejadorToken) {
        this.url = url.trim();
        this.puerto = puerto;
        this.manejadorToken = manejadorToken;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getEmpleado() {
        return manejadorToken.getEmpleado();
    }

    @Override
    public boolean haySesionActiva() {
        return manejadorToken.haySesionActiva();
    }

    @Override
    public RespuestaSesion iniciarSesion(String usuario, String clave) {
        ec.edu.monster.cliente.ws.RespuestaSesion respuesta = verificar(invocar(() -> puerto.iniciarSesion(usuario, clave)));
        manejadorToken.guardar(respuesta.getToken(), respuesta.getEmpleado());
        return new RespuestaSesion(respuesta.getCodigo(), respuesta.getMensaje(), respuesta.getToken(),
                respuesta.getUsuario(), respuesta.getEmpleado(), respuesta.getMinutosVigencia());
    }

    @Override
    public String cambiarClave(String claveActual, String claveNueva) {
        String token = manejadorToken.exigirToken();
        return verificar(invocar(() -> puerto.cambiarClave(token, claveActual, claveNueva))).getMensaje();
    }

    /**
     * CU-03: el token local se borra siempre, aunque el servidor no responda o la sesión ya hubiera vencido.
     */
    @Override
    public String cerrarSesion() {
        try {
            String token = manejadorToken.exigirToken();
            return verificar(invocar(() -> puerto.cerrarSesion(token))).getMensaje();
        } finally {
            manejadorToken.limpiar();
        }
    }

    @Override
    public RespuestaCuentas listarCuentas() {
        String token = manejadorToken.exigirToken();
        ec.edu.monster.cliente.ws.RespuestaCuentas respuesta = verificar(invocar(() -> puerto.listarCuentas(token)));

        List<CuentaDTO> cuentas = new ArrayList<>();
        if (respuesta.getCuentas() != null) {
            for (ec.edu.monster.cliente.ws.Cuenta cuenta : respuesta.getCuentas().getCuenta()) {
                cuentas.add(convertirCuenta(cuenta));
            }
        }
        return new RespuestaCuentas(respuesta.getCodigo(), respuesta.getMensaje(), cuentas);
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String cuenta) {
        String token = manejadorToken.exigirToken();
        ec.edu.monster.cliente.ws.RespuestaMovimientos respuesta =
                verificar(invocar(() -> puerto.consultarMovimientos(token, cuenta)));

        List<MovimientoDTO> movimientos = new ArrayList<>();
        if (respuesta.getMovimientos() != null) {
            for (ec.edu.monster.cliente.ws.Movimiento movimiento : respuesta.getMovimientos().getMovimiento()) {
                movimientos.add(new MovimientoDTO(movimiento.getNumero(), leerFechaHora(movimiento.getFechaHora()),
                        movimiento.getCodigoTipo(), movimiento.getTipo(), movimiento.getAccion(),
                        movimiento.getImporte(), movimiento.getCuentaReferencia(), movimiento.getEmpleado()));
            }
        }
        return new RespuestaMovimientos(respuesta.getCodigo(), respuesta.getMensaje(),
                convertirCuenta(respuesta.getCuenta()), movimientos);
    }

    @Override
    public RespuestaTransaccion registrarDeposito(String cuenta, BigDecimal importe) {
        String token = manejadorToken.exigirToken();
        return convertir(verificar(invocar(() -> puerto.registrarDeposito(token, cuenta, importe.doubleValue()))));
    }

    @Override
    public RespuestaTransaccion registrarRetiro(String cuenta, BigDecimal importe) {
        String token = manejadorToken.exigirToken();
        return convertir(verificar(invocar(() -> puerto.registrarRetiro(token, cuenta, importe.doubleValue()))));
    }

    @Override
    public RespuestaTransaccion registrarTransferencia(String cuentaOrigen, String cuentaDestino, BigDecimal importe) {
        String token = manejadorToken.exigirToken();
        return convertir(verificar(invocar(() ->
                puerto.registrarTransferencia(token, cuentaOrigen, cuentaDestino, importe.doubleValue()))));
    }

    private static synchronized WSEurekaBank crearPuerto(String url) {
        if (servicio == null) {
            // Contrato incluido en el JAR (src/main/resources/wsdl): no se descarga del servidor al conectar
            servicio = new WSEurekaBank_Service(ClienteWSEurekaBank.class.getResource("/wsdl/WSEurekaBank.wsdl"));
        }
        WSEurekaBank puerto = servicio.getWSEurekaBankPort();
        Map<String, Object> contexto = ((BindingProvider) puerto).getRequestContext();
        contexto.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        contexto.put("com.sun.xml.ws.connect.timeout", TIEMPO_CONEXION_MS);
        contexto.put("com.sun.xml.ws.request.timeout", TIEMPO_RESPUESTA_MS);
        return puerto;
    }

    private <T> T invocar(Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (WebServiceException ex) {
            throw new ExcepcionClienteWS(ExcepcionClienteWS.SIN_CONEXION,
                    "No fue posible conectar con el servidor SOAP en " + url + ". Verifique que Payara esté "
                    + "encendido con el servicio desplegado, la IP configurada y que ambas computadoras estén en la misma red.");
        }
    }

    // Toda respuesta trae codigo y mensaje: 400 o más es un error que se muestra tal cual al usuario
    private <T extends ec.edu.monster.cliente.ws.Respuesta> T verificar(T respuesta) {
        if (respuesta == null) {
            throw new ExcepcionClienteWS(500, "El servidor devolvió una respuesta vacía.");
        }
        if (respuesta.getCodigo() >= 400) {
            if (respuesta.getCodigo() == 401) {
                // Token inválido o sesión vencida (CU-10): el cliente vuelve al inicio de sesión
                manejadorToken.limpiar();
            }
            String mensaje = respuesta.getMensaje() == null || respuesta.getMensaje().isBlank()
                    ? "El servidor respondió con el código " + respuesta.getCodigo() + "."
                    : respuesta.getMensaje();
            throw new ExcepcionClienteWS(respuesta.getCodigo(), mensaje);
        }
        return respuesta;
    }

    private static RespuestaTransaccion convertir(ec.edu.monster.cliente.ws.RespuestaTransaccion respuesta) {
        return new RespuestaTransaccion(respuesta.getCodigo(), respuesta.getMensaje(), respuesta.getSaldo(),
                respuesta.getNumeroMovimiento());
    }

    private static CuentaDTO convertirCuenta(ec.edu.monster.cliente.ws.Cuenta cuenta) {
        if (cuenta == null) {
            throw new ExcepcionClienteWS(500, "El servidor no devolvió los datos de la cuenta.");
        }
        return new CuentaDTO(cuenta.getNumero(), cuenta.getCliente(), cuenta.getCedula(), cuenta.getMoneda(),
                cuenta.getSimboloMoneda() == null ? "$" : cuenta.getSimboloMoneda(), cuenta.getSucursal(),
                cuenta.getEstado(), cuenta.getSaldo(), leerFecha(cuenta.getFechaApertura()),
                cuenta.getContadorMovimientos());
    }

    // El servidor envía las fechas como texto: yyyy-MM-dd y yyyy-MM-dd HH:mm:ss
    private static LocalDate leerFecha(String texto) {
        try {
            return texto == null ? null : LocalDate.parse(texto.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private static LocalDateTime leerFechaHora(String texto) {
        try {
            return texto == null ? null : LocalDateTime.parse(texto.trim(), FORMATO_FECHA_HORA);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
