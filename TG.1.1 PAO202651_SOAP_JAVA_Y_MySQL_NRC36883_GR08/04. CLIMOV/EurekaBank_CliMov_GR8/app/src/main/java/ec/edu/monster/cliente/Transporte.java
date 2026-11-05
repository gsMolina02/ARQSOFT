package ec.edu.monster.cliente;

import java.io.IOException;
import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Envía una operación al servicio SOAP y devuelve el contenido de su elemento &lt;return&gt;.
 * Las pruebas lo reemplazan por respuestas armadas a mano, sin red.
 */
public interface Transporte {

    Object llamar(SoapObject peticion) throws IOException, XmlPullParserException;
}
