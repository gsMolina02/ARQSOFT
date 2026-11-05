using ec.edu.monster.Cliente;
using ec.edu.monster.Servicios;
using ec.edu.monster.Models;
using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ServicioServidorPruebas : IDisposable
{
    private readonly string carpeta = Path.Combine(Path.GetTempPath(), "EurekaBankPruebas_" + Guid.NewGuid().ToString("N"));
    private readonly ManejadorHttpPrueba manejador = new();
    private readonly ServicioServidor servicio;

    public ServicioServidorPruebas()
    {
        var configuracion = new ConfiguracionServidor(Path.Combine(carpeta, "servidor_rest.json"), () => null);
        servicio = new ServicioServidor(configuracion, new ProbadorConexion(manejador));
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
        string url = servicio.Guardar("192.168.1.25");

        Assert.Equal("http://192.168.1.25:5000/api", url);
        Assert.Equal(url, servicio.UrlActual);
        Assert.Equal(OrigenUrl.Guardada, servicio.Origen);
    }

    [Fact]
    public void DeberiaRestablecerALocalhost()
    {
        servicio.Guardar("192.168.1.25");

        string url = servicio.Restablecer();

        Assert.Equal(ConfiguracionServidor.UrlPorDefecto, url);
        Assert.Equal(OrigenUrl.PorDefecto, servicio.Origen);
    }

    [Fact]
    public async Task DeberiaProbarLaDireccionCompleta()
    {
        ResultadoConexion resultado = await servicio.ProbarAsync("192.168.1.25");

        Assert.True(resultado.Exitosa);
        Assert.Equal("http://192.168.1.25:5000/api/cuentas/00000000/movimientos", manejador.UltimaUrl!.ToString());
    }

    [Fact]
    public async Task DeberiaRechazarUnaDireccionInvalidaSinLlamarAlServidor()
    {
        await Assert.ThrowsAsync<ArgumentException>(() => servicio.ProbarAsync("192.168.1"));
        Assert.Throws<ArgumentException>(() => servicio.Guardar(""));
        Assert.Null(manejador.UltimaUrl);
    }
}
