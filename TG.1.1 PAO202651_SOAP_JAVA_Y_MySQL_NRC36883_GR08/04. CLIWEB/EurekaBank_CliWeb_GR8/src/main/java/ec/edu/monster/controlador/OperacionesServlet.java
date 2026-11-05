package ec.edu.monster.controlador;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.dto.RespuestaMovimientos;
import ec.edu.monster.cliente.dto.RespuestaTransaccion;
import ec.edu.monster.modelo.ExcepcionCampo;
import ec.edu.monster.modelo.OperacionesModelo;
import ec.edu.monster.modelo.TipoOperacion;
import ec.edu.monster.util.AlmacenSesion;
import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.FormatoUtil;
import ec.edu.monster.util.ValidacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Pantalla principal: consulta de movimientos (CU-04), depósito (CU-05), retiro (CU-06) y transferencia (CU-07).
 * Después de registrar una operación se redirige a la consulta de la cuenta (patrón PRG):
 * recargar la página no repite el depósito.
 */
@WebServlet(urlPatterns = {"/operaciones", "/operaciones/ejecutar"})
public class OperacionesServlet extends HttpServlet {

    /**
     * Muestra la pestaña elegida y, si se indica la cuenta, sus datos y movimientos a la derecha.
     */
    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        OperacionesModelo modelo = new OperacionesModelo();
        modelo.setOperacion(TipoOperacion.desde(peticion.getParameter("operacion")));
        String cuenta = Vistas.parametro(peticion, "cuenta");
        modelo.setCuenta(cuenta);

        String[] comprobante = AlmacenSesion.tomarComprobante(peticion);
        if (comprobante != null) {
            modelo.setResultadoTitulo(comprobante[0]);
            modelo.setResultadoValor(comprobante[1]);
        }

        if (cuenta != null && !cuenta.isEmpty()) {
            try {
                RespuestaMovimientos consulta = crearControladorCuenta(peticion).consultar(cuenta);
                modelo.setConsulta(consulta);
                if (modelo.getOperacion() == TipoOperacion.MOVIMIENTOS && comprobante == null) {
                    modelo.setResultadoTitulo("Saldo disponible de la cuenta " + consulta.cuenta().numero()
                            + " (" + consulta.cuenta().estado() + ")");
                    modelo.setResultadoValor(modelo.getSaldo());
                }
            } catch (ExcepcionClienteWS ex) {
                if (manejarErrorServicio(peticion, respuesta, ex, modelo)) {
                    return;
                }
            } catch (IllegalArgumentException ex) {
                modelo.setError(ex.getMessage());
                modelo.setCampoConError("cuenta");
            }
        }
        mostrar(peticion, respuesta, modelo);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        OperacionesModelo modelo = new OperacionesModelo();
        modelo.setOperacion(TipoOperacion.desde(peticion.getParameter("operacion")));
        modelo.setCuenta(Vistas.parametro(peticion, "cuenta"));
        modelo.setCuentaDestino(Vistas.parametro(peticion, "cuentaDestino"));
        modelo.setImporte(Vistas.parametro(peticion, "importe"));

        if (!AlmacenSesion.tokenFormularioValido(peticion)) {
            AlmacenSesion.avisar(peticion, "La página expiró. Vuelva a enviar el formulario.");
            Vistas.redirigir(peticion, respuesta, "/operaciones?operacion=" + modelo.getOperacion().getParametro());
            return;
        }

        if (modelo.getOperacion() == TipoOperacion.MOVIMIENTOS) {
            try {
                String cuenta = ValidacionUtil.validarCuenta(modelo.getCuenta());
                Vistas.redirigir(peticion, respuesta, "/operaciones?operacion=movimientos&cuenta=" + Vistas.codificar(cuenta));
            } catch (IllegalArgumentException ex) {
                mostrarConError(peticion, respuesta, modelo, ex.getMessage(), "cuenta");
            }
            return;
        }

        try {
            RespuestaTransaccion transaccion = FormularioOperacion.registrar(crearControladorCuenta(peticion), modelo);
            String cuenta = ValidacionUtil.validarCuenta(modelo.getCuenta());

            AlmacenSesion.avisarExito(peticion, transaccion.mensaje() + ".");
            AlmacenSesion.guardarComprobante(peticion, "Movimiento Nro " + transaccion.numeroMovimiento()
                    + " · nuevo saldo de la cuenta " + cuenta + ":", FormatoUtil.formatearMonto(transaccion.saldo()));
            Vistas.redirigir(peticion, respuesta, "/operaciones?operacion=" + modelo.getOperacion().getParametro()
                    + "&cuenta=" + Vistas.codificar(cuenta));
        } catch (ExcepcionCampo ex) {
            mostrarConError(peticion, respuesta, modelo, ex.getMessage(), ex.getCampo());
        } catch (ExcepcionClienteWS ex) {
            if (!manejarErrorServicio(peticion, respuesta, ex, modelo)) {
                mostrar(peticion, respuesta, modelo);
            }
        }
    }

    // Cada petición usa un cliente nuevo con el token guardado en la sesión del navegador
    private static ec.edu.monster.controlador.CuentaControlador crearControladorCuenta(HttpServletRequest peticion) {
        String url = new ConfiguracionServidor().getUrlActual();
        ClienteWSEurekaBank cliente = new ClienteWSEurekaBank(url, AlmacenSesion.obtenerManejador(peticion));
        return new LoginControlador(cliente).crearControladorCuenta();
    }

    /**
     * 401: token inválido o sesión vencida (CU-10) → inicio de sesión.
     * Sin conexión: se muestra el error y se ofrece cerrar sesión para configurar el servidor.
     *
     * @return true si ya se respondió con una redirección
     */
    private static boolean manejarErrorServicio(HttpServletRequest peticion, HttpServletResponse respuesta,
            ExcepcionClienteWS ex, OperacionesModelo modelo) throws IOException {
        if (ex.esErrorAutenticacion()) {
            AlmacenSesion.limpiar(peticion);
            AlmacenSesion.avisar(peticion, ex.getMessage() + " Inicie sesión nuevamente.");
            Vistas.redirigir(peticion, respuesta, "/sesion/login");
            return true;
        }
        modelo.setSinConexion(ex.esErrorConexion());
        modelo.setCampoConError(ex.getCodigoEstado() == 404 ? "cuenta" : null);
        modelo.setError(ex.getMessage());
        return false;
    }

    private static void mostrarConError(HttpServletRequest peticion, HttpServletResponse respuesta,
            OperacionesModelo modelo, String mensaje, String campo) throws ServletException, IOException {
        modelo.setError(mensaje);
        modelo.setCampoConError(campo);
        mostrar(peticion, respuesta, modelo);
    }

    private static void mostrar(HttpServletRequest peticion, HttpServletResponse respuesta, OperacionesModelo modelo)
            throws ServletException, IOException {
        // La lista de cuentas es una ayuda: si el servidor no responde, los campos siguen aceptando el número
        // escrito (y sin conexión ni se intenta, para no esperar otra vez)
        if (!modelo.isSinConexion()) {
            try {
                modelo.setCuentas(crearControladorCuenta(peticion).listarCuentas().cuentas());
            } catch (ExcepcionClienteWS ex) {
                modelo.setCuentas(List.of());
            }
        }
        peticion.setAttribute("modelo", modelo);
        Vistas.mostrar(peticion, respuesta, "operaciones");
    }
}
