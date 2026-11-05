package ec.edu.monster.controlador;

import ec.edu.monster.cliente.ProbadorConexion;
import ec.edu.monster.modelo.ResultadoConexion;
import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.OrigenUrl;

/**
 * "Configurar servidor": completa, prueba y guarda la dirección del servidor SOAP.
 */
public class ServidorControlador {

    private final ConfiguracionServidor configuracion;
    private final ProbadorConexion probador;

    public ServidorControlador() {
        this(new ConfiguracionServidor(), new ProbadorConexion());
    }

    public ServidorControlador(ConfiguracionServidor configuracion, ProbadorConexion probador) {
        this.configuracion = configuracion;
        this.probador = probador;
    }

    public String getUrlActual() {
        return configuracion.getUrlActual();
    }

    public OrigenUrl getOrigen() {
        return configuracion.getOrigen();
    }

    /**
     * @return la dirección completa (192.168.1.25 → http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank)
     */
    public String completarUrl(String entrada) {
        return ConfiguracionServidor.completar(entrada);
    }

    public ResultadoConexion probar(String entrada) {
        return probador.probar(completarUrl(entrada));
    }

    public String guardar(String entrada) {
        String url = completarUrl(entrada);
        configuracion.guardar(url);
        return url;
    }

    public String restablecer() {
        configuracion.restablecer();
        return configuracion.getUrlActual();
    }
}
