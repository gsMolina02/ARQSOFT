package ec.edu.monster.pruebas;

import ec.edu.monster.excepciones.ExcepcionAutenticacion;
import ec.edu.monster.excepciones.ExcepcionNoEncontrado;
import ec.edu.monster.excepciones.ExcepcionReglaNegocio;
import ec.edu.monster.excepciones.ExcepcionValidacion;
import ec.edu.monster.modelos.dto.Respuesta;
import ec.edu.monster.modelos.dto.RespuestaTransaccion;
import ec.edu.monster.ws.ManejadorExcepciones;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManejadorExcepcionesTest {

    @Test
    void deberiaUsarElCodigoDeCadaExcepcionPropia() {
        assertEquals(400, ManejadorExcepciones.traducir(new ExcepcionValidacion("a")).getCodigo());
        assertEquals(401, ManejadorExcepciones.traducir(new ExcepcionAutenticacion("b")).getCodigo());
        assertEquals(404, ManejadorExcepciones.traducir(new ExcepcionNoEncontrado("c")).getCodigo());
        Respuesta conflicto = ManejadorExcepciones.traducir(new ExcepcionReglaNegocio("Saldo insuficiente"));
        assertEquals(409, conflicto.getCodigo());
        assertEquals("Saldo insuficiente", conflicto.getMensaje());
    }

    @Test
    void deberiaResponder500SinDetallesSiFallaLaBaseDeDatos() {
        Respuesta respuesta = ManejadorExcepciones.traducir(new SQLException("Communications link failure"));

        assertEquals(500, respuesta.getCodigo());
        assertEquals(ManejadorExcepciones.MENSAJE_BASE_DATOS, respuesta.getMensaje());
    }

    @Test
    void deberiaReconocerUnErrorDeBaseDeDatosEnvueltoEnOtraExcepcion() {
        Respuesta respuesta = ManejadorExcepciones.traducir(new RuntimeException(new SQLException("caída")));

        assertEquals(ManejadorExcepciones.MENSAJE_BASE_DATOS, respuesta.getMensaje());
    }

    @Test
    void deberiaOcultarLosErroresInesperados() {
        Respuesta respuesta = ManejadorExcepciones.traducir(new NullPointerException("detalle interno"));

        assertEquals(500, respuesta.getCodigo());
        assertEquals(ManejadorExcepciones.MENSAJE_INTERNO, respuesta.getMensaje());
    }

    @Test
    void deberiaDevolverLaRespuestaDeLaOperacionSiNoFalla() {
        RespuestaTransaccion esperada = new RespuestaTransaccion(201, "ok", BigDecimal.TEN, 3);

        assertSame(esperada, ManejadorExcepciones.atender("prueba", RespuestaTransaccion::new, () -> esperada));
    }

    @Test
    void deberiaDevolverElErrorEnUnaRespuestaDelTipoEsperado() {
        RespuestaTransaccion respuesta = ManejadorExcepciones.atender("prueba", RespuestaTransaccion::new, () -> {
            throw new ExcepcionNoEncontrado("La cuenta 99999999 no existe");
        });

        assertEquals(404, respuesta.getCodigo());
        assertEquals("La cuenta 99999999 no existe", respuesta.getMensaje());
        assertNull(respuesta.getSaldo());
    }
}
