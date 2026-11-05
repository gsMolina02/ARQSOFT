package ec.edu.monster.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dirección del servicio SOAP WSEurekaBank: la guardada por el usuario en el celular o, si no hay,
 * la del emulador (10.0.2.2 es la PC donde corre el emulador de Android).
 */
public class ConfiguracionServidor {

    public static final String URL_POR_DEFECTO = "http://10.0.2.2:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private static final int PUERTO_POR_DEFECTO = 8080;
    private static final String RUTA_SERVICIO = "/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private static final String MENSAJE_FORMATO =
            "Use la IP de la computadora servidor (por ejemplo 192.168.1.25) "
            + "o la dirección completa (http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank).";

    private static final Pattern PATRON_NUMERICO = Pattern.compile("[0-9.]+");
    private static final Pattern PATRON_IPV4 =
            Pattern.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})");

    /**
     * Dónde se guarda la dirección (SharedPreferences en el celular; memoria en las pruebas).
     */
    public interface Almacen {

        String leer();

        void guardar(String url);

        void borrar();
    }

    private final Almacen almacen;

    public ConfiguracionServidor(Almacen almacen) {
        this.almacen = almacen;
    }

    public OrigenUrl getOrigen() {
        return leerGuardada() == null ? OrigenUrl.POR_DEFECTO : OrigenUrl.GUARDADA;
    }

    public String getUrlActual() {
        String guardada = leerGuardada();
        return guardada != null ? guardada : URL_POR_DEFECTO;
    }

    public void guardar(String url) {
        if (!esUrlValida(url)) {
            throw new IllegalArgumentException("La dirección del servidor no es válida. " + MENSAJE_FORMATO);
        }
        almacen.guardar(url.trim());
    }

    public void restablecer() {
        almacen.borrar();
    }

    /**
     * Convierte lo que escribe el usuario en la dirección del servicio. Con solo la IP o el nombre del
     * equipo se completan el puerto 8080 y la ruta /WS_EurekaBank_Java_SOAP/WSEurekaBank
     * (192.168.1.25 → http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank). Una dirección con
     * ruta se respeta y se le quita "?wsdl" si se pegó la dirección del contrato.
     */
    public static String completar(String entrada) {
        String texto = entrada == null ? "" : entrada.trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Escriba la dirección del servidor. " + MENSAJE_FORMATO);
        }
        if (!texto.contains("://")) {
            texto = "http://" + texto;
        }

        String autoridad = extraerAutoridad(texto);
        int dosPuntos = autoridad.lastIndexOf(':');
        boolean tienePuerto = dosPuntos >= 0 && !autoridad.endsWith("]");
        String hostEscrito = tienePuerto ? autoridad.substring(0, dosPuntos) : autoridad;

        URI uri;
        try {
            uri = new URI(texto);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("La dirección del servidor no es válida. " + MENSAJE_FORMATO);
        }
        if (!esEsquemaHttp(uri.getScheme()) || uri.getHost() == null || hostEscrito.isEmpty()
                || !esHostValido(hostEscrito) || (tienePuerto && uri.getPort() < 0)) {
            throw new IllegalArgumentException("La dirección del servidor no es válida. " + MENSAJE_FORMATO);
        }

        String ruta = uri.getRawPath() == null ? "" : uri.getRawPath();
        boolean sinRuta = ruta.isEmpty() || ruta.equals("/");
        int puerto = uri.getPort() < 0 && sinRuta ? PUERTO_POR_DEFECTO : uri.getPort();

        String resultado = uri.getScheme().toLowerCase(Locale.ROOT) + "://" + uri.getHost().toLowerCase(Locale.ROOT)
                + (puerto >= 0 ? ":" + puerto : "")
                + (sinRuta ? RUTA_SERVICIO : ruta);
        while (resultado.endsWith("/")) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        return resultado;
    }

    public static boolean esUrlValida(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        try {
            URI uri = new URI(url.trim());
            return esEsquemaHttp(uri.getScheme()) && uri.getHost() != null && !uri.getHost().isEmpty();
        } catch (URISyntaxException ex) {
            return false;
        }
    }

    private static boolean esEsquemaHttp(String esquema) {
        return "http".equalsIgnoreCase(esquema) || "https".equalsIgnoreCase(esquema);
    }

    // Se exigen los cuatro números de 0 a 255 (192.168.1 no es una IP completa)
    private static boolean esHostValido(String host) {
        if (!PATRON_NUMERICO.matcher(host).matches()) {
            return true;
        }
        Matcher coincidencia = PATRON_IPV4.matcher(host);
        if (!coincidencia.matches()) {
            return false;
        }
        for (int grupo = 1; grupo <= 4; grupo++) {
            if (Integer.parseInt(coincidencia.group(grupo)) > 255) {
                return false;
            }
        }
        return true;
    }

    // Host y puerto tal como los escribió el usuario
    private static String extraerAutoridad(String texto) {
        String resto = texto.substring(texto.indexOf("://") + 3);
        int fin = resto.length();
        for (char separador : new char[] {'/', '?', '#'}) {
            int posicion = resto.indexOf(separador);
            if (posicion >= 0 && posicion < fin) {
                fin = posicion;
            }
        }
        String autoridad = resto.substring(0, fin);
        return autoridad.substring(autoridad.lastIndexOf('@') + 1);
    }

    private String leerGuardada() {
        String guardada = almacen.leer();
        return esUrlValida(guardada) ? guardada.trim() : null;
    }
}
