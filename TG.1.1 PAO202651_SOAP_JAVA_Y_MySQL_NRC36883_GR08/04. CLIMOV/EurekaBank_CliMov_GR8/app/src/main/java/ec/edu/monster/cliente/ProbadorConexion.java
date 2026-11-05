package ec.edu.monster.cliente;

import ec.edu.monster.modelo.ResultadoConexion;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.function.Function;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.transport.HttpResponseException;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Comprueba que en una dirección responda el servicio SOAP WSEurekaBank antes de guardarla.
 * Envía consultarMovimientos sin token: el servicio responde con codigo 401, lo que confirma que está activo
 * sin tocar ninguna cuenta. Distingue un servidor apagado o bloqueado por el firewall de una ruta equivocada (404).
 * Hace una llamada de red: la vista lo usa fuera del hilo principal.
 */
public class ProbadorConexion {

    private static final int TIEMPO_ESPERA_MS = 5_000;

    private final Function<String, Transporte> crearTransporte;

    public ProbadorConexion() {
        this(url -> new TransporteKsoap(url, TIEMPO_ESPERA_MS));
    }

    // El transporte se puede reemplazar en las pruebas para no depender del servidor
    public ProbadorConexion(Function<String, Transporte> crearTransporte) {
        this.crearTransporte = crearTransporte;
    }

    public ResultadoConexion probar(String urlServicio) {
        String url = urlServicio.trim();
        int consulta = url.indexOf('?');
        if (consulta >= 0) {
            url = url.substring(0, consulta);
        }

        SoapObject peticion = new SoapObject(ClienteWSEurekaBank.ESPACIO_NOMBRES, "consultarMovimientos");
        peticion.addProperty("cuenta", "00000000");
        try {
            Object respuesta = crearTransporte.apply(url).llamar(peticion);
            if (respuesta instanceof SoapObject objeto && "401".equals(ClienteWSEurekaBank.texto(objeto, "codigo"))) {
                return new ResultadoConexion(true, "Conexión correcta con el servidor SOAP EurekaBank en " + url + ".");
            }
            return new ResultadoConexion(false, "La dirección " + url + " responde, pero no es el servicio SOAP WSEurekaBank.");
        } catch (HttpResponseException ex) {
            if (ex.getStatusCode() == 404) {
                return new ResultadoConexion(false, "El equipo en " + url + " respondió con el código 404. "
                        + "Revise que la dirección termine en /WS_EurekaBank_Java_SOAP/WSEurekaBank.");
            }
            return new ResultadoConexion(false, "La dirección " + url + " responde (código " + ex.getStatusCode()
                    + "), pero no es el servicio SOAP WSEurekaBank.");
        } catch (SocketTimeoutException ex) {
            return new ResultadoConexion(false, "El servidor en " + url + " no respondió en " + TIEMPO_ESPERA_MS / 1000
                    + " segundos. Verifique la IP, que el celular y la PC estén en la misma red y que el firewall "
                    + "permita el puerto 8080.");
        } catch (XmlPullParserException ex) {
            return new ResultadoConexion(false, "La dirección " + url + " responde, pero no es el servicio SOAP WSEurekaBank.");
        } catch (IOException | IllegalArgumentException ex) {
            return new ResultadoConexion(false, "No fue posible conectar con " + url
                    + ". Verifique que Payara esté encendido con el servicio desplegado, la IP y el puerto, "
                    + "y el firewall del servidor.");
        }
    }
}
