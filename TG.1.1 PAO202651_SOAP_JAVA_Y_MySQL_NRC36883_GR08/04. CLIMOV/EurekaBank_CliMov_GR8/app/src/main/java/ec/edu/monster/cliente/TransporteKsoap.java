package ec.edu.monster.cliente;

import java.io.IOException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Transporte real con ksoap2: arma el sobre SOAP 1.1 (document/literal, como lo publica Payara) y lo envía
 * por HTTP a la dirección configurada.
 */
public class TransporteKsoap implements Transporte {

    private static final int TIEMPO_ESPERA_MS = 10_000;

    private final String url;
    private final int tiempoEsperaMs;

    public TransporteKsoap(String url) {
        this(url, TIEMPO_ESPERA_MS);
    }

    public TransporteKsoap(String url, int tiempoEsperaMs) {
        this.url = url;
        this.tiempoEsperaMs = tiempoEsperaMs;
    }

    @Override
    public Object llamar(SoapObject peticion) throws IOException, XmlPullParserException {
        SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        // Parámetros sin xsi:type ni atributos extra: Metro los espera como elementos simples
        sobre.dotNet = false;
        sobre.implicitTypes = true;
        sobre.setAddAdornments(false);
        sobre.setOutputSoapObject(peticion);

        HttpTransportSE http = new HttpTransportSE(url, tiempoEsperaMs);
        // El WSDL declara soapAction="" para todas las operaciones
        http.call("", sobre);
        // getResponse lanza SoapFault si el servidor respondió con un error SOAP
        return sobre.getResponse();
    }
}
