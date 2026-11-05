package ec.edu.monster.pruebas;

import com.sun.net.httpserver.HttpServer;
import ec.edu.monster.cliente.ProbadorConexion;
import ec.edu.monster.modelo.ResultadoConexion;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba el probador contra un servidor HTTP local que imita las respuestas de Payara.
 */
public class ProbadorConexionTest {

    private HttpServer servidor;

    private String iniciar(int codigo, String cuerpo) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/WS_EurekaBank_Java_SOAP/WSEurekaBank", intercambio -> {
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "text/xml; charset=utf-8");
            intercambio.sendResponseHeaders(codigo, bytes.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(bytes);
            }
        });
        servidor.start();
        return "http://127.0.0.1:" + servidor.getAddress().getPort() + "/WS_EurekaBank_Java_SOAP/WSEurekaBank";
    }

    @AfterEach
    void detener() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void deberiaReconocerAlServicioPorSuRespuesta401() throws Exception {
        String url = iniciar(200, "<S:Envelope><S:Body><ns2:consultarMovimientosResponse><return>"
                + "<codigo>401</codigo><mensaje>Acceso denegado</mensaje></return>"
                + "</ns2:consultarMovimientosResponse></S:Body></S:Envelope>");

        ResultadoConexion resultado = new ProbadorConexion().probar(url + "?wsdl");

        assertTrue(resultado.exitosa(), resultado.mensaje());
    }

    @Test
    void deberiaAvisarDeUnaRutaEquivocada() throws Exception {
        String url = iniciar(404, "No encontrado");

        ResultadoConexion resultado = new ProbadorConexion().probar(url);

        assertFalse(resultado.exitosa());
        assertTrue(resultado.mensaje().contains("404"));
    }

    @Test
    void deberiaRechazarOtroServicioQueResponde() throws Exception {
        String url = iniciar(200, "<html>Otra aplicación</html>");

        assertFalse(new ProbadorConexion().probar(url).exitosa());
    }

    @Test
    void deberiaInformarQueNoHayServidor() {
        ResultadoConexion resultado = new ProbadorConexion().probar("http://127.0.0.1:1/WS_EurekaBank_Java_SOAP/WSEurekaBank");

        assertFalse(resultado.exitosa());
        assertTrue(resultado.mensaje().startsWith("No fue posible conectar"));
    }
}
