package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.ManejadorTokenSOAP;
import ec.edu.monster.cliente.dto.MovimientoDTO;
import ec.edu.monster.cliente.dto.RespuestaCuentas;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteWSEurekaBankTest {

    private static final String URL = "http://localhost:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private final PuertoFalso puerto = new PuertoFalso();
    private final ManejadorTokenSOAP token = new ManejadorTokenSOAP();
    private final ClienteWSEurekaBank cliente = new ClienteWSEurekaBank(URL, puerto, token);

    private void iniciarSesion() {
        cliente.iniciarSesion("Monster", "Monster9");
    }

    @Test
    void deberiaGuardarElTokenYElEmpleadoAlIniciarSesion() {
        puerto.mensaje = "Bienvenido, Monster EurekaBank";

        RespuestaSesion sesion = cliente.iniciarSesion("Monster", "Monster9");

        assertEquals("Bienvenido, Monster EurekaBank", sesion.mensaje());
        assertEquals(30, sesion.minutosVigencia());
        assertTrue(cliente.haySesionActiva());
        assertEquals("token-de-prueba", token.obtenerToken());
        assertEquals("Monster EurekaBank", cliente.getEmpleado());
    }

    @Test
    void deberiaConvertirUnCodigoDeErrorEnExcepcionConElMensajeDelServidor() {
        puerto.codigo = 401;
        puerto.mensaje = "Usuario o contraseña incorrectos";

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.iniciarSesion("Monster", "x"));

        assertEquals(401, error.getCodigoEstado());
        assertEquals("Usuario o contraseña incorrectos", error.getMessage());
        assertFalse(cliente.haySesionActiva());
    }

    @Test
    void deberiaEnviarElTokenComoParametro() {
        iniciarSesion();

        cliente.registrarDeposito("00100001", new BigDecimal("25.50"));

        assertEquals("registrarDeposito", puerto.operacion);
        assertEquals("token-de-prueba", puerto.tokenRecibido);
        assertEquals("00100001", puerto.cuentaRecibida);
        assertEquals(25.50, puerto.importeRecibido);
    }

    @Test
    void deberiaConvertirLaRespuestaDeLaTransaccion() {
        iniciarSesion();
        puerto.codigo = 201;
        puerto.mensaje = "Depósito registrado";

        RespuestaTransaccion respuesta = cliente.registrarRetiro("00100001", BigDecimal.TEN);

        assertEquals(201, respuesta.codigo());
        assertEquals(new BigDecimal("591.00"), respuesta.saldo());
        assertEquals(8, respuesta.numeroMovimiento());
    }

    @Test
    void deberiaConvertirLaCuentaYSusMovimientosConSusFechas() {
        iniciarSesion();

        RespuestaMovimientos respuesta = cliente.consultarMovimientos("00100001");

        assertEquals("Carlos Andrés Pérez Salazar", respuesta.cuenta().cliente());
        assertEquals(LocalDate.of(2026, 1, 15), respuesta.cuenta().fechaApertura());
        assertEquals(2, respuesta.movimientos().size());
        MovimientoDTO transferencia = respuesta.movimientos().get(1);
        assertEquals(LocalDateTime.of(2026, 11, 4, 9, 15, 7), transferencia.fechaHora());
        assertFalse(transferencia.esIngreso());
        assertEquals("00200001", transferencia.cuentaReferencia());
        assertTrue(respuesta.movimientos().get(0).esIngreso());
    }

    @Test
    void deberiaListarLasCuentasConElTokenDeLaSesion() {
        iniciarSesion();

        RespuestaCuentas respuesta = cliente.listarCuentas();

        assertEquals("listarCuentas", puerto.operacion);
        assertEquals("token-de-prueba", puerto.tokenRecibido);
        assertEquals(2, respuesta.cuentas().size());
        assertTrue(respuesta.cuentas().get(0).estaActiva());
        assertEquals("ANULADO", respuesta.cuentas().get(1).estado());
    }

    @Test
    void deberiaExigirSesionSinLlamarAlServidor() {
        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.consultarMovimientos("00100001"));

        assertEquals(401, error.getCodigoEstado());
        assertEquals(ManejadorTokenSOAP.MENSAJE_SIN_SESION, error.getMessage());
        assertNull(puerto.operacion);
    }

    @Test
    void deberiaBorrarElTokenSiLaSesionVencio() {
        iniciarSesion();
        puerto.codigo = 401;
        puerto.mensaje = "La sesión ha expirado. Inicie sesión nuevamente.";

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.consultarMovimientos("00100001"));

        assertTrue(error.esErrorAutenticacion());
        assertFalse(cliente.haySesionActiva());
    }

    @Test
    void deberiaConservarElTokenAnteUnaReglaDeNegocio() {
        iniciarSesion();
        puerto.codigo = 409;
        puerto.mensaje = "Saldo insuficiente. Saldo disponible: $540.00";

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.registrarRetiro("00100001", new BigDecimal("1000")));

        assertEquals(409, error.getCodigoEstado());
        assertTrue(cliente.haySesionActiva());
    }

    @Test
    void deberiaInformarLaFaltaDeConexion() {
        puerto.sinConexion = true;

        ExcepcionClienteWS error = assertThrows(ExcepcionClienteWS.class,
                () -> cliente.iniciarSesion("Monster", "Monster9"));

        assertTrue(error.esErrorConexion());
        assertTrue(error.getMessage().contains(URL));
    }

    @Test
    void deberiaBorrarElTokenAlCerrarSesionAunqueElServidorNoResponda() {
        iniciarSesion();
        puerto.sinConexion = true;

        assertThrows(ExcepcionClienteWS.class, cliente::cerrarSesion);
        assertFalse(cliente.haySesionActiva());
        assertNull(cliente.getEmpleado());
    }

    @Test
    void deberiaCerrarLaSesionEnElServidor() {
        iniciarSesion();
        puerto.mensaje = "Sesión cerrada correctamente";

        assertEquals("Sesión cerrada correctamente", cliente.cerrarSesion());
        assertEquals("token-de-prueba", puerto.tokenRecibido);
        assertFalse(cliente.haySesionActiva());
    }

    @Test
    void deberiaCrearElPuertoRealDesdeElContratoIncluidoEnElJar() {
        assertDoesNotThrow(() -> new ClienteWSEurekaBank(URL));
    }
}
