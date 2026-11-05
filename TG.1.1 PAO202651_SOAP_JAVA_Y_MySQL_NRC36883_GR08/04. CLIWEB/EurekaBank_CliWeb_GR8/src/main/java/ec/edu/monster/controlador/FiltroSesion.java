package ec.edu.monster.controlador;

import ec.edu.monster.util.AlmacenSesion;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Protege las páginas que requieren haber iniciado sesión: sin token, redirige al inicio de sesión.
 * Además pide al navegador no guardarlas, así "Atrás" no muestra datos después de cerrar sesión.
 */
@WebFilter(urlPatterns = {"/operaciones", "/operaciones/*", "/sesion/cambiar-contrasenia"})
public class FiltroSesion extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest peticion, HttpServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        if (!AlmacenSesion.haySesionActiva(peticion)) {
            AlmacenSesion.avisar(peticion, "Inicie sesión para continuar.");
            Vistas.redirigir(peticion, respuesta, "/sesion/login");
            return;
        }
        respuesta.setHeader("Cache-Control", "no-store");
        cadena.doFilter(peticion, respuesta);
    }
}
