package ec.edu.monster.pruebas;

import ec.edu.monster.modelos.dto.Respuesta;
import ec.edu.monster.modelos.dto.RespuestaCuentas;
import ec.edu.monster.modelos.dto.RespuestaMovimientos;
import ec.edu.monster.modelos.dto.RespuestaSesion;
import ec.edu.monster.modelos.dto.RespuestaTransaccion;
import ec.edu.monster.pruebas.falsos.CuentaDAOFalso;
import ec.edu.monster.pruebas.falsos.DatosPrueba;
import ec.edu.monster.pruebas.falsos.FabricaFalsa;
import ec.edu.monster.pruebas.falsos.MovimientoDAOFalso;
import ec.edu.monster.pruebas.falsos.SesionDAOFalso;
import ec.edu.monster.pruebas.falsos.UsuarioDAOFalso;
import ec.edu.monster.seguridad.ServicioSeguridad;
import ec.edu.monster.servicios.ServicioCuenta;
import ec.edu.monster.servicios.ServicioTransaccion;
import ec.edu.monster.ws.ManejadorExcepciones;
import ec.edu.monster.ws.WSEurekaBank;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Clock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Operaciones del servicio SOAP con los DAO falsos: el éxito y cada error llegan como {codigo, mensaje}.
 */
public class WSEurekaBankTest {

    private final FabricaFalsa fabrica = new FabricaFalsa();
    private final SesionDAOFalso sesiones = new SesionDAOFalso().con(DatosPrueba.sesionMonster());
    private final CuentaDAOFalso cuentas = new CuentaDAOFalso().con(DatosPrueba.cuenta("00100001", "540.00", 4));
    private final WSEurekaBank servicio;

    public WSEurekaBankTest() {
        Clock reloj = DatosPrueba.reloj(DatosPrueba.AHORA);
        MovimientoDAOFalso movimientos = new MovimientoDAOFalso();
        servicio = new WSEurekaBank(
                new ServicioSeguridad(fabrica, new UsuarioDAOFalso().con(DatosPrueba.usuarioMonster()), sesiones, reloj),
                new ServicioCuenta(fabrica, cuentas, movimientos),
                new ServicioTransaccion(fabrica, cuentas, movimientos, reloj));
    }

    @Test
    void deberiaIniciarSesionGuardandoUnaIpDesconocidaFueraDeUnaPeticionSoap() {
        RespuestaSesion respuesta = servicio.iniciarSesion("Monster", "Monster9");

        assertEquals(200, respuesta.getCodigo());
        assertNotNull(respuesta.getToken());
        assertEquals("desconocida", sesiones.registradas.get(0).getIp());
    }

    @Test
    void deberiaResponderLasCredencialesIncorrectasDentroDeLaRespuesta() {
        RespuestaSesion respuesta = servicio.iniciarSesion("Monster", "otra");

        assertEquals(401, respuesta.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_CREDENCIALES_INCORRECTAS, respuesta.getMensaje());
        assertNull(respuesta.getToken());
    }

    @Test
    void deberiaExigirElTokenEnLasOperaciones() {
        RespuestaMovimientos respuesta = servicio.consultarMovimientos(null, "00100001");

        assertEquals(401, respuesta.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_SIN_TOKEN, respuesta.getMensaje());
        assertNull(respuesta.getCuenta());
    }

    @Test
    void deberiaValidarElTokenAntesQueLosDatos() {
        RespuestaTransaccion respuesta = servicio.registrarDeposito("otro-token", "123", 0);

        assertEquals(ServicioSeguridad.MENSAJE_TOKEN_INVALIDO, respuesta.getMensaje());
    }

    @Test
    void deberiaListarLasCuentasSoloConUnTokenValido() {
        RespuestaCuentas sinToken = servicio.listarCuentas(null);
        assertEquals(401, sinToken.getCodigo());
        assertTrue(sinToken.getCuentas().isEmpty());

        RespuestaCuentas respuesta = servicio.listarCuentas(DatosPrueba.TOKEN);
        assertEquals(200, respuesta.getCodigo());
        assertEquals("00100001", respuesta.getCuentas().get(0).getNumero());
    }

    @Test
    void deberiaRegistrarElDepositoConElTokenDeLaSesion() {
        RespuestaTransaccion respuesta = servicio.registrarDeposito(DatosPrueba.TOKEN, "00100001", 10);

        assertEquals(201, respuesta.getCodigo());
        assertEquals(new BigDecimal("550.00"), respuesta.getSaldo());
        assertEquals(5, respuesta.getNumeroMovimiento());
    }

    @Test
    void deberiaResponderLasReglasDeNegocioComo409() {
        RespuestaTransaccion respuesta = servicio.registrarRetiro(DatosPrueba.TOKEN, "00100001", 1000);

        assertEquals(409, respuesta.getCodigo());
        assertEquals("Saldo insuficiente. Saldo disponible: $540.00", respuesta.getMensaje());
    }

    @Test
    void deberiaResponderLaCuentaInexistenteComo404() {
        RespuestaTransaccion respuesta = servicio.registrarTransferencia(DatosPrueba.TOKEN, "00100001", "99999999", 5);

        assertEquals(404, respuesta.getCodigo());
        assertEquals("La cuenta 99999999 no existe", respuesta.getMensaje());
    }

    @Test
    void deberiaCambiarLaClaveYCerrarLaSesion() {
        Respuesta cambio = servicio.cambiarClave(DatosPrueba.TOKEN, "Monster9", "NuevaClave1");
        Respuesta cierre = servicio.cerrarSesion(DatosPrueba.TOKEN);
        Respuesta despues = servicio.cerrarSesion(DatosPrueba.TOKEN);

        assertEquals(200, cambio.getCodigo());
        assertEquals("Sesión cerrada correctamente", cierre.getMensaje());
        assertEquals(401, despues.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_TOKEN_INVALIDO, despues.getMensaje());
    }

    @Test
    void deberiaResponder500SiLaBaseDeDatosNoEstaDisponible() {
        fabrica.error = new SQLException("Communications link failure");

        RespuestaSesion respuesta = servicio.iniciarSesion("Monster", "Monster9");

        assertEquals(500, respuesta.getCodigo());
        assertEquals(ManejadorExcepciones.MENSAJE_BASE_DATOS, respuesta.getMensaje());
    }
}
