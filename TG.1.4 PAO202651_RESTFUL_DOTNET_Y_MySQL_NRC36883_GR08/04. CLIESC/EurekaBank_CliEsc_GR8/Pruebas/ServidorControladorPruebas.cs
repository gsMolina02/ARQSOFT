using ec.edu.monster.Cliente;
using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ServidorControladorPruebas : IDisposable
{
    private readonly string carpeta = Path.Combine(Path.GetTempPath(), "EurekaBankPruebas_" + Guid.NewGuid().ToString("N"));
    private readonly ManejadorHttpPrueba manejador = new();
    private readonly ServidorControlador controlador;

    public ServidorControladorPruebas()
    {
        var configuracion = new ConfiguracionServidor(Path.Combine(carpeta, "servidor_rest.json"), () => null);
        controlador = new ServidorControlador(configuracion, new ProbadorConexion(manejador));
    }

    public void Dispose()
    {
        if (Directory.Exists(carpeta))
        {
            Directory.Delete(carpeta, true);
        }
    }

    [Fact]
    public void DeberiaGuardarLaDireccionCompleta()
    {
        string url = controlador.Guardar("192.168.1.25");

        Assert.Equal("http://192.168.1.25:5000/api", url);
        Assert.Equal(url, controlador.UrlActual);
        Assert.Equal(OrigenUrl.Guardada, controlador.Origen);
    }

    [Fact]
    public void DeberiaRestablecerALocalhost()
    {
        controlador.Guardar("192.168.1.25");

        string url = controlador.Restablecer();

        Assert.Equal(ConfiguracionServidor.UrlPorDefecto, url);
        Assert.Equal(OrigenUrl.PorDefecto, controlador.Origen);
    }

    [Fact]
    public async Task DeberiaProbarLaDireccionCompleta()
    {
        ResultadoConexion resultado = await controlador.ProbarAsync("192.168.1.25");

        Assert.True(resultado.Exitosa);
        Assert.Equal("http://192.168.1.25:5000/api/cuentas/00000000/movimientos", manejador.UltimaUrl!.ToString());
    }

    [Fact]
    public async Task DeberiaRechazarUnaDireccionInvalidaSinLlamarAlServidor()
    {
        await Assert.ThrowsAsync<ArgumentException>(() => controlador.ProbarAsync("192.168.1"));
        Assert.Throws<ArgumentException>(() => controlador.Guardar(""));
        Assert.Null(manejador.UltimaUrl);
    }
}
