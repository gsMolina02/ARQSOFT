package ec.edu.monster.pruebas;

import ec.edu.monster.util.DireccionesRed;
import java.net.InetAddress;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class DireccionesRedTest {

    private static final String CONTEXTO = "/WS_EurekaBank_Java_SOAP";

    @Test
    void deberiaArmarLaUrlDelWsdlPorCadaIPv4DeLaRed() throws Exception {
        List<String> urls = DireccionesRed.crearUrlsServicio(
                List.of(InetAddress.getByName("192.168.1.30"), InetAddress.getByName("10.0.0.5")), CONTEXTO);

        assertEquals(List.of(
                "http://192.168.1.30:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank?wsdl",
                "http://10.0.0.5:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank?wsdl"), urls);
    }

    @ParameterizedTest
    @ValueSource(strings = {"127.0.0.1", "169.254.10.20", "::1", "fe80::1"})
    void deberiaDescartarDireccionesQueNoSirvenDesdeOtraComputadora(String direccion) throws Exception {
        assertTrue(DireccionesRed.crearUrlsServicio(List.of(InetAddress.getByName(direccion)), CONTEXTO).isEmpty());
    }

    @Test
    void deberiaNoRepetirDirecciones() throws Exception {
        InetAddress ip = InetAddress.getByName("192.168.1.30");

        assertEquals(1, DireccionesRed.crearUrlsServicio(List.of(ip, ip), CONTEXTO).size());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "::ffff:192.168.1.30, 192.168.1.30",
        "192.168.1.30, 192.168.1.30",
        "0:0:0:0:0:0:0:1, ::1",
        "NULO, desconocida",
        "'  ', desconocida"
    }, nullValues = "NULO")
    void deberiaGuardarLaIpDelClienteEnFormatoLegible(String direccion, String esperada) {
        assertEquals(esperada, DireccionesRed.ipCliente(direccion));
    }

    @ParameterizedTest
    @CsvSource({
        "VirtualBox Host-Only Ethernet Adapter, true",
        "Hyper-V Virtual Ethernet Adapter, true",
        "Check Point Virtual Network Adapter For Endpoint VPN Client, true",
        "Realtek 8852CE WiFi 6E PCI-E NIC, false",
        "Realtek PCIe GbE Family Controller, false"
    })
    void deberiaReconocerLosAdaptadoresVirtualesPorSuNombre(String nombre, boolean esperado) {
        assertEquals(esperado, DireccionesRed.esAdaptadorVirtual(nombre));
    }

    @Test
    void deberiaListarLasIPDeEstaComputadoraSinFallar() {
        assertDoesNotThrow(DireccionesRed::obtenerIPv4Locales);
    }
}
