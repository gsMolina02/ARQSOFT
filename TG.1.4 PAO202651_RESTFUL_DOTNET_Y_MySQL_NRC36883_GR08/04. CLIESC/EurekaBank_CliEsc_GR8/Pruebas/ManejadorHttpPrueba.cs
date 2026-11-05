using System.Net;

namespace ec.edu.monster.Pruebas;

/// <summary>
/// Responde a las peticiones HTTP sin red. Por defecto imita al servicio EurekaBank sin token
/// (401 con {"codigo":401,"mensaje":"..."}); también puede lanzar la excepción indicada.
/// Guarda la última petición para revisar la URL, el método, el token y el cuerpo enviados.
/// </summary>
public class ManejadorHttpPrueba : HttpMessageHandler
{
    public HttpStatusCode Codigo { get; set; } = HttpStatusCode.Unauthorized;

    public string Contenido { get; set; } =
            "{\"codigo\":401,\"mensaje\":\"Acceso denegado: no se encontró el token. Inicie sesión primero.\"}";

    public Exception? Excepcion { get; set; }

    public Uri? UltimaUrl { get; private set; }

    public HttpMethod? UltimoMetodo { get; private set; }

    public string? UltimoToken { get; private set; }

    public string? UltimoCuerpo { get; private set; }

    public int Peticiones { get; private set; }

    public void Responder(HttpStatusCode codigo, string contenido)
    {
        Codigo = codigo;
        Contenido = contenido;
    }

    protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage peticion, CancellationToken cancelacion)
    {
        Peticiones++;
        UltimaUrl = peticion.RequestUri;
        UltimoMetodo = peticion.Method;
        UltimoToken = peticion.Headers.Authorization?.Parameter;
        UltimoCuerpo = peticion.Content is null ? null : await peticion.Content.ReadAsStringAsync(cancelacion);
        if (Excepcion is not null)
        {
            throw Excepcion;
        }
        return new HttpResponseMessage(Codigo) { Content = new StringContent(Contenido) };
    }
}
