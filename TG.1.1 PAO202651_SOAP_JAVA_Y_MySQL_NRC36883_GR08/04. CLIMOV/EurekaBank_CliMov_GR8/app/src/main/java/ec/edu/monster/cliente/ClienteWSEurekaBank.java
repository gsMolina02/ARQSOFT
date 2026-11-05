package ec.edu.monster.cliente;

import ec.edu.monster.cliente.dto.CuentaDTO;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Cliente del servicio SOAP WSEurekaBank con ksoap2: arma cada operación del contrato, envía el token como
 * parámetro y convierte cada respuesta con codigo 400 o mayor en una ExcepcionClienteWS con el mensaje del servidor.
 * Hace llamadas de red: la vista lo usa fuera del hilo principal.
 */
public class ClienteWSEurekaBank implements ContratoWSEurekaBank {

    public static final String ESPACIO_NOMBRES = "http://ws.monster.edu.ec/";

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String url;
    private final Transporte transporte;
    private final ManejadorTokenSOAP manejadorToken;

    public ClienteWSEurekaBank(String url) {
        this(url, new TransporteKsoap(url.trim()), new ManejadorTokenSOAP());
    }

    // El transporte se puede reemplazar en las pruebas para no depender del servidor
    public ClienteWSEurekaBank(String url, Transporte transporte, ManejadorTokenSOAP manejadorToken) {
        this.url = url.trim();
        this.transporte = transporte;
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
        SoapObject peticion = peticion("iniciarSesion");
        peticion.addProperty("usuario", usuario);
        peticion.addProperty("clave", clave);
        SoapObject respuesta = invocar(peticion);

        String token = texto(respuesta, "token");
        String empleado = texto(respuesta, "empleado");
        manejadorToken.guardar(token, empleado);
        return new RespuestaSesion(codigo(respuesta), texto(respuesta, "mensaje"), token, texto(respuesta, "usuario"),
                empleado, entero(respuesta, "minutosVigencia"));
    }

    @Override
    public String cambiarClave(String claveActual, String claveNueva) {
        SoapObject peticion = peticionProtegida("cambiarClave");
        peticion.addProperty("claveActual", claveActual);
        peticion.addProperty("claveNueva", claveNueva);
        return texto(invocar(peticion), "mensaje");
    }

    /**
     * CU-03: el token local se borra siempre, aunque el servidor no responda o la sesión ya hubiera vencido.
     */
    @Override
    public String cerrarSesion() {
        try {
            return texto(invocar(peticionProtegida("cerrarSesion")), "mensaje");
        } finally {
            manejadorToken.limpiar();
        }
    }

    @Override
    public RespuestaCuentas listarCuentas() {
        SoapObject respuesta = invocar(peticionProtegida("listarCuentas"));

        // Las cuentas llegan dentro de <cuentas>, una por cada <cuenta>
        List<CuentaDTO> cuentas = new ArrayList<>();
        if (respuesta.hasProperty("cuentas") && respuesta.getProperty("cuentas") instanceof SoapObject lista) {
            for (int i = 0; i < lista.getPropertyCount(); i++) {
                if (lista.getProperty(i) instanceof SoapObject cuenta) {
                    cuentas.add(leerCuenta(cuenta));
                }
            }
        }
        return new RespuestaCuentas(codigo(respuesta), texto(respuesta, "mensaje"), cuentas);
    }

    @Override
    public RespuestaMovimientos consultarMovimientos(String cuenta) {
        SoapObject peticion = peticionProtegida("consultarMovimientos");
        peticion.addProperty("cuenta", cuenta);
        SoapObject respuesta = invocar(peticion);

        if (!(respuesta.hasProperty("cuenta") && respuesta.getProperty("cuenta") instanceof SoapObject datos)) {
            throw new ExcepcionClienteWS(500, "El servidor no devolvió los datos de la cuenta.");
        }
        CuentaDTO datosCuenta = leerCuenta(datos);

        // Los movimientos llegan dentro de <movimientos>, uno por cada <movimiento>
        List<MovimientoDTO> movimientos = new ArrayList<>();
        if (respuesta.hasProperty("movimientos") && respuesta.getProperty("movimientos") instanceof SoapObject lista) {
            for (int i = 0; i < lista.getPropertyCount(); i++) {
                if (lista.getProperty(i) instanceof SoapObject movimiento) {
                    movimientos.add(new MovimientoDTO(entero(movimiento, "numero"),
                            leerFechaHora(texto(movimiento, "fechaHora")), texto(movimiento, "codigoTipo"),
                            texto(movimiento, "tipo"), texto(movimiento, "accion"), decimal(movimiento, "importe"),
                            texto(movimiento, "cuentaReferencia"), texto(movimiento, "empleado")));
                }
            }
        }
        return new RespuestaMovimientos(codigo(respuesta), texto(respuesta, "mensaje"), datosCuenta, movimientos);
    }

    private static CuentaDTO leerCuenta(SoapObject datos) {
        return new CuentaDTO(texto(datos, "numero"), texto(datos, "cliente"), texto(datos, "cedula"),
                texto(datos, "moneda"), texto(datos, "simboloMoneda") == null ? "$" : texto(datos, "simboloMoneda"),
                texto(datos, "sucursal"), texto(datos, "estado"), decimal(datos, "saldo"),
                leerFecha(texto(datos, "fechaApertura")), entero(datos, "contadorMovimientos"));
    }

    @Override
    public RespuestaTransaccion registrarDeposito(String cuenta, BigDecimal importe) {
        SoapObject peticion = peticionProtegida("registrarDeposito");
        peticion.addProperty("cuenta", cuenta);
        peticion.addProperty("importe", importe.toPlainString());
        return transaccion(invocar(peticion));
    }

    @Override
    public RespuestaTransaccion registrarRetiro(String cuenta, BigDecimal importe) {
        SoapObject peticion = peticionProtegida("registrarRetiro");
        peticion.addProperty("cuenta", cuenta);
        peticion.addProperty("importe", importe.toPlainString());
        return transaccion(invocar(peticion));
    }

    @Override
    public RespuestaTransaccion registrarTransferencia(String cuentaOrigen, String cuentaDestino, BigDecimal importe) {
        SoapObject peticion = peticionProtegida("registrarTransferencia");
        peticion.addProperty("cuentaOrigen", cuentaOrigen);
        peticion.addProperty("cuentaDestino", cuentaDestino);
        peticion.addProperty("importe", importe.toPlainString());
        return transaccion(invocar(peticion));
    }

    private static SoapObject peticion(String operacion) {
        return new SoapObject(ESPACIO_NOMBRES, operacion);
    }

    // El token va como primer parámetro; sin sesión se responde como el servidor (401) sin llamarlo
    private SoapObject peticionProtegida(String operacion) {
        SoapObject peticion = peticion(operacion);
        peticion.addProperty("token", manejadorToken.exigirToken());
        return peticion;
    }

    private SoapObject invocar(SoapObject peticion) {
        Object respuesta;
        try {
            respuesta = transporte.llamar(peticion);
        } catch (SoapFault ex) {
            throw new ExcepcionClienteWS(500, "El servidor respondió con un error: " + ex.faultstring);
        } catch (IOException | XmlPullParserException ex) {
            throw new ExcepcionClienteWS(ExcepcionClienteWS.SIN_CONEXION,
                    "No fue posible conectar con el servidor SOAP en " + url + ". Verifique que Payara esté encendido, "
                    + "la IP configurada y que el celular y la PC estén en la misma red.");
        }
        if (!(respuesta instanceof SoapObject objeto)) {
            throw new ExcepcionClienteWS(500, "El servidor devolvió una respuesta vacía.");
        }
        return verificar(objeto);
    }

    // Toda respuesta trae codigo y mensaje: 400 o más es un error que se muestra tal cual al usuario
    private SoapObject verificar(SoapObject respuesta) {
        int codigo = codigo(respuesta);
        if (codigo >= 400) {
            if (codigo == 401) {
                // Token inválido o sesión vencida (CU-10): el cliente vuelve al inicio de sesión
                manejadorToken.limpiar();
            }
            String mensaje = texto(respuesta, "mensaje");
            throw new ExcepcionClienteWS(codigo, mensaje == null || mensaje.trim().isEmpty()
                    ? "El servidor respondió con el código " + codigo + "." : mensaje);
        }
        return respuesta;
    }

    private static RespuestaTransaccion transaccion(SoapObject respuesta) {
        return new RespuestaTransaccion(codigo(respuesta), texto(respuesta, "mensaje"), decimal(respuesta, "saldo"),
                entero(respuesta, "numeroMovimiento"));
    }

    private static int codigo(SoapObject objeto) {
        return entero(objeto, "codigo");
    }

    // ksoap2 entrega cada valor simple como SoapPrimitive; un elemento que no llegó no está en el objeto
    static String texto(SoapObject objeto, String nombre) {
        if (objeto == null || !objeto.hasProperty(nombre)) {
            return null;
        }
        Object valor = objeto.getProperty(nombre);
        if (valor instanceof SoapPrimitive primitivo) {
            return primitivo.toString();
        }
        return valor instanceof SoapObject vacio && vacio.getPropertyCount() == 0 ? "" : String.valueOf(valor);
    }

    private static int entero(SoapObject objeto, String nombre) {
        String valor = texto(objeto, nombre);
        try {
            return valor == null ? 0 : Integer.parseInt(valor.trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static BigDecimal decimal(SoapObject objeto, String nombre) {
        String valor = texto(objeto, nombre);
        try {
            return valor == null ? BigDecimal.ZERO : new BigDecimal(valor.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
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
