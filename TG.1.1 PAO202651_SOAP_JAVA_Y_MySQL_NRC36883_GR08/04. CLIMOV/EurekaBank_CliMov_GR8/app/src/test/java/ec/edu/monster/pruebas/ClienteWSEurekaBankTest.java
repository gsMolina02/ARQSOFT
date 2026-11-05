package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.ManejadorTokenSOAP;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;

import static ec.edu.monster.pruebas.TransporteFalso.objeto;
import static ec.edu.monster.pruebas.TransporteFalso.parametro;
import static ec.edu.monster.pruebas.TransporteFalso.respuesta;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteWSEurekaBankTest {

    private static final String URL = "http://10.0.2.2:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private final TransporteFalso transporte = new TransporteFalso();
    private final ClienteWSEurekaBank cliente = new ClienteWSEurekaBank(URL, transporte, new ManejadorTokenSOAP());

    private void iniciarSesion() {
        transporte.responder(respuesta("codigo", "200", "mensaje", "Bienvenido", "token", "abc-123",
                "usuario", "Monster", "empleado", "Monster EurekaBank", "minutosVigencia", "30"));
        cliente.iniciarSesion("Monster", "Monster9");
    }

    @Test
    void deberiaGuardarElTokenYElEmpleadoAlIniciarSesion() {
        transporte.responder(respuesta("codigo", "200", "mensaje", "Bienvenido", "token", "abc-123",
                "usuario", "Monster", "empleado", "Monster EurekaBank", "minutosVigencia", "30"));

        RespuestaSesion sesion = cliente.iniciarSesion("Monster", "Monster9");

        assertEquals("abc-123", sesion.token());
        assertEquals(30, sesion.minutosVigencia());
        assertEquals("Monster EurekaBank", cliente.getEmpleado());
        assertTrue(cliente.haySesionActiva());
        SoapObject peticion = transporte.ultimaPeticion();
        assertEquals("iniciarSesion", peticion.getName());
        assertEquals("Monster", parametro(peticion, "usuario"));
        assertEquals("Monster9", parametro(peticion, "clave"));
    }

    @Test
    void deberiaConvertirUnCodigoDeErrorEnExcepcionConElMensajeDelServidor() {
        transporte.responder(respuesta("codigo", "401", "mensaje", "Usuario o contraseña incorrectos"));

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.iniciarSesion("Monster", "mala"));

        assertEquals(401, error.getCodigoEstado());
        assertEquals("Usuario o contraseña incorrectos", error.getMessage());
        assertFalse(cliente.haySesionActiva());
    }

    @Test
    void deberiaEnviarElTokenComoPrimerParametro() {
        iniciarSesion();
        transporte.responder(respuesta("codigo", "201", "mensaje", "Depósito registrado", "saldo", "100.50",
                "numeroMovimiento", "7"));

        cliente.registrarDeposito("00100001", new BigDecimal("100.50"));

        SoapObject peticion = transporte.ultimaPeticion();
        assertEquals("registrarDeposito", peticion.getName());
        assertEquals("token", peticion.getPropertyInfo(0).getName());
        assertEquals("abc-123", parametro(peticion, "token"));
        assertEquals("00100001", parametro(peticion, "cuenta"));
        assertEquals("100.50", parametro(peticion, "importe"));
    }

    @Test
    void deberiaConvertirLaRespuestaDeLaTransaccion() {
        iniciarSesion();
        transporte.responder(respuesta("codigo", "201", "mensaje", "Transferencia registrada", "saldo", "1250.75",
                "numeroMovimiento", "41"));

        RespuestaTransaccion respuesta = cliente.registrarTransferencia("00100001", "00200001", new BigDecimal("40"));

        assertEquals(201, respuesta.codigo());
        assertEquals(new BigDecimal("1250.75"), respuesta.saldo());
        assertEquals(41, respuesta.numeroMovimiento());
        SoapObject peticion = transporte.ultimaPeticion();
        assertEquals("00100001", parametro(peticion, "cuentaOrigen"));
        assertEquals("00200001", parametro(peticion, "cuentaDestino"));
    }

    @Test
    void deberiaListarLasCuentasConSuEstado() {
        iniciarSesion();
        SoapObject respuesta = respuesta("codigo", "200", "mensaje", "Se encontraron 2 cuenta(s)");
        SoapObject cuentas = objeto("cuentas");
        cuentas.addProperty("cuenta", objeto("cuenta", "numero", "00100001", "cliente", "Carlos Andrés Pérez Salazar",
                "estado", "ACTIVO", "saldo", "540.00", "fechaApertura", "2026-01-15", "contadorMovimientos", "4"));
        cuentas.addProperty("cuenta", objeto("cuenta", "numero", "00100003", "cliente", "Diego Sebastián Naranjo Toapanta",
                "estado", "ANULADO", "saldo", "0.00", "fechaApertura", "2026-05-12", "contadorMovimientos", "1"));
        respuesta.addProperty("cuentas", cuentas);
        transporte.responder(respuesta);

        RespuestaCuentas resultado = cliente.listarCuentas();

        assertEquals(2, resultado.cuentas().size());
        assertTrue(resultado.cuentas().get(0).estaActiva());
        assertEquals("ANULADO", resultado.cuentas().get(1).estado());
        assertEquals(new BigDecimal("540.00"), resultado.cuentas().get(0).saldo());
    }

    @Test
    void deberiaConvertirLaCuentaYSusMovimientosConSusFechas() {
        iniciarSesion();
        SoapObject respuesta = respuesta("codigo", "200", "mensaje", "La cuenta registra 2 movimientos");
        respuesta.addProperty("cuenta", objeto("cuenta", "numero", "00100001", "cliente", "Coronel Castillo, Eric Gustavo",
                "cedula", "0102030405", "moneda", "Dólares", "simboloMoneda", "$", "sucursal", "Sipan",
                "estado", "ACTIVO", "saldo", "6900.00", "fechaApertura", "2008-01-06", "contadorMovimientos", "2"));
        SoapObject movimientos = objeto("movimientos");
        movimientos.addProperty("movimiento", objeto("movimiento", "numero", "1", "fechaHora", "2008-01-06 10:15:00",
                "codigoTipo", "001", "tipo", "Apertura de Cuenta", "accion", "INGRESO", "importe", "2800.00",
                "empleado", "Monster EurekaBank"));
        movimientos.addProperty("movimiento", objeto("movimiento", "numero", "2", "fechaHora", "2026-11-04 09:30:05",
                "codigoTipo", "009", "tipo", "Transferencia", "accion", "SALIDA", "importe", "40.00",
                "cuentaReferencia", "00200001", "empleado", "Monster EurekaBank"));
        respuesta.addProperty("movimientos", movimientos);
        transporte.responder(respuesta);

        RespuestaMovimientos resultado = cliente.consultarMovimientos("00100001");

        assertEquals("Coronel Castillo, Eric Gustavo", resultado.cuenta().cliente());
        assertEquals(new BigDecimal("6900.00"), resultado.cuenta().saldo());
        assertEquals(LocalDate.of(2008, 1, 6), resultado.cuenta().fechaApertura());
        assertEquals(2, resultado.movimientos().size());
        MovimientoDTO apertura = resultado.movimientos().get(0);
        assertTrue(apertura.esIngreso());
        assertNull(apertura.cuentaReferencia());
        MovimientoDTO transferencia = resultado.movimientos().get(1);
        assertFalse(transferencia.esIngreso());
        assertEquals("00200001", transferencia.cuentaReferencia());
        assertEquals(LocalDateTime.of(2026, 11, 4, 9, 30, 5), transferencia.fechaHora());
    }

    @Test
    void deberiaAceptarUnaCuentaSinMovimientos() {
        iniciarSesion();
        SoapObject respuesta = respuesta("codigo", "200", "mensaje", "La cuenta no registra movimientos");
        respuesta.addProperty("cuenta", objeto("cuenta", "numero", "00400001", "estado", "ANULADO", "saldo", "0.00"));
        transporte.responder(respuesta);

        RespuestaMovimientos resultado = cliente.consultarMovimientos("00400001");

        assertTrue(resultado.movimientos().isEmpty());
        assertEquals("ANULADO", resultado.cuenta().estado());
        // Sin simboloMoneda se usa el dólar
        assertEquals("$", resultado.cuenta().simboloMoneda());
    }

    @Test
    void deberiaExigirSesionSinLlamarAlServidor() {
        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.consultarMovimientos("00100001"));

        assertTrue(error.esErrorAutenticacion());
        assertTrue(transporte.peticiones.isEmpty());
    }

    @Test
    void deberiaBorrarElTokenSiLaSesionVencio() {
        iniciarSesion();
        transporte.responder(respuesta("codigo", "401", "mensaje", "La sesión expiró por inactividad"));

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.consultarMovimientos("00100001"));

        assertEquals("La sesión expiró por inactividad", error.getMessage());
        assertFalse(cliente.haySesionActiva());
    }

    @Test
    void deberiaConservarElTokenAnteUnaReglaDeNegocio() {
        iniciarSesion();
        transporte.responder(respuesta("codigo", "409", "mensaje", "Saldo insuficiente"));

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.registrarRetiro("00100001", new BigDecimal("999999")));

        assertEquals(409, error.getCodigoEstado());
        assertTrue(cliente.haySesionActiva());
    }

    @Test
    void deberiaUsarUnMensajeGenericoSiElErrorLlegaSinMensaje() {
        iniciarSesion();
        transporte.responder(respuesta("codigo", "500"));

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.registrarRetiro("00100001", BigDecimal.TEN));

        assertEquals("El servidor respondió con el código 500.", error.getMessage());
    }

    @Test
    void deberiaInformarLaFaltaDeConexion() {
        transporte.fallar(new SocketTimeoutException("connect timed out"));

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.iniciarSesion("Monster", "Monster9"));

        assertTrue(error.esErrorConexion());
        assertTrue(error.getMessage().contains(URL));
    }

    @Test
    void deberiaConvertirUnSoapFaultEnError500() {
        SoapFault falla = new SoapFault();
        falla.faultstring = "Error interno";
        transporte.fallar(falla);

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.iniciarSesion("Monster", "Monster9"));

        assertEquals(500, error.getCodigoEstado());
        assertTrue(error.getMessage().endsWith("Error interno"));
    }

    @Test
    void deberiaRechazarUnaRespuestaVacia() {
        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.iniciarSesion("Monster", "Monster9"));

        assertEquals(500, error.getCodigoEstado());
    }

    @Test
    void deberiaBorrarElTokenAlCerrarSesionAunqueElServidorNoResponda() {
        iniciarSesion();
        transporte.fallar(new IOException("Connection refused"));

        assertThrows(ExcepcionClienteWS.class, cliente::cerrarSesion);

        assertFalse(cliente.haySesionActiva());
    }

    @Test
    void deberiaCerrarLaSesionEnElServidor() {
        iniciarSesion();
        transporte.responder(respuesta("codigo", "200", "mensaje", "Sesión cerrada correctamente"));

        assertEquals("Sesión cerrada correctamente", cliente.cerrarSesion());
        assertEquals("cerrarSesion", transporte.ultimaPeticion().getName());
        assertEquals("abc-123", parametro(transporte.ultimaPeticion(), "token"));
        assertFalse(cliente.haySesionActiva());
    }
}
