using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ConfiguracionServidorPruebas : IDisposable
{
    private readonly string carpeta = Path.Combine(Path.GetTempPath(), "EurekaBankPruebas_" + Guid.NewGuid().ToString("N"));
    private readonly string rutaArchivo;
    private string? variable;

    public ConfiguracionServidorPruebas()
    {
        rutaArchivo = Path.Combine(carpeta, "servidor_rest.json");
    }

    public void Dispose()
    {
        if (Directory.Exists(carpeta))
        {
            Directory.Delete(carpeta, true);
        }
    }

    private ConfiguracionServidor Crear() => new(rutaArchivo, () => variable);

    [Fact]
    public void DeberiaUsarLocalhostSiNoHayNadaConfigurado()
    {
        ConfiguracionServidor configuracion = Crear();

        Assert.Equal(ConfiguracionServidor.UrlPorDefecto, configuracion.UrlActual);
        Assert.Equal(OrigenUrl.PorDefecto, configuracion.Origen);
    }

    [Fact]
    public void DeberiaGuardarYLeerLaDireccion()
    {
        Crear().Guardar("http://192.168.1.25:5000/api");

        ConfiguracionServidor otraInstancia = Crear();
        Assert.Equal("http://192.168.1.25:5000/api", otraInstancia.UrlActual);
        Assert.Equal(OrigenUrl.Guardada, otraInstancia.Origen);
    }

    [Fact]
    public void DeberiaDarPrioridadALaVariableDeEntorno()
    {
        Crear().Guardar("http://192.168.1.25:5000/api");
        variable = "http://10.0.0.7:5000/api";

        ConfiguracionServidor configuracion = Crear();
        Assert.Equal("http://10.0.0.7:5000/api", configuracion.UrlActual);
        Assert.Equal(OrigenUrl.VariableEntorno, configuracion.Origen);
    }

    [Theory]
    [InlineData("")]
    [InlineData("   ")]
    [InlineData("no es una url")]
    [InlineData("ftp://192.168.1.25")]
    public void DeberiaIgnorarUnaVariableDeEntornoInvalida(string valor)
    {
        variable = valor;

        Assert.Equal(ConfiguracionServidor.UrlPorDefecto, Crear().UrlActual);
    }

    [Fact]
    public void DeberiaVolverALocalhostAlRestablecer()
    {
        ConfiguracionServidor configuracion = Crear();
        configuracion.Guardar("http://192.168.1.25:5000/api");

        configuracion.Restablecer();

        Assert.Equal(ConfiguracionServidor.UrlPorDefecto, configuracion.UrlActual);
        Assert.False(File.Exists(rutaArchivo));
    }

    [Theory]
    [InlineData("{ esto no es json")]
    [InlineData("{\"Url\":\"texto cualquiera\"}")]
    [InlineData("{}")]
    public void DeberiaIgnorarUnArchivoDaniado(string contenido)
    {
        Directory.CreateDirectory(carpeta);
        File.WriteAllText(rutaArchivo, contenido);

        Assert.Equal(ConfiguracionServidor.UrlPorDefecto, Crear().UrlActual);
    }

    [Fact]
    public void DeberiaRechazarGuardarUnaDireccionInvalida()
    {
        Assert.Throws<ArgumentException>(() => Crear().Guardar("no es una url"));
        Assert.False(File.Exists(rutaArchivo));
    }

    [Theory]
    [InlineData("192.168.1.25", "http://192.168.1.25:5000/api")]
    [InlineData("  192.168.1.25  ", "http://192.168.1.25:5000/api")]
    [InlineData("192.168.1.25:6000", "http://192.168.1.25:6000/api")]
    [InlineData("http://192.168.1.25", "http://192.168.1.25:5000/api")]
    [InlineData("http://192.168.1.25/", "http://192.168.1.25:5000/api")]
    [InlineData("PC-STEVEN", "http://pc-steven:5000/api")]
    [InlineData("localhost", "http://localhost:5000/api")]
    [InlineData("http://192.168.1.25:5000/api", "http://192.168.1.25:5000/api")]
    [InlineData("http://192.168.1.25:5000/api/", "http://192.168.1.25:5000/api")]
    [InlineData("https://servidor.local/otra/ruta.svc", "https://servidor.local/otra/ruta.svc")]
    public void DeberiaCompletarLaDireccion(string entrada, string esperado)
    {
        Assert.Equal(esperado, ConfiguracionServidor.Completar(entrada));
    }

    [Theory]
    [InlineData(null)]
    [InlineData("")]
    [InlineData("   ")]
    [InlineData("ftp://192.168.1.25")]
    [InlineData("192.168.1")]
    [InlineData("192.168.1.256")]
    [InlineData("192.168.1.25.7")]
    [InlineData("http://")]
    [InlineData("http://mi servidor")]
    [InlineData("192.168.1.25:abc")]
    public void DeberiaRechazarDireccionesInvalidas(string? entrada)
    {
        var error = Assert.Throws<ArgumentException>(() => ConfiguracionServidor.Completar(entrada));
        Assert.Contains("192.168.1.25", error.Message);
    }

    [Theory]
    [InlineData("http://192.168.1.25:5000/api", true)]
    [InlineData("https://localhost/api", true)]
    [InlineData("192.168.1.25", false)]
    [InlineData("ftp://192.168.1.25", false)]
    [InlineData(null, false)]
    public void DeberiaValidarUrls(string? url, bool esperado)
    {
        Assert.Equal(esperado, ConfiguracionServidor.EsUrlValida(url));
    }
}
