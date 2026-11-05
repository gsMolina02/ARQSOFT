package ec.edu.monster.pruebas;

import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.OrigenUrl;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracionServidorTest {

    @TempDir
    Path carpeta;

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
    void deberiaUsarLocalhostSiNoHayNadaGuardado() {
        ConfiguracionServidor configuracion = new ConfiguracionServidor(carpeta.resolve("s.json"), () -> null, () -> null);

        assertEquals(ConfiguracionServidor.URL_POR_DEFECTO, configuracion.getUrlActual());
        assertEquals(OrigenUrl.POR_DEFECTO, configuracion.getOrigen());
    }

    @Test
    void deberiaGuardarYLeerLaDireccion() throws Exception {
        Path archivo = carpeta.resolve("EurekaBank_GR8").resolve("servidor_soap.json");
        ConfiguracionServidor configuracion = new ConfiguracionServidor(archivo, () -> null, () -> null);

        configuracion.guardar("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank");

        assertTrue(Files.readString(archivo).contains("\"Url\""));
        assertEquals(OrigenUrl.GUARDADA, configuracion.getOrigen());
        configuracion.restablecer();
        assertEquals(OrigenUrl.POR_DEFECTO, configuracion.getOrigen());
    }

    @Test
    void deberiaDarPrioridadALaVariableDeEntorno() {
        ConfiguracionServidor configuracion = new ConfiguracionServidor(carpeta.resolve("s.json"),
                () -> null, () -> "http://10.0.0.5:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank");
        configuracion.guardar("http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank");

        assertEquals("http://10.0.0.5:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank", configuracion.getUrlActual());
        assertEquals(OrigenUrl.VARIABLE_ENTORNO, configuracion.getOrigen());
    }

    @Test
    void deberiaIgnorarUnArchivoDaniado() throws Exception {
        Path archivo = carpeta.resolve("s.json");
        Files.writeString(archivo, "{esto no es json");

        assertEquals(ConfiguracionServidor.URL_POR_DEFECTO,
                new ConfiguracionServidor(archivo, () -> null, () -> null).getUrlActual());
    }
}
