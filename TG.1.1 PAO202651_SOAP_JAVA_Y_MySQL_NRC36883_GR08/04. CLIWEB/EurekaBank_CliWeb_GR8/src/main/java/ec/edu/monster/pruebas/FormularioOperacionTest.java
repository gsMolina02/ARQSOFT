package ec.edu.monster.pruebas;

import ec.edu.monster.controlador.CuentaControlador;
import ec.edu.monster.controlador.FormularioOperacion;
import ec.edu.monster.modelo.ExcepcionCampo;
import ec.edu.monster.modelo.OperacionesModelo;
import ec.edu.monster.modelo.TipoOperacion;
import ec.edu.monster.util.ValidacionUtil;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class FormularioOperacionTest {

    private final ContratoFalso contrato = new ContratoFalso();
    private final CuentaControlador cuentas = new CuentaControlador(contrato);

    private static OperacionesModelo formulario(TipoOperacion operacion, String cuenta, String destino, String importe) {
        OperacionesModelo modelo = new OperacionesModelo();
        modelo.setOperacion(operacion);
        modelo.setCuenta(cuenta);
        modelo.setCuentaDestino(destino);
        modelo.setImporte(importe);
        return modelo;
    }

    @Test
    void deberiaRegistrarElDepositoConElImporteEscritoConComa() {
        FormularioOperacion.registrar(cuentas, formulario(TipoOperacion.DEPOSITO, "00100001", null, "25,50"));

        assertEquals("registrarDeposito", contrato.operacionLlamada);
        assertEquals(new BigDecimal("25.50"), contrato.importeRecibido);
    }

    @Test
    void deberiaRegistrarElRetiroYLaTransferencia() {
        FormularioOperacion.registrar(cuentas, formulario(TipoOperacion.RETIRO, "00100001", null, "10"));
        assertEquals("registrarRetiro", contrato.operacionLlamada);

        FormularioOperacion.registrar(cuentas, formulario(TipoOperacion.TRANSFERENCIA, "00100001", "00200001", "1"));
        assertEquals("registrarTransferencia", contrato.operacionLlamada);
        assertEquals("00200001", contrato.cuentaDestinoRecibida);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "DEPOSITO, 0010, NULO, 10, cuenta",
        "DEPOSITO, 00100001, NULO, abc, importe",
        "RETIRO, 00100001, NULO, 10.555, importe",
        "TRANSFERENCIA, 00100001, 123, 10, destino",
        "TRANSFERENCIA, 00100001, 00100001, 10, destino",
        "TRANSFERENCIA, 00100001, 00200001, 0, importe"
    }, nullValues = "NULO")
    void deberiaIndicarElCampoConErrorSinLlamarAlServicio(TipoOperacion operacion, String cuenta, String destino,
            String importe, String campo) {
        ExcepcionCampo error = assertThrows(ExcepcionCampo.class,
                () -> FormularioOperacion.registrar(cuentas, formulario(operacion, cuenta, destino, importe)));

        assertEquals(campo, error.getCampo());
        assertNull(contrato.operacionLlamada);
    }

    @Test
    void deberiaUsarElMensajeDeCuentasIguales() {
        ExcepcionCampo error = assertThrows(ExcepcionCampo.class, () -> FormularioOperacion.registrar(cuentas,
                formulario(TipoOperacion.TRANSFERENCIA, "00100001", "00100001", "10")));

        assertEquals(ValidacionUtil.MENSAJE_CUENTAS_IGUALES, error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(value = {"deposito, DEPOSITO", "RETIRO, RETIRO", " transferencia , TRANSFERENCIA", "otra, MOVIMIENTOS",
        "NULO, MOVIMIENTOS"}, nullValues = "NULO")
    void deberiaLeerLaOperacionDeLaDireccion(String texto, TipoOperacion esperada) {
        assertEquals(esperada, TipoOperacion.desde(texto));
    }

    @Test
    void deberiaArmarLosTextosDeLaConfirmacion() {
        assertEquals("Sí, depositar", TipoOperacion.DEPOSITO.getBotonConfirmacion());
        assertTrue(TipoOperacion.TRANSFERENCIA.getPlantillaConfirmacion().contains("{CuentaDestino}"));
        assertNull(TipoOperacion.MOVIMIENTOS.getPlantillaConfirmacion());
        assertFalse(TipoOperacion.MOVIMIENTOS.isLlevaImporte());
    }

    @Test
    void deberiaMostrarElTituloDelResultadoSegunLaOperacion() {
        OperacionesModelo modelo = new OperacionesModelo();
        assertEquals("Saldo disponible", modelo.getResultadoTitulo());
        assertEquals("—", modelo.getResultadoValor());

        modelo.setOperacion(TipoOperacion.RETIRO);
        assertEquals("Comprobante", modelo.getResultadoTitulo());

        modelo.setError("Saldo insuficiente");
        assertEquals("Saldo insuficiente.", modelo.getError());
        assertTrue(modelo.getMovimientos().isEmpty());
    }
}
