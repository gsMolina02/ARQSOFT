package ec.edu.monster.pruebas;

import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.OrigenUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionServidorTest {

    @ParameterizedTest
    @CsvSource({
        "192.168.1.25, http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank",
        "10.0.2.2, http://10.0.2.2:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank",
        "  Steven , http://steven:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank",
        "192.168.1.25:9090, http://192.168.1.25:9090/WS_EurekaBank_Java_SOAP/WSEurekaBank",
        "http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank?wsdl, http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank",
        "http://192.168.1.25:8080/OtraRuta/, http://192.168.1.25:8080/OtraRuta"
    })
    void deberiaCompletarLaDireccionDelServicio(String entrada, String esperada) {
        assertEquals(esperada, ConfiguracionServidor.completar(entrada));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "192.168.1", "300.1.1.1", "ftp://192.168.1.25", "192.168.1.25:abc"})
    void deberiaRechazarDireccionesInvalidas(String entrada) {
        assertThrows(IllegalArgumentException.class, () -> ConfiguracionServidor.completar(entrada));
    }

    @Test
    void deberiaUsarElEmuladorSiNoHayNadaGuardado() {
        ConfiguracionServidor configuracion = new ConfiguracionServidor(new AlmacenMemoria());

        assertEquals("http://10.0.2.2:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", configuracion.getUrlActual());
        assertEquals(OrigenUrl.POR_DEFECTO, configuracion.getOrigen());
    }

    @Test
    void deberiaGuardarYLeerLaDireccion() {
        AlmacenMemoria almacen = new AlmacenMemoria();
        ConfiguracionServidor configuracion = new ConfiguracionServidor(almacen);

        configuracion.guardar(" http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank ");

        assertEquals("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", almacen.url);
        assertEquals(almacen.url, configuracion.getUrlActual());
        assertEquals(OrigenUrl.GUARDADA, configuracion.getOrigen());
    }

    @Test
    void deberiaNoGuardarUnaDireccionInvalida() {
        AlmacenMemoria almacen = new AlmacenMemoria();
        ConfiguracionServidor configuracion = new ConfiguracionServidor(almacen);

        assertThrows(IllegalArgumentException.class, () -> configuracion.guardar("ftp://192.168.1.25"));
        assertNull(almacen.url);
    }

    @Test
    void deberiaIgnorarUnValorDaniado() {
        ConfiguracionServidor configuracion = new ConfiguracionServidor(new AlmacenMemoria("no es una dirección"));

        assertEquals(ConfiguracionServidor.URL_POR_DEFECTO, configuracion.getUrlActual());
        assertEquals(OrigenUrl.POR_DEFECTO, configuracion.getOrigen());
    }

    @Test
    void deberiaVolverAlEmuladorAlRestablecer() {
        AlmacenMemoria almacen = new AlmacenMemoria("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank");
        ConfiguracionServidor configuracion = new ConfiguracionServidor(almacen);

        configuracion.restablecer();

        assertNull(almacen.url);
        assertEquals(ConfiguracionServidor.URL_POR_DEFECTO, configuracion.getUrlActual());
    }
}
