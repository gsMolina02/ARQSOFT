package ec.edu.monster.cliente;

import ec.edu.monster.modelo.ResultadoConexion;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Comprueba que en una dirección responda el servicio SOAP WSEurekaBank antes de guardarla.
 * Envía consultarMovimientos sin token: el servicio responde con &lt;codigo&gt;401&lt;/codigo&gt;, lo que confirma
 * que está activo sin tocar ninguna cuenta. Distingue un servidor apagado o bloqueado por el firewall
 * de una ruta equivocada (404).
 */
public class ProbadorConexion {

    private static final Duration TIEMPO_ESPERA = Duration.ofSeconds(5);

    static final String SOBRE_PRUEBA = "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" "
            + "xmlns:ws=\"http://ws.monster.edu.ec/\"><s:Body><ws:consultarMovimientos>"
            + "<cuenta>00000000</cuenta></ws:consultarMovimientos></s:Body></s:Envelope>";

    private final HttpClient clienteHttp = HttpClient.newBuilder()
            .connectTimeout(TIEMPO_ESPERA)
            .build();

    public ResultadoConexion probar(String urlServicio) {
        String url = urlServicio.trim();
        int consulta = url.indexOf('?');
        if (consulta >= 0) {
            url = url.substring(0, consulta);
        }

        HttpResponse<String> respuesta;
        try {
            HttpRequest peticion = HttpRequest.newBuilder(URI.create(url))
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .header("SOAPAction", "\"\"")
                    .timeout(TIEMPO_ESPERA)
                    .POST(HttpRequest.BodyPublishers.ofString(SOBRE_PRUEBA, StandardCharsets.UTF_8))
                    .build();
            respuesta = clienteHttp.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (HttpTimeoutException ex) {
            return new ResultadoConexion(false, "El servidor en " + url + " no respondió en "
                    + TIEMPO_ESPERA.toSeconds() + " segundos. Verifique la IP, que ambas computadoras estén "
                    + "en la misma red y que el firewall permita el puerto 8080.");
        } catch (IOException | IllegalArgumentException ex) {
            return new ResultadoConexion(false, "No fue posible conectar con " + url
                    + ". Verifique que Payara esté encendido con el servicio desplegado "
                    + "(PREPARAR_SERVIDOR_SOAP_8080.bat), la IP y el puerto, y el firewall del servidor.");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return new ResultadoConexion(false, "La prueba de conexión fue interrumpida.");
        }

        int codigo = respuesta.statusCode();
        String cuerpo = respuesta.body() == null ? "" : respuesta.body();
        if (codigo == 200 && cuerpo.contains("consultarMovimientosResponse") && cuerpo.contains("<codigo>401</codigo>")) {
            return new ResultadoConexion(true, "Conexión correcta con el servidor SOAP EurekaBank en " + url + ".");
        }
        if (codigo == 404) {
            return new ResultadoConexion(false, "El equipo en " + url + " respondió con el código 404. "
                    + "Revise que la dirección termine en /WS_EurekaBank_Java_SOAP/WSEurekaBank.");
        }
        return new ResultadoConexion(false, "La dirección " + url + " responde (código " + codigo
                + "), pero no es el servicio SOAP WSEurekaBank.");
    }
}
