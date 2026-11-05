package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ProbadorConexion;
import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.ResultadoConexion;
import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.OrigenUrl;
import org.junit.jupiter.api.Test;

import static ec.edu.monster.pruebas.TransporteFalso.respuesta;
import static org.junit.jupiter.api.Assertions.*;

public class ServidorControladorTest {

    private final AlmacenMemoria almacen = new AlmacenMemoria();
    private final TransporteFalso transporte = new TransporteFalso();
    private String urlProbada;
    private final ServidorControlador controlador = new ServidorControlador(new ConfiguracionServidor(almacen),
            new ProbadorConexion(url -> {
                urlProbada = url;
                return transporte;
            }));

    @Test
    void deberiaGuardarLaDireccionCompleta() {
        String url = controlador.guardar("192.168.1.25");

        assertEquals("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", url);
        assertEquals(url, almacen.url);
        assertEquals(url, controlador.getUrlActual());
        assertEquals(OrigenUrl.GUARDADA, controlador.getOrigen());
    }

    @Test
    void deberiaRestablecerAlEmulador() {
        controlador.guardar("192.168.1.25");

        assertEquals(ConfiguracionServidor.URL_POR_DEFECTO, controlador.restablecer());
        assertEquals(OrigenUrl.POR_DEFECTO, controlador.getOrigen());
    }

    @Test
    void deberiaProbarLaDireccionCompleta() {
        transporte.responder(respuesta("codigo", "401", "mensaje", "Acceso denegado"));

        ResultadoConexion resultado = controlador.probar("10.0.2.2");

        assertTrue(resultado.exitosa());
        assertEquals("http://10.0.2.2:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", urlProbada);
    }

    @Test
    void deberiaRechazarUnaDireccionInvalidaSinLlamarAlServidor() {
        assertThrows(IllegalArgumentException.class, () -> controlador.probar("192.168.1"));
        assertThrows(IllegalArgumentException.class, () -> controlador.guardar("300.1.1.1"));

        assertTrue(transporte.peticiones.isEmpty());
        assertNull(almacen.url);
    }
}
