package ec.edu.monster.controlador;

import ec.edu.monster.cliente.ManejadorTokenSOAP;
import ec.edu.monster.util.AlmacenSesion;
import ec.edu.monster.util.ConfiguracionServidor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Muestra las páginas JSP de WEB-INF/vista con los datos comunes (raíz del sitio, servidor, empleado,
 * avisos y la clave de los formularios) y arma las redirecciones.
 */
final class Vistas {

    private Vistas() {
    }

    static void mostrar(HttpServletRequest peticion, HttpServletResponse respuesta, String pagina)
            throws ServletException, IOException {
        AlmacenSesion.moverAvisos(peticion);
        ManejadorTokenSOAP manejador = AlmacenSesion.obtenerManejador(peticion);
        peticion.setAttribute("raiz", peticion.getContextPath());
        peticion.setAttribute("urlServidor", new ConfiguracionServidor().getUrlActual());
        peticion.setAttribute("empleado", manejador == null ? null : manejador.getEmpleado());
        peticion.setAttribute("tokenFormulario", AlmacenSesion.tokenFormulario(peticion));
        peticion.getRequestDispatcher("/WEB-INF/vista/" + pagina + ".jsp").forward(peticion, respuesta);
    }

    static void redirigir(HttpServletRequest peticion, HttpServletResponse respuesta, String ruta) throws IOException {
        respuesta.sendRedirect(peticion.getContextPath() + ruta);
    }

    static String codificar(String valor) {
        return URLEncoder.encode(valor == null ? "" : valor, StandardCharsets.UTF_8);
    }

    static String parametro(HttpServletRequest peticion, String nombre) {
        String valor = peticion.getParameter(nombre);
        return valor == null ? null : valor.trim();
    }
}
