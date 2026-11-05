package ec.edu.monster.pruebas;

import ec.edu.monster.excepciones.ExcepcionNoEncontrado;
import ec.edu.monster.excepciones.ExcepcionValidacion;
import ec.edu.monster.modelos.Movimiento;
import ec.edu.monster.modelos.TipoMovimiento;
import ec.edu.monster.modelos.dto.CuentaDTO;
import ec.edu.monster.modelos.dto.MovimientoDTO;
import ec.edu.monster.modelos.dto.RespuestaCuentas;
import ec.edu.monster.modelos.dto.RespuestaMovimientos;
import ec.edu.monster.pruebas.falsos.CuentaDAOFalso;
import ec.edu.monster.pruebas.falsos.DatosPrueba;
import ec.edu.monster.pruebas.falsos.FabricaFalsa;
import ec.edu.monster.pruebas.falsos.MovimientoDAOFalso;
import ec.edu.monster.servicios.ServicioCuenta;
import ec.edu.monster.servicios.ValidadorDatos;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioCuentaTest {

    private final FabricaFalsa fabrica = new FabricaFalsa();
    private final CuentaDAOFalso cuentas = new CuentaDAOFalso();
    private final MovimientoDAOFalso movimientos = new MovimientoDAOFalso();
    private final ServicioCuenta servicio = new ServicioCuenta(fabrica, cuentas, movimientos);

    @BeforeEach
    void prepararDatos() {
        cuentas.con(DatosPrueba.cuenta("00100001", "565.5", 2),
                DatosPrueba.cuenta("00100003", "0", "ANULADO", "USD", 0));
        // Se agregan desordenados: el servicio los devuelve por número
        movimientos.existentes.add(movimiento(2, TipoMovimiento.TRANSFERENCIA_ENVIADA, "SALIDA", "65.50", "00200001"));
        movimientos.existentes.add(movimiento(1, TipoMovimiento.APERTURA, "INGRESO", "631.00", null));
    }

    @Test
    void deberiaRetornarLaCuentaYSusMovimientosOrdenados() throws Exception {
        RespuestaMovimientos respuesta = servicio.consultarMovimientos(" 00100001 ");

        assertEquals(200, respuesta.getCodigo());
        assertEquals("La cuenta registra 2 movimiento(s)", respuesta.getMensaje());

        CuentaDTO cuenta = respuesta.getCuenta();
        assertEquals("00100001", cuenta.getNumero());
        assertEquals("Carlos Andrés Pérez Salazar", cuenta.getCliente());
        assertEquals("1712345675", cuenta.getCedula());
        assertEquals("USD", cuenta.getMoneda());
        assertEquals("$", cuenta.getSimboloMoneda());
        assertEquals("Matriz Quito", cuenta.getSucursal());
        assertEquals("ACTIVO", cuenta.getEstado());
        assertEquals(new BigDecimal("565.50"), cuenta.getSaldo());
        assertEquals("2026-01-15", cuenta.getFechaApertura());
        assertEquals(2, cuenta.getContadorMovimientos());

        assertEquals(2, respuesta.getMovimientos().size());
        MovimientoDTO primero = respuesta.getMovimientos().get(0);
        MovimientoDTO segundo = respuesta.getMovimientos().get(1);
        assertEquals(1, primero.getNumero());
        assertEquals("2026-11-04 09:15:07", primero.getFechaHora());
        assertNull(primero.getCuentaReferencia());
        assertEquals(2, segundo.getNumero());
        assertEquals("009", segundo.getCodigoTipo());
        assertEquals("Transferencia", segundo.getTipo());
        assertEquals("SALIDA", segundo.getAccion());
        assertEquals(new BigDecimal("65.50"), segundo.getImporte());
        assertEquals("00200001", segundo.getCuentaReferencia());
        assertEquals("0001", segundo.getEmpleado());
    }

    @Test
    void deberiaPermitirConsultarCuentasNoActivasSinMovimientos() throws Exception {
        RespuestaMovimientos respuesta = servicio.consultarMovimientos("00100003");

        assertEquals(200, respuesta.getCodigo());
        assertEquals("La cuenta no registra movimientos", respuesta.getMensaje());
        assertEquals("ANULADO", respuesta.getCuenta().getEstado());
        assertTrue(respuesta.getMovimientos().isEmpty());
    }

    @Test
    void deberiaResponder404SiLaCuentaNoExiste() {
        ExcepcionNoEncontrado error = assertThrows(ExcepcionNoEncontrado.class,
                () -> servicio.consultarMovimientos("99999999"));

        assertEquals(404, error.getCodigo());
        assertEquals("La cuenta 99999999 no existe", error.getMessage());
    }

    @Test
    void deberiaValidarElFormatoDeLaCuenta() {
        ExcepcionValidacion error = assertThrows(ExcepcionValidacion.class,
                () -> servicio.consultarMovimientos("1234"));

        assertEquals(ValidadorDatos.MENSAJE_CUENTA, error.getMessage());
        assertTrue(fabrica.unidades.isEmpty());
    }

    @Test
    void deberiaListarTodasLasCuentasOrdenadasConSuEstado() throws Exception {
        cuentas.con(DatosPrueba.cuenta("00200001", "10", 1));

        RespuestaCuentas respuesta = servicio.listarCuentas();

        assertEquals(200, respuesta.getCodigo());
        assertEquals("Se encontraron 3 cuenta(s)", respuesta.getMensaje());
        assertEquals(List.of("00100001", "00100003", "00200001"),
                respuesta.getCuentas().stream().map(CuentaDTO::getNumero).toList());
        assertEquals("ANULADO", respuesta.getCuentas().get(1).getEstado());
        assertEquals(1, fabrica.unidades.size());
    }

    private static Movimiento movimiento(int numero, String tipo, String accion, String importe, String referencia) {
        Movimiento movimiento = new Movimiento();
        movimiento.setNumeroCuenta("00100001");
        movimiento.setNumero(numero);
        movimiento.setCodigoTipo(tipo);
        movimiento.setDescripcionTipo(TipoMovimiento.APERTURA.equals(tipo) ? "Apertura de cuenta" : "Transferencia");
        movimiento.setAccion(accion);
        movimiento.setCodigoEmpleado("0001");
        movimiento.setCuentaReferencia(referencia);
        movimiento.setFechaHora(LocalDateTime.of(2026, 11, 4, 9, 15, 7));
        movimiento.setImporte(new BigDecimal(importe));
        return movimiento;
    }
}
