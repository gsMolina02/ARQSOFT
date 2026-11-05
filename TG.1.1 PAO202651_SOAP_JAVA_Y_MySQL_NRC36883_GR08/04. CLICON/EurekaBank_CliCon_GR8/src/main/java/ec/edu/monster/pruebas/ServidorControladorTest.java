package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ProbadorConexion;
import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.ResultadoConexion;
import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.OrigenUrl;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

public class ServidorControladorTest {

    @TempDir
    Path carpeta;

    private String urlProbada;

    private ServidorControlador crear() {
        var configuracion = new ConfiguracionServidor(carpeta.resolve("servidor_soap.json"), () -> null, () -> null);
        var probadorFalso = new ProbadorConexion() {
            @Override
            public ResultadoConexion probar(String url) {
                urlProbada = url;
                return new ResultadoConexion(true, "Conexión correcta");
            }
        };
        return new ServidorControlador(configuracion, probadorFalso);
    }

    @Test
    void deberiaGuardarLaDireccionCompleta() {
        ServidorControlador controlador = crear();

        String url = controlador.guardar("192.168.1.25");

        assertEquals("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", url);
        assertEquals(url, controlador.getUrlActual());
        assertEquals(OrigenUrl.GUARDADA, controlador.getOrigen());
    }

    @Test
    void deberiaRestablecerALocalhost() {
        ServidorControlador controlador = crear();
        controlador.guardar("192.168.1.25");

        assertEquals(ConfiguracionServidor.URL_POR_DEFECTO, controlador.restablecer());
        assertEquals(OrigenUrl.POR_DEFECTO, controlador.getOrigen());
    }

    @Test
    void deberiaProbarLaDireccionCompleta() {
        ResultadoConexion resultado = crear().probar("192.168.1.25");

        assertTrue(resultado.exitosa());
        assertEquals("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", urlProbada);
    }

    @Test
    void deberiaRechazarUnaDireccionInvalidaSinLlamarAlServidor() {
        ServidorControlador controlador = crear();

        assertThrows(IllegalArgumentException.class, () -> controlador.probar("192.168.1"));
        assertThrows(IllegalArgumentException.class, () -> controlador.guardar(""));
        assertNull(urlProbada);
    }
}
