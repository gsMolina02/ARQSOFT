using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;

namespace ec.edu.monster.Vista;

public class LoginVista
{
    private readonly MensajesModelo mensajes = new();
    private readonly ServidorControlador servidorControlador = new();

    public async Task IniciarAsync()
    {
        mensajes.MostrarBanner();

        while (true)
        {
            // Se crea un cliente nuevo cada vez que se cambia la dirección del servidor
            using var clienteWs = new ClienteWSEurekaBank(servidorControlador.UrlActual);
            var loginControlador = new LoginControlador(clienteWs);
            mensajes.MostrarDetalle("  Servidor REST: " + clienteWs.BaseUrl);

            if (await AtenderInicioSesionAsync(loginControlador))
            {
                return;
            }
            await new ServidorVista(servidorControlador).EjecutarAsync();
        }
    }

    /// <returns>true si el programa debe terminar; false si el usuario quiere configurar el servidor.</returns>
    private async Task<bool> AtenderInicioSesionAsync(LoginControlador loginControlador)
    {
        while (true)
        {
            mensajes.MostrarTitulo("INICIO DE SESIÓN");
            mensajes.MostrarMensaje("  Ingrese sus credenciales para acceder al sistema.");
            mensajes.MostrarMensaje("  (Escriba 0 como usuario para configurar el servidor.)");
            mensajes.MostrarMensaje("");
            string usuario = EntradaUtil.LeerTextoNoVacio("  > Usuario     : ");
            if (usuario == "0")
            {
                return false;
            }
            string clave = EntradaUtil.LeerClave("  > Contraseña  : ");

            try
            {
                RespuestaSesion sesion = await loginControlador.IniciarSesionAsync(new LoginModelo(usuario, clave));
                mensajes.MostrarMensaje("");
                mensajes.MostrarExito(sesion.Mensaje ?? $"Bienvenido, {sesion.Empleado}");
                mensajes.MostrarDetalle($"  La sesión vence tras {sesion.MinutosVigencia} minutos sin uso.");

                ResultadoMenu resultado = await new MenuVista(loginControlador).EjecutarAsync();
                switch (resultado)
                {
                    case ResultadoMenu.Salir:
                        mensajes.MostrarMensaje("");
                        mensajes.MostrarExito("Sesión finalizada. Hasta pronto.");
                        return true;
                    case ResultadoMenu.ConfigurarServidor:
                        return false;
                    default:
                        mensajes.MostrarAviso("  Vuelva a iniciar sesión.");
                        break;
                }
            }
            catch (ExcepcionClienteWS ex)
            {
                mensajes.MostrarError(ex.Message);
                if (ex.EsErrorConexion)
                {
                    // Si el servidor está en otra computadora, aquí se cambia su dirección
                    if (EntradaUtil.LeerSiNo("  ¿Desea configurar la dirección del servidor?"))
                    {
                        return false;
                    }
                    if (!EntradaUtil.LeerSiNo("  ¿Desea reintentar?"))
                    {
                        return true;
                    }
                }
            }
            catch (ArgumentException ex)
            {
                mensajes.MostrarError(ex.Message);
            }
        }
    }
}
