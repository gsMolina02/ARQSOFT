package ec.edu.monster.controlador;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.ManejadorTokenSOAP;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.modelo.ResultadoConexion;
import ec.edu.monster.modelo.ServidorModelo;
import ec.edu.monster.util.AlmacenSesion;
import ec.edu.monster.util.OrigenUrl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Inicio de sesión (CU-01), configuración del servidor, cambio de contraseña (CU-02) y cierre de sesión (CU-03).
 * Rutas: /sesion/login, /sesion/servidor, /sesion/probar-servidor, /sesion/guardar-servidor,
 * /sesion/cambiar-contrasenia y /sesion/cerrar-sesion.
 */
@WebServlet("/sesion/*")
public class SesionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        switch (accion(peticion)) {
            case "/login" -> {
                if (AlmacenSesion.haySesionActiva(peticion)) {
                    Vistas.redirigir(peticion, respuesta, "/operaciones");
                } else {
                    Vistas.mostrar(peticion, respuesta, "login");
                }
            }
            case "/servidor" -> {
                // La dirección del servidor solo se cambia sin sesión: el token vale únicamente en el servidor que lo emitió
                if (AlmacenSesion.haySesionActiva(peticion)) {
                    Vistas.redirigir(peticion, respuesta, "/operaciones");
                } else {
                    mostrarServidor(peticion, respuesta, crearModeloServidor(servidor().getUrlActual()));
                }
            }
            case "/cambiar-contrasenia" -> Vistas.mostrar(peticion, respuesta, "cambiarContrasenia");
            default -> Vistas.redirigir(peticion, respuesta, "/sesion/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String accion = accion(peticion);
        if (!AlmacenSesion.tokenFormularioValido(peticion)) {
            AlmacenSesion.avisar(peticion, "La página expiró. Vuelva a enviar el formulario.");
            Vistas.redirigir(peticion, respuesta, accion.equals("/cambiar-contrasenia") ? "/sesion/cambiar-contrasenia"
                    : accion.endsWith("-servidor") ? "/sesion/servidor" : "/sesion/login");
            return;
        }
        switch (accion) {
            case "/login" -> iniciarSesion(peticion, respuesta);
            case "/probar-servidor" -> probarServidor(peticion, respuesta);
            case "/guardar-servidor" -> guardarServidor(peticion, respuesta);
            case "/cerrar-sesion" -> cerrarSesion(peticion, respuesta);
            case "/cambiar-contrasenia" -> cambiarContrasenia(peticion, respuesta);
            default -> Vistas.redirigir(peticion, respuesta, "/sesion/login");
        }
    }

    private void iniciarSesion(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String usuario = Vistas.parametro(peticion, "usuario");
        ManejadorTokenSOAP manejador = new ManejadorTokenSOAP();
        try {
            crearControladorLogin(manejador).iniciarSesion(new LoginModelo(usuario, peticion.getParameter("contrasenia")));
            AlmacenSesion.iniciar(peticion, manejador);
            Vistas.redirigir(peticion, respuesta, "/operaciones");
        } catch (ExcepcionClienteWS | IllegalArgumentException ex) {
            // Por seguridad, la contraseña no se devuelve a la página
            peticion.setAttribute("error", ex.getMessage());
            peticion.setAttribute("usuario", usuario);
            Vistas.mostrar(peticion, respuesta, "login");
        }
    }

    private void probarServidor(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        if (redirigirSiHaySesion(peticion, respuesta)) {
            return;
        }
        String direccion = Vistas.parametro(peticion, "direccion");
        try {
            ServidorControlador servidor = servidor();
            String url = servidor.completarUrl(direccion);
            ResultadoConexion resultado = servidor.probar(url);
            ServidorModelo modelo = crearModeloServidor(url);
            if (resultado.exitosa()) {
                modelo.setExito(resultado.mensaje());
            } else {
                modelo.setError(resultado.mensaje());
            }
            mostrarServidor(peticion, respuesta, modelo);
        } catch (IllegalArgumentException ex) {
            ServidorModelo modelo = crearModeloServidor(direccion);
            modelo.setError(ex.getMessage());
            mostrarServidor(peticion, respuesta, modelo);
        }
    }

    private void guardarServidor(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        if (redirigirSiHaySesion(peticion, respuesta)) {
            return;
        }
        String direccion = Vistas.parametro(peticion, "direccion");
        try {
            ServidorControlador servidor = servidor();
            String url = servidor.completarUrl(direccion);
            ResultadoConexion resultado = servidor.probar(url);
            if (!resultado.exitosa() && !"true".equals(peticion.getParameter("guardarSinConexion"))) {
                ServidorModelo modelo = crearModeloServidor(url);
                modelo.setError(resultado.mensaje());
                modelo.setOfrecerGuardarSinConexion(true);
                mostrarServidor(peticion, respuesta, modelo);
                return;
            }
            servidor.guardar(url);
            AlmacenSesion.avisarExito(peticion, "Servidor guardado: " + url);
            Vistas.redirigir(peticion, respuesta, "/sesion/login");
        } catch (IllegalArgumentException ex) {
            ServidorModelo modelo = crearModeloServidor(direccion);
            modelo.setError(ex.getMessage());
            mostrarServidor(peticion, respuesta, modelo);
        }
    }

    /**
     * El parámetro "configurar" llega cuando se perdió la conexión: después de cerrar se abre "Configurar servidor".
     */
    private void cerrarSesion(HttpServletRequest peticion, HttpServletResponse respuesta) throws IOException {
        ManejadorTokenSOAP manejador = AlmacenSesion.obtenerManejador(peticion);
        boolean notificado = true;
        if (manejador != null) {
            try {
                crearControladorLogin(manejador).cerrarSesion();
            } catch (ExcepcionClienteWS ex) {
                // Si la sesión ya había vencido en el servidor no hay nada más que avisar
                notificado = !ex.esErrorConexion();
            }
        }
        AlmacenSesion.limpiar(peticion);

        if (notificado) {
            AlmacenSesion.avisarExito(peticion, "Sesión cerrada correctamente.");
        } else {
            AlmacenSesion.avisar(peticion, "Sesión cerrada en este equipo, pero no se pudo avisar al servidor; "
                    + "vencerá sola tras 30 minutos sin uso.");
        }
        Vistas.redirigir(peticion, respuesta, "true".equals(peticion.getParameter("configurar"))
                ? "/sesion/servidor" : "/sesion/login");
    }

    private void cambiarContrasenia(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        try {
            String mensaje = crearControladorLogin(AlmacenSesion.obtenerManejador(peticion)).cambiarClave(
                    peticion.getParameter("contraseniaActual"), peticion.getParameter("contraseniaNueva"),
                    peticion.getParameter("confirmacion"));
            AlmacenSesion.avisarExito(peticion, mensaje + ". Use la nueva contraseña la próxima vez que inicie sesión.");
            Vistas.redirigir(peticion, respuesta, "/operaciones");
        } catch (ExcepcionClienteWS ex) {
            if (ex.esErrorAutenticacion()) {
                AlmacenSesion.limpiar(peticion);
                AlmacenSesion.avisar(peticion, ex.getMessage() + " Inicie sesión nuevamente.");
                Vistas.redirigir(peticion, respuesta, "/sesion/login");
                return;
            }
            mostrarErrorClave(peticion, respuesta, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            mostrarErrorClave(peticion, respuesta, ex.getMessage());
        }
    }

    // Las contraseñas no se devuelven a la página: el usuario las vuelve a escribir
    private static void mostrarErrorClave(HttpServletRequest peticion, HttpServletResponse respuesta, String mensaje)
            throws ServletException, IOException {
        peticion.setAttribute("error", mensaje);
        Vistas.mostrar(peticion, respuesta, "cambiarContrasenia");
    }

    private static boolean redirigirSiHaySesion(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws IOException {
        if (AlmacenSesion.haySesionActiva(peticion)) {
            Vistas.redirigir(peticion, respuesta, "/operaciones");
            return true;
        }
        return false;
    }

    private static LoginControlador crearControladorLogin(ManejadorTokenSOAP manejador) {
        return new LoginControlador(new ClienteWSEurekaBank(servidor().getUrlActual(), manejador));
    }

    private static ServidorControlador servidor() {
        return new ServidorControlador();
    }

    private static ServidorModelo crearModeloServidor(String direccion) {
        ServidorControlador servidor = servidor();
        return new ServidorModelo(direccion, servidor.getUrlActual(), servidor.getOrigen() == OrigenUrl.VARIABLE_ENTORNO);
    }

    private static void mostrarServidor(HttpServletRequest peticion, HttpServletResponse respuesta, ServidorModelo modelo)
            throws ServletException, IOException {
        peticion.setAttribute("modelo", modelo);
        Vistas.mostrar(peticion, respuesta, "servidor");
    }

    private static String accion(HttpServletRequest peticion) {
        String ruta = peticion.getPathInfo();
        return ruta == null ? "" : ruta;
    }
}
