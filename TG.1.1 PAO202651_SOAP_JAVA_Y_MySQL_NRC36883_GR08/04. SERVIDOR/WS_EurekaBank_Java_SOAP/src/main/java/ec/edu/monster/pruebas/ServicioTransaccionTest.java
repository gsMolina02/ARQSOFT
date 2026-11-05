package ec.edu.monster.pruebas;

import ec.edu.monster.excepciones.ExcepcionNoEncontrado;
import ec.edu.monster.excepciones.ExcepcionReglaNegocio;
import ec.edu.monster.excepciones.ExcepcionValidacion;
import ec.edu.monster.modelos.Movimiento;
import ec.edu.monster.modelos.SesionActiva;
import ec.edu.monster.modelos.TipoMovimiento;
import ec.edu.monster.modelos.dto.RespuestaTransaccion;
import ec.edu.monster.pruebas.falsos.CuentaDAOFalso;
import ec.edu.monster.pruebas.falsos.DatosPrueba;
import ec.edu.monster.pruebas.falsos.FabricaFalsa;
import ec.edu.monster.pruebas.falsos.MovimientoDAOFalso;
import ec.edu.monster.servicios.ServicioTransaccion;
import ec.edu.monster.servicios.ValidadorDatos;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioTransaccionTest {

    private final FabricaFalsa fabrica = new FabricaFalsa();
    private final CuentaDAOFalso cuentas = new CuentaDAOFalso().con(
            DatosPrueba.cuenta("00100001", "540.00", 4),
            DatosPrueba.cuenta("00200001", "530.25", 3),
            DatosPrueba.cuenta("00100003", "20.00", "ANULADO", "USD", 1),
            DatosPrueba.cuenta("00900001", "100.00", "ACTIVO", "EUR", 1));
    private final MovimientoDAOFalso movimientos = new MovimientoDAOFalso();
    private final ServicioTransaccion servicio =
            new ServicioTransaccion(fabrica, cuentas, movimientos, DatosPrueba.reloj(DatosPrueba.AHORA));
    private final SesionActiva sesion = DatosPrueba.sesionMonster();

    @Test
    void deberiaRegistrarElDepositoActualizandoSaldoYContador() throws Exception {
        RespuestaTransaccion respuesta = servicio.registrarDeposito("00100001", 100.50, sesion);

        assertEquals(201, respuesta.getCodigo());
        assertEquals("Depósito de $100.50 en la cuenta 00100001 registrado correctamente", respuesta.getMensaje());
        assertEquals(new BigDecimal("640.50"), respuesta.getSaldo());
        assertEquals(5, respuesta.getNumeroMovimiento());
        assertEquals(new BigDecimal("640.50"), cuentas.cuentas.get("00100001").getSaldo());
        assertEquals(5, cuentas.cuentas.get("00100001").getContadorMovimientos());

        assertEquals(1, movimientos.registrados.size());
        Movimiento movimiento = movimientos.registrados.get(0);
        assertEquals(TipoMovimiento.DEPOSITO, movimiento.getCodigoTipo());
        assertEquals(5, movimiento.getNumero());
        assertEquals("0001", movimiento.getCodigoEmpleado());
        assertEquals(new BigDecimal("100.50"), movimiento.getImporte());
        assertEquals(DatosPrueba.AHORA, movimiento.getFechaHora());
        assertNull(movimiento.getCuentaReferencia());
        assertTransaccionConfirmada();
    }

    @Test
    void deberiaRegistrarElRetiro() throws Exception {
        RespuestaTransaccion respuesta = servicio.registrarRetiro("00100001", 540, sesion);

        assertEquals("Retiro de $540.00 en la cuenta 00100001 registrado correctamente", respuesta.getMensaje());
        assertEquals(new BigDecimal("0.00"), respuesta.getSaldo());
        assertEquals(TipoMovimiento.RETIRO, movimientos.registrados.get(0).getCodigoTipo());
        assertTransaccionConfirmada();
    }

    @Test
    void deberiaRechazarElRetiroConSaldoInsuficienteSinModificarNada() {
        ExcepcionReglaNegocio error = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicio.registrarRetiro("00100001", 540.01, sesion));

        assertEquals(409, error.getCodigo());
        assertEquals("Saldo insuficiente. Saldo disponible: $540.00", error.getMessage());
        assertSinCambios();
    }

    @ParameterizedTest
    @ValueSource(doubles = {0, -5, 10.555, 0.001, Double.NaN, Double.POSITIVE_INFINITY})
    void deberiaRechazarImportesNoValidos(double importe) {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.registrarDeposito("00100001", importe, sesion));

        assertEquals(400, error.getCodigo());
        assertEquals(ValidadorDatos.MENSAJE_IMPORTE, error.getMessage());
        assertTrue(fabrica.unidades.isEmpty());
    }

    @Test
    void deberiaRechazarImportesMayoresAlMaximo() {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.registrarDeposito("00100001", 10_000_000_000.00, sesion));

        assertEquals(ValidadorDatos.MENSAJE_IMPORTE_MAXIMO, error.getMessage());
    }

    @Test
    void deberiaAceptarImportesConDecimalesQueElDoubleNoRepresentaExactos() throws Exception {
        // 0.1 + 0.2 en double es 0.30000000000000004; con BigDecimal el saldo queda exacto
        servicio.registrarDeposito("00100001", 0.1, sesion);
        RespuestaTransaccion respuesta = servicio.registrarDeposito("00100001", 0.2, sesion);

        assertEquals(new BigDecimal("540.30"), respuesta.getSaldo());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"123", "abcdefgh", "001000011"})
    void deberiaRechazarNumerosDeCuentaSinOchoDigitos(String cuenta) {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.registrarDeposito(cuenta, 10, sesion));

        assertEquals(ValidadorDatos.MENSAJE_CUENTA, error.getMessage());
    }

    @Test
    void deberiaResponder404SiLaCuentaNoExiste() {
        ExcepcionNoEncontrado error = assertThrows(ExcepcionNoEncontrado.class,
                () -> servicio.registrarDeposito("99999999", 10, sesion));

        assertEquals(404, error.getCodigo());
        assertEquals("La cuenta 99999999 no existe", error.getMessage());
        assertSinCambios();
    }

    @Test
    void deberiaRechazarOperacionesSobreCuentasNoActivas() {
        ExcepcionReglaNegocio error = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicio.registrarDeposito("00100003", 10, sesion));

        assertEquals("La cuenta 00100003 no está activa", error.getMessage());
        assertSinCambios();
    }

    @Test
    void deberiaRechazarUnDepositoQueSupereElSaldoMaximo() {
        ExcepcionReglaNegocio error = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicio.registrarDeposito("00100001", 9_999_999_999.99, sesion));

        assertEquals("El saldo de la cuenta 00100001 superaría el máximo permitido", error.getMessage());
        assertSinCambios();
    }

    @Test
    void deberiaTransferirRegistrandoLosDosMovimientosEnUnaTransaccion() throws Exception {
        RespuestaTransaccion respuesta = servicio.registrarTransferencia("00100001", "00200001", 40.00, sesion);

        assertEquals(201, respuesta.getCodigo());
        assertEquals("Transferencia de $40.00 de la cuenta 00100001 a la cuenta 00200001 registrada correctamente",
                respuesta.getMensaje());
        assertEquals(new BigDecimal("500.00"), respuesta.getSaldo());
        assertEquals(5, respuesta.getNumeroMovimiento());
        assertEquals(new BigDecimal("570.25"), cuentas.cuentas.get("00200001").getSaldo());

        Movimiento salida = movimientos.registrados.get(0);
        Movimiento ingreso = movimientos.registrados.get(1);
        assertEquals(List.of(TipoMovimiento.TRANSFERENCIA_ENVIADA, "00100001", 5, "00200001"),
                List.of(salida.getCodigoTipo(), salida.getNumeroCuenta(), salida.getNumero(), salida.getCuentaReferencia()));
        assertEquals(List.of(TipoMovimiento.TRANSFERENCIA_RECIBIDA, "00200001", 4, "00100001"),
                List.of(ingreso.getCodigoTipo(), ingreso.getNumeroCuenta(), ingreso.getNumero(), ingreso.getCuentaReferencia()));
        assertEquals(1, fabrica.unidades.size());
        assertTransaccionConfirmada();
    }

    @Test
    void deberiaBloquearLasCuentasEnOrdenDeNumero() throws Exception {
        servicio.registrarTransferencia("00200001", "00100001", 10, sesion);

        assertEquals(List.of("00100001", "00200001"), cuentas.bloqueadas);
    }

    @Test
    void deberiaRechazarTransferenciasALaMismaCuenta() {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.registrarTransferencia("00100001", "00100001", 10, sesion));

        assertEquals(ServicioTransaccion.MENSAJE_CUENTAS_IGUALES, error.getMessage());
        assertTrue(fabrica.unidades.isEmpty());
    }

    @Test
    void deberiaRechazarTransferenciasEntreMonedasDistintas() {
        ExcepcionReglaNegocio error = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicio.registrarTransferencia("00100001", "00900001", 10, sesion));

        assertEquals(409, error.getCodigo());
        assertEquals(ServicioTransaccion.MENSAJE_MONEDAS_DISTINTAS, error.getMessage());
        assertSinCambios();
    }

    @Test
    void deberiaRechazarTransferenciasHaciaCuentasInexistentes() {
        ExcepcionNoEncontrado error = assertThrows(ExcepcionNoEncontrado.class,
                () -> servicio.registrarTransferencia("00100001", "00000009", 10, sesion));

        assertEquals("La cuenta 00000009 no existe", error.getMessage());
        assertSinCambios();
    }

    @Test
    void deberiaRechazarTransferenciasHaciaCuentasNoActivas() {
        ExcepcionReglaNegocio error = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicio.registrarTransferencia("00100001", "00100003", 10, sesion));

        assertEquals("La cuenta 00100003 no está activa", error.getMessage());
        assertSinCambios();
    }

    @Test
    void deberiaRechazarTransferenciasConSaldoInsuficiente() {
        ExcepcionReglaNegocio error = assertThrows(ExcepcionReglaNegocio.class,
                () -> servicio.registrarTransferencia("00200001", "00100001", 1000, sesion));

        assertEquals("Saldo insuficiente. Saldo disponible: $530.25", error.getMessage());
        assertSinCambios();
    }

    private void assertTransaccionConfirmada() {
        FabricaFalsa.UnidadFalsa unidad = fabrica.unidades.get(fabrica.unidades.size() - 1);
        assertTrue(unidad.transaccional);
        assertTrue(unidad.confirmada);
        assertTrue(unidad.cerrada);
    }

    // Sin confirmar, la unidad real revierte la transacción al cerrarse
    private void assertSinCambios() {
        assertTrue(cuentas.actualizadas.isEmpty());
        assertTrue(movimientos.registrados.isEmpty());
        for (FabricaFalsa.UnidadFalsa unidad : fabrica.unidades) {
            assertFalse(unidad.confirmada);
            assertTrue(unidad.cerrada);
        }
    }
}
