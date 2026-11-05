package ec.edu.monster.vista;

import ec.edu.monster.controlador.ServidorControlador;
import ec.edu.monster.modelo.MensajesModelo;
import ec.edu.monster.modelo.ResultadoConexion;
import ec.edu.monster.util.ConfiguracionServidor;
import ec.edu.monster.util.EntradaUtil;
import ec.edu.monster.util.OrigenUrl;

public class ServidorVista {

    private final MensajesModelo mensajes = new MensajesModelo();
    private final ServidorControlador servidorControlador;

    public ServidorVista(ServidorControlador servidorControlador) {
        this.servidorControlador = servidorControlador;
    }

    public void ejecutar() {
        while (true) {
            mensajes.mostrarTitulo("CONFIGURAR SERVIDOR");
            mensajes.mostrarMensaje("  Servidor actual: " + servidorControlador.getUrlActual());
            mensajes.mostrarDetalle("  Origen: " + describirOrigen(servidorControlador.getOrigen()));
            if (servidorControlador.getOrigen() == OrigenUrl.VARIABLE_ENTORNO) {
                mensajes.mostrarAviso("  La variable " + ConfiguracionServidor.VARIABLE_BASE_WS + " (o la propiedad "
                        + ConfiguracionServidor.PROPIEDAD_BASE_WS + ") tiene prioridad sobre la dirección guardada.");
            }
            mensajes.mostrarMensaje("");
            mensajes.mostrarMensaje("  [1] Cambiar la dirección del servidor");
            mensajes.mostrarMensaje("  [2] Probar la conexión con el servidor actual");
            mensajes.mostrarMensaje("  [3] Volver a localhost (esta computadora)");
            mensajes.mostrarMensaje("  [0] Volver al inicio de sesión");

            int opcion = EntradaUtil.leerEntero("  > Seleccione una opción: ");

            switch (opcion) {
                case 1 -> cambiarServidor();
                case 2 -> probarServidorActual();
                case 3 -> {
                    try {
                        mensajes.mostrarExito("Se usará el servidor " + servidorControlador.restablecer());
                    } catch (IllegalArgumentException ex) {
                        mensajes.mostrarError(ex.getMessage());
                    }
                }
                case 0 -> {
                    return;
                }
                default -> mensajes.mostrarError("Opción no válida. Intente nuevamente.");
            }
        }
    }

    private void cambiarServidor() {
        mensajes.mostrarMensaje("");
        mensajes.mostrarMensaje("  Escriba la IP de la computadora servidor (por ejemplo 192.168.1.25)");
        mensajes.mostrarMensaje("  o la dirección completa (http://192.168.1.25:8080/WS_EurekaBank_Java_SOAP/WSEurekaBank).");
        String entrada = EntradaUtil.leerTextoNoVacio("  > Servidor: ");

        try {
            String url = servidorControlador.completarUrl(entrada);
            mensajes.mostrarDetalle("  Probando la conexión con " + url + " ...");
            ResultadoConexion resultado = servidorControlador.probar(url);

            if (resultado.exitosa()) {
                mensajes.mostrarExito(resultado.mensaje());
            } else {
                mensajes.mostrarError(resultado.mensaje());
                if (!EntradaUtil.leerSiNo("  ¿Desea guardar la dirección de todas formas?")) {
                    mensajes.mostrarAviso("  No se cambió el servidor.");
                    return;
                }
            }
            servidorControlador.guardar(url);
            mensajes.mostrarExito("Servidor guardado: " + url);
        } catch (IllegalArgumentException ex) {
            mensajes.mostrarError(ex.getMessage());
        }
    }

    private void probarServidorActual() {
        mensajes.mostrarDetalle("  Probando la conexión con " + servidorControlador.getUrlActual() + " ...");
        ResultadoConexion resultado = servidorControlador.probar(servidorControlador.getUrlActual());
        if (resultado.exitosa()) {
            mensajes.mostrarExito(resultado.mensaje());
        } else {
            mensajes.mostrarError(resultado.mensaje());
        }
    }

    private static String describirOrigen(OrigenUrl origen) {
        return switch (origen) {
            case VARIABLE_ENTORNO -> "variable " + ConfiguracionServidor.VARIABLE_BASE_WS
                    + " o propiedad " + ConfiguracionServidor.PROPIEDAD_BASE_WS;
            case GUARDADA -> "dirección guardada en esta computadora";
            default -> "valor por defecto (esta computadora)";
        };
    }
}
