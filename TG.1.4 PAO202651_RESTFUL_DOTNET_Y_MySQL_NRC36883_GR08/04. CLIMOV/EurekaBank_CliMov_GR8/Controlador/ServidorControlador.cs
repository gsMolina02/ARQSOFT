using ec.edu.monster.Cliente;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;

namespace ec.edu.monster.Controlador;

/// <summary>
/// Completa y prueba la dirección del servidor antes de guardarla. El guardado lo hace la vista con
/// ConfiguracionServidor, que usa las preferencias del teléfono.
/// </summary>
public class ServidorControlador
{
    private readonly ProbadorConexion probador;

    public ServidorControlador() : this(new ProbadorConexion())
    {
    }

    public ServidorControlador(ProbadorConexion probador)
    {
        this.probador = probador;
    }

    /// <returns>La dirección completa del servicio (192.168.1.25 → http://192.168.1.25:5000/api).</returns>
    public string CompletarUrl(string? entrada) => DireccionServidor.Completar(entrada);

    public async Task<ResultadoConexion> ProbarAsync(string? entrada)
    {
        return await probador.ProbarAsync(CompletarUrl(entrada));
    }
}
