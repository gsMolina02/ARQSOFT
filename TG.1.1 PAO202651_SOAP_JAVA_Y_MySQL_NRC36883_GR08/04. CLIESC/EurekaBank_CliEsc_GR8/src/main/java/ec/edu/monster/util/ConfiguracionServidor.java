package ec.edu.monster.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dirección del servicio SOAP WSEurekaBank. Se toma, en este orden, de la propiedad -Dws.base.url,
 * de la variable de entorno WS_BASE_URL, de la dirección guardada por el usuario
 * (%APPDATA%\EurekaBank_GR8\servidor_soap.json) o de localhost. Así el cliente puede usar el servidor
 * Payara de otra computadora de la red.
 */
public class ConfiguracionServidor {

    public static final String PROPIEDAD_BASE_WS = "ws.base.url";
    public static final String VARIABLE_BASE_WS = "WS_BASE_URL";
    public static final String URL_POR_DEFECTO = "http://localhost:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private static final int PUERTO_POR_DEFECTO = 8080;
    private static final String RUTA_SERVICIO = "/WS_EurekaBank_Java_SOAP/WSEurekaBank";

    private static final String MENSAJE_FORMATO =
            "Use la IP de la computadora servidor (por ejemplo 192.168.1.25) "
            + "o la dirección completa (http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank).";

    private static final Pattern PATRON_NUMERICO = Pattern.compile("[0-9.]+");
    private static final Pattern PATRON_IPV4 =
            Pattern.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})");

    private final Path rutaArchivo;
    private final Supplier<String> leerPropiedad;
    private final Supplier<String> leerVariable;
    private final Gson gson = new Gson();

    public ConfiguracionServidor() {
        this(rutaArchivoPorDefecto(),
                () -> System.getProperty(PROPIEDAD_BASE_WS),
                () -> System.getenv(VARIABLE_BASE_WS));
    }

    // La ruta del archivo, la propiedad y la variable se pueden reemplazar en las pruebas
    public ConfiguracionServidor(Path rutaArchivo, Supplier<String> leerPropiedad, Supplier<String> leerVariable) {
        this.rutaArchivo = rutaArchivo;
        this.leerPropiedad = leerPropiedad;
        this.leerVariable = leerVariable;
    }

    public OrigenUrl getOrigen() {
        if (leerExterna() != null) {
            return OrigenUrl.VARIABLE_ENTORNO;
        }
        return leerGuardada() == null ? OrigenUrl.POR_DEFECTO : OrigenUrl.GUARDADA;
    }

    public String getUrlActual() {
        String externa = leerExterna();
        if (externa != null) {
            return externa;
        }
        String guardada = leerGuardada();
        return guardada != null ? guardada : URL_POR_DEFECTO;
    }

    public void guardar(String url) {
        if (!esUrlValida(url)) {
            throw new IllegalArgumentException("La dirección del servidor no es válida. " + MENSAJE_FORMATO);
        }
        try {
            Path carpeta = rutaArchivo.getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.writeString(rutaArchivo, gson.toJson(new ArchivoServidor(url.trim())), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo guardar la dirección del servidor: " + ex.getMessage());
        }
    }

    public void restablecer() {
        try {
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo borrar la dirección guardada: " + ex.getMessage());
        }
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

        String resultado = uri.getScheme().toLowerCase() + "://" + uri.getHost().toLowerCase()
                + (puerto >= 0 ? ":" + puerto : "")
                + (sinRuta ? RUTA_SERVICIO : ruta);
        while (resultado.endsWith("/")) {
            resultado = resultado.substring(0, resultado.length() - 1);
        }
        return resultado;
    }

    public static boolean esUrlValida(String url) {
        if (url == null || url.isBlank()) {
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

    private String leerExterna() {
        String desdePropiedad = leerPropiedad.get();
        if (esUrlValida(desdePropiedad)) {
            return desdePropiedad.trim();
        }
        String desdeVariable = leerVariable.get();
        return esUrlValida(desdeVariable) ? desdeVariable.trim() : null;
    }

    private String leerGuardada() {
        try {
            if (!Files.exists(rutaArchivo)) {
                return null;
            }
            ArchivoServidor archivo = gson.fromJson(Files.readString(rutaArchivo, StandardCharsets.UTF_8),
                    ArchivoServidor.class);
            return archivo != null && esUrlValida(archivo.url) ? archivo.url.trim() : null;
        } catch (IOException | JsonParseException ex) {
            // Un archivo dañado o inaccesible no debe impedir que el cliente arranque con localhost
            return null;
        }
    }

    private static Path rutaArchivoPorDefecto() {
        String appData = System.getenv("APPDATA");
        Path base = appData != null && !appData.isBlank() ? Path.of(appData) : Path.of(System.getProperty("user.home"));
        return base.resolve("EurekaBank_GR8").resolve("servidor_soap.json");
    }

    private static final class ArchivoServidor {

        // Sin final: Gson lo asigna por reflexión y Java 26 avisa si se modifica un campo final
        @SerializedName("Url")
        private String url;

        private ArchivoServidor(String url) {
            this.url = url;
        }
    }
}
