package ec.edu.monster.pruebas;

import ec.edu.monster.controlador.CuentaControlador;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ControladoresTest {

    private final ContratoFalso contrato = new ContratoFalso();
    private final LoginControlador login = new LoginControlador(contrato, "http://localhost:8080/x");
    private final CuentaControlador cuenta = login.crearControladorCuenta();

    @ParameterizedTest
    @CsvSource(value = {"'', Monster9", "Monster, ''", "'  ', Monster9", "NULO, NULO"}, nullValues = "NULO")
    void deberiaExigirUsuarioYContraseniaSinLlamarAlServicio(String usuario, String clave) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> login.iniciarSesion(new LoginModelo(usuario, clave)));

        assertEquals(LoginControlador.MENSAJE_CREDENCIALES_VACIAS, error.getMessage());
        assertNull(contrato.operacionLlamada);
    }

    @Test
    void deberiaQuitarLosEspaciosDelUsuario() {
        login.iniciarSesion(new LoginModelo("  Monster ", "Monster9"));

        assertEquals("iniciarSesion:Monster", contrato.operacionLlamada);
    }

    @Test
    void deberiaValidarLaConfirmacionDeLaNuevaContrasenia() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> login.cambiarClave("Monster9", "Nueva123", "Nueva124"));

        assertEquals(ValidacionUtil.MENSAJE_CONFIRMACION, error.getMessage());
        assertNull(contrato.operacionLlamada);
    }

    @Test
    void deberiaCambiarLaContrasenia() {
        assertEquals("Contraseña actualizada correctamente", login.cambiarClave("Monster9", "Nueva123", "Nueva123"));
        assertEquals("Nueva123", contrato.claveNuevaRecibida);
    }

    @Test
    void deberiaConsultarLaCuentaSinEspacios() {
        cuenta.consultar(" 00100001 ");

        assertEquals("00100001", contrato.cuentaRecibida);
    }

    @Test
    void deberiaRechazarUnaCuentaMalEscritaSinLlamarAlServicio() {
        assertThrows(IllegalArgumentException.class, () -> cuenta.depositar("0010", BigDecimal.TEN));
        assertNull(contrato.operacionLlamada);
    }

    @Test
    void deberiaRechazarImportesNoValidos() {
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar("00100001", new BigDecimal("10.555")));
        assertThrows(IllegalArgumentException.class, () -> cuenta.retirar("00100001", BigDecimal.ZERO));
        assertNull(contrato.operacionLlamada);
    }

    @Test
    void deberiaTransferirEntreCuentasDistintas() {
        cuenta.transferir("00100001", "00200001", new BigDecimal("15.50"));

        assertEquals("registrarTransferencia", contrato.operacionLlamada);
        assertEquals("00200001", contrato.cuentaDestinoRecibida);
        assertEquals(new BigDecimal("15.50"), contrato.importeRecibido);
    }

    @Test
    void deberiaRechazarTransferenciasALaMismaCuenta() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> cuenta.transferir("00100001", "00100001", BigDecimal.ONE));

        assertEquals(ValidacionUtil.MENSAJE_CUENTAS_IGUALES, error.getMessage());
        assertNull(contrato.operacionLlamada);
    }
}
