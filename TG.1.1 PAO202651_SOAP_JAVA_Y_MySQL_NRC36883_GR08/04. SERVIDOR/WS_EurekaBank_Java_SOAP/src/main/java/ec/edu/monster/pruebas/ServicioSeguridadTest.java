package ec.edu.monster.pruebas;

import ec.edu.monster.excepciones.ExcepcionAutenticacion;
import ec.edu.monster.excepciones.ExcepcionValidacion;
import ec.edu.monster.modelos.Sesion;
import ec.edu.monster.modelos.SesionActiva;
import ec.edu.monster.modelos.Usuario;
import ec.edu.monster.modelos.dto.Respuesta;
import ec.edu.monster.modelos.dto.RespuestaSesion;
import ec.edu.monster.pruebas.falsos.DatosPrueba;
import ec.edu.monster.pruebas.falsos.FabricaFalsa;
import ec.edu.monster.pruebas.falsos.SesionDAOFalso;
import ec.edu.monster.pruebas.falsos.UsuarioDAOFalso;
import ec.edu.monster.seguridad.ServicioSeguridad;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioSeguridadTest {

    private final FabricaFalsa fabrica = new FabricaFalsa();
    private final UsuarioDAOFalso usuarios = new UsuarioDAOFalso().con(DatosPrueba.usuarioMonster());
    private final SesionDAOFalso sesiones = new SesionDAOFalso();
    private final ServicioSeguridad servicio =
            new ServicioSeguridad(fabrica, usuarios, sesiones, DatosPrueba.reloj(DatosPrueba.AHORA));

    @Test
    void deberiaCalcularElHashSha256EnHexadecimal() {
        assertEquals("7fbdfcdd431cb77a5bce52182f863d807544fdbea2edb49dc396d5ba30481f64",
                Usuario.calcularHash("Monster9"));
    }

    @Test
    void deberiaIniciarSesionYRegistrarla() throws Exception {
        RespuestaSesion respuesta = servicio.iniciarSesion(" Monster ", "Monster9", "192.168.1.30");

        assertEquals(200, respuesta.getCodigo());
        assertEquals("Bienvenido, Monster EurekaBank", respuesta.getMensaje());
        assertEquals("Monster", respuesta.getUsuario());
        assertEquals("Monster EurekaBank", respuesta.getEmpleado());
        assertEquals(30, respuesta.getMinutosVigencia());
        assertDoesNotThrow(() -> UUID.fromString(respuesta.getToken()));

        Sesion sesion = sesiones.registradas.get(0);
        assertEquals(respuesta.getToken(), sesion.getToken());
        assertEquals("Monster", sesion.getNombreUsuario());
        assertEquals(DatosPrueba.AHORA, sesion.getFechaIngreso());
        assertEquals(DatosPrueba.AHORA, sesion.getUltimaActividad());
        assertNull(sesion.getFechaSalida());
        assertEquals("192.168.1.30", sesion.getIp());
        assertTrue(fabrica.unidades.get(0).cerrada);
    }

    @ParameterizedTest
    @CsvSource({
        "Monster, clave-errada",
        "NoExiste, Monster9",
        "Monster, monster9"
    })
    void deberiaRechazarCredencialesIncorrectasSinRevelarCualFallo(String usuario, String clave) {
        ExcepcionAutenticacion error = assertThrows(ExcepcionAutenticacion.class,
                () -> servicio.iniciarSesion(usuario, clave, "::1"));

        assertEquals(401, error.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_CREDENCIALES_INCORRECTAS, error.getMessage());
        assertTrue(sesiones.registradas.isEmpty());
    }

    @Test
    void deberiaRechazarUsuariosNoActivos() {
        usuarios.con(DatosPrueba.usuarioMonster("INACTIVO"));

        ExcepcionAutenticacion error = assertThrows(ExcepcionAutenticacion.class,
                () -> servicio.iniciarSesion("Monster", "Monster9", "::1"));

        assertEquals(ServicioSeguridad.MENSAJE_CREDENCIALES_INCORRECTAS, error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "'', Monster9",
        "Monster, ''",
        "'   ', Monster9",
        "NULO, NULO"
    }, nullValues = "NULO")
    void deberiaExigirUsuarioYContrasenia(String usuario, String clave) {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.iniciarSesion(usuario, clave, "::1"));

        assertEquals(400, error.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_CREDENCIALES_VACIAS, error.getMessage());
        assertTrue(fabrica.unidades.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void deberiaRechazarUnTokenAusente(String token) {
        ExcepcionAutenticacion error = assertThrows(ExcepcionAutenticacion.class, () -> servicio.validarToken(token));

        assertEquals(401, error.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_SIN_TOKEN, error.getMessage());
    }

    @Test
    void deberiaRechazarUnTokenNoRegistrado() {
        ExcepcionAutenticacion error = assertThrows(ExcepcionAutenticacion.class,
                () -> servicio.validarToken("no-existe"));

        assertEquals(ServicioSeguridad.MENSAJE_TOKEN_INVALIDO, error.getMessage());
    }

    @Test
    void deberiaRenovarLaActividadDeUnaSesionVigente() throws Exception {
        sesiones.con(DatosPrueba.sesionMonster(DatosPrueba.AHORA.minusMinutes(30)));

        SesionActiva sesion = servicio.validarToken(" " + DatosPrueba.TOKEN + " ");

        assertEquals("0001", sesion.getCodigoEmpleado());
        assertEquals(DatosPrueba.AHORA, sesiones.actividad.get(DatosPrueba.TOKEN));
        assertFalse(sesiones.cerradas.containsKey(DatosPrueba.TOKEN));
    }

    @Test
    void deberiaCerrarLaSesionTrasTreintaMinutosSinUso() {
        sesiones.con(DatosPrueba.sesionMonster(DatosPrueba.AHORA.minusMinutes(30).minusSeconds(1)));

        ExcepcionAutenticacion error = assertThrows(ExcepcionAutenticacion.class,
                () -> servicio.validarToken(DatosPrueba.TOKEN));

        assertEquals(ServicioSeguridad.MENSAJE_SESION_EXPIRADA, error.getMessage());
        assertEquals(DatosPrueba.AHORA, sesiones.cerradas.get(DatosPrueba.TOKEN));
        assertFalse(sesiones.actividad.containsKey(DatosPrueba.TOKEN));
    }

    @Test
    void deberiaCambiarLaContrasenia() throws Exception {
        Respuesta respuesta = servicio.cambiarClave(DatosPrueba.sesionMonster(), "Monster9", "NuevaClave1");

        assertEquals(200, respuesta.getCodigo());
        assertEquals("Contraseña actualizada correctamente", respuesta.getMensaje());
        assertTrue(usuarios.usuarios.get("Monster").verificarClave("NuevaClave1"));
    }

    @Test
    void deberiaRechazarElCambioSiLaContraseniaActualEsIncorrecta() {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.cambiarClave(DatosPrueba.sesionMonster(), "otra", "NuevaClave1"));

        assertEquals(400, error.getCodigo());
        assertEquals(ServicioSeguridad.MENSAJE_CLAVE_ACTUAL_INCORRECTA, error.getMessage());
        assertTrue(usuarios.usuarios.get("Monster").verificarClave("Monster9"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"corta", "Monster9"})
    void deberiaRechazarContraseniasNuevasNoValidas(String nueva) {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.cambiarClave(DatosPrueba.sesionMonster(), "Monster9", nueva));

        assertEquals(ServicioSeguridad.MENSAJE_CLAVE_NUEVA_INVALIDA, error.getMessage());
        assertTrue(usuarios.usuarios.get("Monster").verificarClave("Monster9"));
    }

    @Test
    void deberiaCerrarLaSesion() throws Exception {
        Respuesta respuesta = servicio.cerrarSesion(DatosPrueba.sesionMonster());

        assertEquals(200, respuesta.getCodigo());
        assertEquals("Sesión cerrada correctamente", respuesta.getMensaje());
        assertEquals(DatosPrueba.AHORA, sesiones.cerradas.get(DatosPrueba.TOKEN));
    }
}
