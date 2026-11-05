package ec.edu.monster;

import android.app.Application;
import ec.edu.monster.cliente.ClienteWSEurekaBank;
import ec.edu.monster.cliente.ProbadorConexion;
import ec.edu.monster.controlador.LoginControlador;
import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.util.AlmacenPreferencias;
import ec.edu.monster.util.ConfiguracionServidor;

/**
 * Datos que comparten las pantallas mientras la aplicación está abierta: el controlador de la sesión
 * (con el token) y el de la configuración del servidor.
 */
public class EurekaBankApp extends Application {

    private ServidorControlador servidorControlador;
    private LoginControlador loginControlador;

    @Override
    public void onCreate() {
        super.onCreate();
        servidorControlador = new ServidorControlador(
                new ConfiguracionServidor(new AlmacenPreferencias(this)), new ProbadorConexion());
        conectar();
    }

    /**
     * Crea el cliente del servicio con la dirección configurada (se repite al cambiar de servidor).
     */
    public void conectar() {
        loginControlador = new LoginControlador(new ClienteWSEurekaBank(servidorControlador.getUrlActual()));
    }

    public ServidorControlador getServidorControlador() {
        return servidorControlador;
    }

    public LoginControlador getLoginControlador() {
        return loginControlador;
    }
}
