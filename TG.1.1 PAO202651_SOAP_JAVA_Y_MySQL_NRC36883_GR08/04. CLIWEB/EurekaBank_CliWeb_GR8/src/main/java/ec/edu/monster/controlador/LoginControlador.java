package ec.edu.monster.controlador;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ContratoWSEurekaBank;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.util.ValidacionUtil;

/**
 * CU-01 Autenticarse, CU-02 Cambiar contraseña y CU-03 Cerrar sesión.
 */
public class LoginControlador {

    public static final String MENSAJE_CREDENCIALES_VACIAS = "El usuario y la contraseña son obligatorios";

    private final ContratoWSEurekaBank clienteWs;
    private final String urlServidor;

    public LoginControlador(ClienteWSEurekaBank clienteWs) {
        this(clienteWs, clienteWs.getUrl());
    }

    public LoginControlador(ContratoWSEurekaBank clienteWs, String urlServidor) {
        this.clienteWs = clienteWs;
        this.urlServidor = urlServidor;
    }

    /**
     * Dirección del servidor SOAP que usa este controlador (se muestra en la barra de estado).
     */
    public String getUrlServidor() {
        return urlServidor;
    }

    public String getEmpleado() {
        return clienteWs.getEmpleado();
    }

    public boolean haySesionActiva() {
        return clienteWs.haySesionActiva();
    }

    public RespuestaSesion iniciarSesion(LoginModelo modelo) {
        if (modelo == null || modelo.usuario() == null || modelo.usuario().isBlank()
                || modelo.clave() == null || modelo.clave().isEmpty()) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES_VACIAS);
        }
        return clienteWs.iniciarSesion(modelo.usuario().trim(), modelo.clave());
    }

    public String cambiarClave(String claveActual, String claveNueva, String confirmacion) {
        ValidacionUtil.validarCambioClave(claveActual, claveNueva, confirmacion);
        return clienteWs.cambiarClave(claveActual, claveNueva);
    }

    public String cerrarSesion() {
        return clienteWs.cerrarSesion();
    }

    public CuentaControlador crearControladorCuenta() {
        return new CuentaControlador(clienteWs);
    }
}
