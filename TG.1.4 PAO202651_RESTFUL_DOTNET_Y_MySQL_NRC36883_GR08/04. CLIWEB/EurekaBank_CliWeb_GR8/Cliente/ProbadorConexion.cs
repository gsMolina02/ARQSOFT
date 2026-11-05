using ec.edu.monster.Models;

namespace ec.edu.monster.Cliente;

/// <summary>
/// Comprueba que en una dirección responda el servicio RESTful EurekaBank antes de guardarla.
/// Pide GET /cuentas/00000000/movimientos sin token: el servicio responde 401 con
/// {"codigo": 401, "mensaje": "..."}, lo que confirma que está activo sin tocar ninguna cuenta.
/// Distingue un servidor apagado o bloqueado por el firewall de una ruta equivocada (404).
/// </summary>
public class ProbadorConexion
{
    private static readonly TimeSpan TiempoEspera = TimeSpan.FromSeconds(5);

    private readonly HttpMessageHandler? manejadorHttp;

    public ProbadorConexion()
    {
    }

    // El manejador HTTP se puede reemplazar en las pruebas para no depender del servidor
    public ProbadorConexion(HttpMessageHandler manejadorHttp)
    {
        this.manejadorHttp = manejadorHttp;
    }

    public async Task<ResultadoConexion> ProbarAsync(string baseUrl)
    {
        string url = baseUrl.Trim().TrimEnd('/');
        using var clienteHttp = manejadorHttp is null ? new HttpClient() : new HttpClient(manejadorHttp, false);
        clienteHttp.Timeout = TiempoEspera;

        try
        {
            using var peticion = new HttpRequestMessage(HttpMethod.Get, url + "/cuentas/00000000/movimientos");
            peticion.Headers.Accept.ParseAdd("application/json");
            using HttpResponseMessage respuesta = await clienteHttp.SendAsync(peticion);

            int codigo = (int)respuesta.StatusCode;
            string contenido = await respuesta.Content.ReadAsStringAsync();
            bool esElServicio = codigo == 401
                    && contenido.Contains("\"codigo\"", StringComparison.Ordinal)
                    && contenido.Contains("\"mensaje\"", StringComparison.Ordinal);

            if (esElServicio)
            {
                return new ResultadoConexion(true, $"Conexión correcta con el servidor REST EurekaBank en {url}.");
            }
            if (codigo == 404)
            {
                return new ResultadoConexion(false,
                    $"El equipo en {url} respondió con el código 404. Revise que la dirección termine en /api.");
            }
            return new ResultadoConexion(false,
                $"La dirección {url} responde (código {codigo}), pero no es el servicio REST EurekaBank.");
        }
        catch (TaskCanceledException)
        {
            return new ResultadoConexion(false,
                $"El servidor en {url} no respondió en {TiempoEspera.TotalSeconds:0} segundos. " +
                "Verifique la IP, que ambas computadoras estén en la misma red y que el firewall permita el puerto 5000.");
        }
        catch (Exception ex) when (ex is HttpRequestException or InvalidOperationException or UriFormatException)
        {
            return new ResultadoConexion(false,
                $"No fue posible conectar con {url}. " +
                "Verifique que el servidor esté encendido (PREPARAR_SERVIDOR_REST_5000.bat), la IP y el puerto, y el firewall del servidor.");
        }
    }
}
