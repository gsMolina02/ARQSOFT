package ec.edu.monster.vista;

import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ExcepcionClienteWS;
import ec.edu.monster.cliente.dto.RespuestaSesion;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.LoginModelo;
import ec.edu.monster.modelo.MensajesModelo;
import ec.edu.monster.util.EntradaUtil;

public class LoginVista {

    private final MensajesModelo mensajes = new MensajesModelo();
    private final ServidorControlador servidorControlador = new ServidorControlador();

    public void iniciar() {
        mensajes.mostrarBanner();

        while (true) {
            // Se crea un cliente nuevo cada vez que se cambia la dirección del servidor
            LoginControlador loginControlador =
                    new LoginControlador(new ClienteWSEurekaBank(servidorControlador.getUrlActual()));
            mensajes.mostrarDetalle("  Servidor SOAP: " + loginControlador.getUrlServidor());

            if (atenderInicioSesion(loginControlador)) {
                return;
            }
            new ServidorVista(servidorControlador).ejecutar();
        }
    }

    /**
     * @return true si el programa debe terminar; false si el usuario quiere configurar el servidor.
     */
    private boolean atenderInicioSesion(LoginControlador loginControlador) {
        while (true) {
            mensajes.mostrarTitulo("INICIO DE SESIÓN");
            mensajes.mostrarMensaje("  Ingrese sus credenciales para acceder al sistema.");
            mensajes.mostrarMensaje("  (Escriba 0 como usuario para configurar el servidor.)");
            mensajes.mostrarMensaje("");
            String usuario = EntradaUtil.leerTextoNoVacio("  > Usuario     : ");
            if (usuario.equals("0")) {
                return false;
            }
            String clave = EntradaUtil.leerClave("  > Contraseña  : ");

            try {
                RespuestaSesion sesion = loginControlador.iniciarSesion(new LoginModelo(usuario, clave));
                mensajes.mostrarMensaje("");
                mensajes.mostrarExito(sesion.mensaje());
                mensajes.mostrarDetalle("  La sesión vence tras " + sesion.minutosVigencia() + " minutos sin uso.");

                ResultadoMenu resultado = new MenuVista(loginControlador).ejecutar();
                switch (resultado) {
                    case SALIR -> {
                        mensajes.mostrarMensaje("");
                        mensajes.mostrarExito("Sesión finalizada. Hasta pronto.");
                        return true;
                    }
                    case CONFIGURAR_SERVIDOR -> {
                        return false;
                    }
                    default -> mensajes.mostrarAviso("  Vuelva a iniciar sesión.");
                }
            } catch (ExcepcionClienteWS ex) {
                mensajes.mostrarError(ex.getMessage());
                if (ex.esErrorConexion()) {
                    // Si el servidor está en otra computadora, aquí se cambia su dirección
                    if (EntradaUtil.leerSiNo("  ¿Desea configurar la dirección del servidor?")) {
                        return false;
                    }
                    if (!EntradaUtil.leerSiNo("  ¿Desea reintentar?")) {
                        return true;
                    }
                }
            } catch (IllegalArgumentException ex) {
                mensajes.mostrarError(ex.getMessage());
            }
        }
    }
}
