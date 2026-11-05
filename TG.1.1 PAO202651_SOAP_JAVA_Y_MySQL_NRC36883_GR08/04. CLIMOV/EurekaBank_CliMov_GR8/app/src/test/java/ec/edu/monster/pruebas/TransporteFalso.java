package ec.edu.monster.pruebas;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.Transporte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Transporte de prueba: devuelve respuestas armadas a mano como las entrega ksoap2 (cada valor simple
 * es un SoapPrimitive) y guarda las peticiones recibidas, sin red ni servidor.
 */
class TransporteFalso implements Transporte {

    final List<SoapObject> peticiones = new ArrayList<>();
    private final List<Object> respuestas = new ArrayList<>();
    private IOException error;

    /** Respuesta del elemento &lt;return&gt;: pares nombre, valor. */
    static SoapObject respuesta(String... nombresYValores) {
        SoapObject objeto = new SoapObject("", "return");
        agregar(objeto, nombresYValores);
        return objeto;
    }

    static SoapObject objeto(String nombre, String... nombresYValores) {
        SoapObject objeto = new SoapObject("", nombre);
        agregar(objeto, nombresYValores);
        return objeto;
    }

    private static void agregar(SoapObject objeto, String... nombresYValores) {
        for (int i = 0; i < nombresYValores.length; i += 2) {
            objeto.addProperty(nombresYValores[i], new SoapPrimitive("", nombresYValores[i], nombresYValores[i + 1]));
        }
    }

    TransporteFalso responder(Object respuesta) {
        respuestas.add(respuesta);
        return this;
    }

    TransporteFalso fallar(IOException error) {
        this.error = error;
        return this;
    }

    SoapObject ultimaPeticion() {
        return peticiones.get(peticiones.size() - 1);
    }

    static String parametro(SoapObject peticion, String nombre) {
        return peticion.hasProperty(nombre) ? String.valueOf(peticion.getProperty(nombre)) : null;
    }

    @Override
    public Object llamar(SoapObject peticion) throws IOException, XmlPullParserException {
        peticiones.add(peticion);
        if (error != null) {
            throw error;
        }
        if (!ClienteWSEurekaBank.ESPACIO_NOMBRES.equals(peticion.getNamespace())) {
            throw new IllegalStateException("La petición no usa el espacio de nombres del servicio");
        }
        return respuestas.isEmpty() ? null : respuestas.remove(0);
    }
}
