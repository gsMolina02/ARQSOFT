package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ProbadorConexion;
import ec.edu.monster.modelo.ResultadoConexion;
import java.io.IOException;
import java.net.SocketTimeoutException;
import org.junit.jupiter.api.Test;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpResponseException;
import org.xmlpull.v1.XmlPullParserException;

import static ec.edu.monster.pruebas.TransporteFalso.parametro;
import static ec.edu.monster.pruebas.TransporteFalso.respuesta;
import static org.junit.jupiter.api.Assertions.*;

public class ProbadorConexionTest {

    private static final String URL = "http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private final TransporteFalso transporte = new TransporteFalso();
    private String urlUsada;
    private final ProbadorConexion probador = new ProbadorConexion(url -> {
        urlUsada = url;
        return transporte;
    });

    @Test
    void deberiaReconocerAlServicioPorSuRespuesta401() {
        transporte.responder(respuesta("codigo", "401", "mensaje", "Acceso denegado: no se encontró el token. "
                + "Inicie sesión primero."));

        ResultadoConexion resultado = probador.probar(URL + "?wsdl");

        assertTrue(resultado.exitosa());
        // Se prueba sin "?wsdl" y sin token, con una cuenta que no se toca
        assertEquals(URL, urlUsada);
        SoapObject peticion = transporte.ultimaPeticion();
        assertEquals("consultarMovimientos", peticion.getName());
        assertNull(parametro(peticion, "token"));
        assertEquals("00000000", parametro(peticion, "cuenta"));
    }

    @Test
    void deberiaAvisarDeUnaRutaEquivocada() {
        transporte.fallar(new HttpResponseException("Not Found", 404));

        ResultadoConexion resultado = probador.probar("http://192.168.1.25:8080/OtraRuta");

        assertFalse(resultado.exitosa());
        assertTrue(resultado.mensaje().contains("404"));
        assertTrue(resultado.mensaje().contains("/WS_EurekaBank_Java_SOAP/WSEurekaBank"));
    }

    @Test
    void deberiaRechazarOtroServicioQueResponde() {
        transporte.responder(respuesta("codigo", "200", "mensaje", "Hola"));

        ResultadoConexion resultado = probador.probar(URL);

        assertFalse(resultado.exitosa());
        assertTrue(resultado.mensaje().contains("no es el servicio SOAP WSEurekaBank"));
    }

    @Test
    void deberiaRechazarUnaPaginaQueNoEsSoap() {
        // Una página HTML (por ejemplo la de bienvenida de Payara) no se puede leer como sobre SOAP
        ResultadoConexion conHtml = new ProbadorConexion(url -> peticion -> {
            throw new XmlPullParserException("unexpected type");
        }).probar(URL);

        assertFalse(conHtml.exitosa());
        assertTrue(conHtml.mensaje().contains("no es el servicio SOAP WSEurekaBank"));
    }

    @Test
    void deberiaAvisarSiElServidorNoRespondeATiempo() {
        transporte.fallar(new SocketTimeoutException("connect timed out"));

        ResultadoConexion resultado = probador.probar(URL);

        assertFalse(resultado.exitosa());
        assertTrue(resultado.mensaje().contains("no respondió"));
        assertTrue(resultado.mensaje().contains("8080"));
    }

    @Test
    void deberiaInformarQueNoHayServidor() {
        transporte.fallar(new IOException("Connection refused"));

        ResultadoConexion resultado = probador.probar(URL);

        assertFalse(resultado.exitosa());
        assertTrue(resultado.mensaje().startsWith("No fue posible conectar con " + URL));
    }
}
