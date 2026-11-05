using ec.edu.monster.Cliente;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;

namespace ec.edu.monster.Controlador;

/// <summary>
/// "Configurar servidor": completa, prueba y guarda la dirección del servidor REST.
/// </summary>
public class ServidorControlador
{
    private readonly ConfiguracionServidor configuracion;
    private readonly ProbadorConexion probador;

    public ServidorControlador() : this(new ConfiguracionServidor(), new ProbadorConexion())
    {
    }

    public ServidorControlador(ConfiguracionServidor configuracion, ProbadorConexion probador)
    {
        this.configuracion = configuracion;
        this.probador = probador;
    }

    public string UrlActual => configuracion.UrlActual;

    public OrigenUrl Origen => configuracion.Origen;

    /// <returns>La dirección completa del servicio (192.168.1.25 → http://192.168.1.25:5000/api).</returns>
    public string CompletarUrl(string? entrada) => ConfiguracionServidor.Completar(entrada);

    public async Task<ResultadoConexion> ProbarAsync(string? entrada)
    {
        return await probador.ProbarAsync(CompletarUrl(entrada));
    }

    public string Guardar(string? entrada)
    {
        string url = CompletarUrl(entrada);
        configuracion.Guardar(url);
        return url;
    }

    public string Restablecer()
    {
        configuracion.Restablecer();
        return configuracion.UrlActual;
    }
}
