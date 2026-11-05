package ec.edu.monster.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Direcciones con las que los clientes de otras computadoras de la red llegan a este servidor.
 * Se muestran al desplegar para saber qué IP escribir en "Configurar servidor" de cada cliente.
 */
public final class DireccionesRed {

    /** Puerto HTTP de Payara (http-listener-1), que escucha en todas las interfaces de red. */
    public static final int PUERTO = 8080;

    /** Nombre del servicio SOAP: forma parte de la URL después del contexto de la aplicación. */
    public static final String SERVICIO = "WSEurekaBank";

    private DireccionesRed() {
    }

    /** Palabras del nombre de los adaptadores que no sirven para que otra computadora se conecte. */
    private static final List<String> ADAPTADORES_VIRTUALES =
            List.of("virtual", "vmware", "hyper-v", "vethernet", "wsl", "docker", "vpn", "bluetooth");

    /**
     * IP de las tarjetas de red activas. Se prefieren las físicas (Wi-Fi, cable o el hotspot de un celular):
     * así no aparecen los adaptadores virtuales (VirtualBox, Hyper-V, VPN). Si solo hay virtuales, se muestran todas.
     */
    public static List<InetAddress> obtenerIPv4Locales() {
        List<InetAddress> fisicas = new ArrayList<>();
        List<InetAddress> todas = new ArrayList<>();
        try {
            for (NetworkInterface interfaz : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (interfaz.isUp() && !interfaz.isLoopback()) {
                    List<InetAddress> direcciones = Collections.list(interfaz.getInetAddresses());
                    todas.addAll(direcciones);
                    if (!interfaz.isVirtual() && !esAdaptadorVirtual(interfaz.getDisplayName())) {
                        fisicas.addAll(direcciones);
                    }
                }
            }
        } catch (SocketException ex) {
            // Sin permisos para listar las tarjetas: se devuelve lo que se haya encontrado
        }
        return fisicas.isEmpty() ? todas : fisicas;
    }

    /**
     * true para nombres como "VirtualBox Host-Only Ethernet Adapter" o "Hyper-V Virtual Ethernet Adapter".
     */
    public static boolean esAdaptadorVirtual(String nombre) {
        String texto = nombre == null ? "" : nombre.toLowerCase();
        return ADAPTADORES_VIRTUALES.stream().anyMatch(texto::contains);
    }

    /**
     * Arma la URL del WSDL para cada IPv4 de la red. Se descartan IPv6, loopback (127.x)
     * y las direcciones automáticas 169.254.x, que no sirven para conectarse desde otra computadora.
     */
    public static List<String> crearUrlsServicio(Collection<InetAddress> direcciones, String contexto) {
        Set<String> urls = new LinkedHashSet<>();
        for (InetAddress direccion : direcciones) {
            if (esUtilEnLaRed(direccion)) {
                urls.add(urlServicio(direccion.getHostAddress(), contexto));
            }
        }
        return new ArrayList<>(urls);
    }

    /**
     * http://IP:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank?wsdl
     */
    public static String urlServicio(String host, String contexto) {
        return "http://" + host + ":" + PUERTO + contexto + "/" + SERVICIO + "?wsdl";
    }

    /**
     * IP del cliente que hizo la petición, tal como se guarda en la sesión
     * (::ffff:192.168.1.30 → 192.168.1.30, 0:0:0:0:0:0:0:1 → ::1).
     */
    public static String ipCliente(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            return "desconocida";
        }
        String texto = direccion.trim();
        if (texto.toLowerCase().startsWith("::ffff:") && texto.indexOf('.') > 0) {
            return texto.substring("::ffff:".length());
        }
        if (texto.equals("0:0:0:0:0:0:0:1")) {
            return "::1";
        }
        return texto;
    }

    private static boolean esUtilEnLaRed(InetAddress direccion) {
        if (!(direccion instanceof Inet4Address) || direccion.isLoopbackAddress()) {
            return false;
        }
        return !direccion.isLinkLocalAddress();
    }
}
