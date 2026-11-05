using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;

namespace ec.edu.monster.Vista;

public class ServidorVista
{
    private readonly MensajesModelo mensajes = new();
    private readonly ServidorControlador servidorControlador;

    public ServidorVista(ServidorControlador servidorControlador)
    {
        this.servidorControlador = servidorControlador;
    }

    public async Task EjecutarAsync()
    {
        while (true)
        {
            mensajes.MostrarTitulo("CONFIGURAR SERVIDOR");
            mensajes.MostrarMensaje("  Servidor actual: " + servidorControlador.UrlActual);
            mensajes.MostrarDetalle("  Origen: " + DescribirOrigen(servidorControlador.Origen));
            if (servidorControlador.Origen == OrigenUrl.VariableEntorno)
            {
                mensajes.MostrarAviso($"  La variable de entorno {ConfiguracionServidor.VariableBaseWs} tiene prioridad " +
                        "sobre la dirección guardada.");
            }
            mensajes.MostrarMensaje("");
            mensajes.MostrarMensaje("  [1] Cambiar la dirección del servidor");
            mensajes.MostrarMensaje("  [2] Probar la conexión con el servidor actual");
            mensajes.MostrarMensaje("  [3] Volver a localhost (esta computadora)");
            mensajes.MostrarMensaje("  [0] Volver al inicio de sesión");

            int opcion = EntradaUtil.LeerEntero("  > Seleccione una opción: ");

            switch (opcion)
            {
                case 1:
                    await CambiarServidorAsync();
                    break;
                case 2:
                    await ProbarServidorActualAsync();
                    break;
                case 3:
                    string url = servidorControlador.Restablecer();
                    mensajes.MostrarExito("Se usará el servidor " + url);
                    break;
                case 0:
                    return;
                default:
                    mensajes.MostrarError("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }

    private async Task CambiarServidorAsync()
    {
        mensajes.MostrarMensaje("");
        mensajes.MostrarMensaje("  Escriba la IP de la computadora servidor (por ejemplo 192.168.1.25)");
        mensajes.MostrarMensaje("  o la dirección completa (http://192.168.1.25:5000/api).");
        string entrada = EntradaUtil.LeerTextoNoVacio("  > Servidor: ");

        try
        {
            string url = servidorControlador.CompletarUrl(entrada);
            mensajes.MostrarDetalle("  Probando la conexión con " + url + " ...");
            ResultadoConexion resultado = await servidorControlador.ProbarAsync(url);

            if (resultado.Exitosa)
            {
                mensajes.MostrarExito(resultado.Mensaje);
            }
            else
            {
                mensajes.MostrarError(resultado.Mensaje);
                if (!EntradaUtil.LeerSiNo("  ¿Desea guardar la dirección de todas formas?"))
                {
                    mensajes.MostrarAviso("  No se cambió el servidor.");
                    return;
                }
            }
            servidorControlador.Guardar(url);
            mensajes.MostrarExito("Servidor guardado: " + url);
        }
        catch (ArgumentException ex)
        {
            mensajes.MostrarError(ex.Message);
        }
    }

    private async Task ProbarServidorActualAsync()
    {
        mensajes.MostrarDetalle("  Probando la conexión con " + servidorControlador.UrlActual + " ...");
        ResultadoConexion resultado = await servidorControlador.ProbarAsync(servidorControlador.UrlActual);
        if (resultado.Exitosa)
        {
            mensajes.MostrarExito(resultado.Mensaje);
        }
        else
        {
            mensajes.MostrarError(resultado.Mensaje);
        }
    }

    private static string DescribirOrigen(OrigenUrl origen) => origen switch
    {
        OrigenUrl.VariableEntorno => "variable de entorno " + ConfiguracionServidor.VariableBaseWs,
        OrigenUrl.Guardada => "dirección guardada en esta computadora",
        _ => "valor por defecto (esta computadora)"
    };
}
