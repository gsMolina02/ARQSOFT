package ec.edu.monster.util;

import ec.edu.monster.cliente.ManejadorTokenSOAP;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Datos de la sesión del navegador de cada usuario: el token del servicio con el nombre del empleado, los avisos
 * que se muestran después de una redirección y la clave que protege los formularios (CSRF).
 * Las cuentas y movimientos no se guardan aquí: los guarda el servidor SOAP en MySQL.
 */
public final class AlmacenSesion {

    public static final String PARAMETRO_TOKEN_FORMULARIO = "_token";

    private static final String CLAVE_MANEJADOR = "eurekabank.manejadorToken";
    private static final String CLAVE_TOKEN_FORMULARIO = "eurekabank.tokenFormulario";
    private static final String CLAVE_EXITO = "eurekabank.exito";
    private static final String CLAVE_AVISO = "eurekabank.aviso";
    private static final String CLAVE_COMPROBANTE_TITULO = "eurekabank.comprobanteTitulo";
    private static final String CLAVE_COMPROBANTE_VALOR = "eurekabank.comprobanteValor";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private AlmacenSesion() {
    }

    /**
     * Token y empleado de la sesión; null si el usuario no ha iniciado sesión.
     */
    public static ManejadorTokenSOAP obtenerManejador(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        Object manejador = sesion == null ? null : sesion.getAttribute(CLAVE_MANEJADOR);
        return manejador instanceof ManejadorTokenSOAP propio && propio.haySesionActiva() ? propio : null;
    }

    public static boolean haySesionActiva(HttpServletRequest peticion) {
        return obtenerManejador(peticion) != null;
    }

    /**
     * Guarda el token recibido al iniciar sesión. Se cambia el identificador de la sesión del navegador
     * para que una cookie anterior a la autenticación no sirva para entrar (fijación de sesión).
     */
    public static void iniciar(HttpServletRequest peticion, ManejadorTokenSOAP manejador) {
        peticion.getSession(true);
        peticion.changeSessionId();
        peticion.getSession().setAttribute(CLAVE_MANEJADOR, manejador);
    }

    public static void limpiar(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
    }

    // Avisos que sobreviven a una redirección (por ejemplo, "Contraseña actualizada")

    public static void avisarExito(HttpServletRequest peticion, String mensaje) {
        peticion.getSession(true).setAttribute(CLAVE_EXITO, mensaje);
    }

    public static void avisar(HttpServletRequest peticion, String mensaje) {
        peticion.getSession(true).setAttribute(CLAVE_AVISO, mensaje);
    }

    /**
     * Pasa los avisos pendientes a la petición actual (atributos "exito" y "aviso") y los borra de la sesión.
     */
    public static void moverAvisos(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        if (sesion == null) {
            return;
        }
        peticion.setAttribute("exito", tomar(sesion, CLAVE_EXITO));
        peticion.setAttribute("aviso", tomar(sesion, CLAVE_AVISO));
    }

    public static void guardarComprobante(HttpServletRequest peticion, String titulo, String valor) {
        HttpSession sesion = peticion.getSession(true);
        sesion.setAttribute(CLAVE_COMPROBANTE_TITULO, titulo);
        sesion.setAttribute(CLAVE_COMPROBANTE_VALOR, valor);
    }

    /**
     * @return título y valor del comprobante de la última operación, o null si no hay
     */
    public static String[] tomarComprobante(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        if (sesion == null) {
            return null;
        }
        String titulo = tomar(sesion, CLAVE_COMPROBANTE_TITULO);
        String valor = tomar(sesion, CLAVE_COMPROBANTE_VALOR);
        return titulo == null ? null : new String[] {titulo, valor};
    }

    /**
     * Clave aleatoria de la sesión que cada formulario envía en un campo oculto: un sitio ajeno no la conoce
     * y no puede hacer que el navegador registre operaciones a nombre del usuario.
     */
    public static String tokenFormulario(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(true);
        Object actual = sesion.getAttribute(CLAVE_TOKEN_FORMULARIO);
        if (actual instanceof String token) {
            return token;
        }
        byte[] bytes = new byte[24];
        ALEATORIO.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        sesion.setAttribute(CLAVE_TOKEN_FORMULARIO, token);
        return token;
    }

    public static boolean tokenFormularioValido(HttpServletRequest peticion) {
        HttpSession sesion = peticion.getSession(false);
        Object esperado = sesion == null ? null : sesion.getAttribute(CLAVE_TOKEN_FORMULARIO);
        String recibido = peticion.getParameter(PARAMETRO_TOKEN_FORMULARIO);
        return esperado instanceof String token && recibido != null
                && MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }

    private static String tomar(HttpSession sesion, String clave) {
        Object valor = sesion.getAttribute(clave);
        sesion.removeAttribute(clave);
        return valor instanceof String texto ? texto : null;
    }
}
