using System.Net;
using ec.edu.monster.Cliente;
using ec.edu.monster.Models;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ProbadorConexionPruebas
{
    private readonly ManejadorHttpPrueba manejador = new();

    [Fact]
    public async Task DeberiaReconocerAlServicioPorSuRespuestaSinToken()
    {
        ResultadoConexion resultado = await new ProbadorConexion(manejador).ProbarAsync("http://192.168.1.25:5000/api/");

        Assert.True(resultado.Exitosa);
        Assert.Equal("http://192.168.1.25:5000/api/cuentas/00000000/movimientos", manejador.UltimaUrl!.ToString());
        Assert.Null(manejador.UltimoToken);
    }

    [Fact]
    public async Task DeberiaAvisarSiLaRutaNoEsLaDelServicio()
    {
        manejador.Responder(HttpStatusCode.NotFound, "");

        ResultadoConexion resultado = await new ProbadorConexion(manejador).ProbarAsync("http://192.168.1.25:5000");

        Assert.False(resultado.Exitosa);
        Assert.Contains("/api", resultado.Mensaje);
    }

    [Fact]
    public async Task DeberiaRechazarOtroProgramaQueEscucheEnElPuerto()
    {
        manejador.Responder(HttpStatusCode.Unauthorized, "<html>Acceso no autorizado</html>");

        ResultadoConexion resultado = await new ProbadorConexion(manejador).ProbarAsync("http://192.168.1.25:5000/api");

        Assert.False(resultado.Exitosa);
        Assert.Contains("no es el servicio REST EurekaBank", resultado.Mensaje);
    }

    [Fact]
    public async Task DeberiaExplicarQueRevisarSiNoHayRespuesta()
    {
        manejador.Excepcion = new TaskCanceledException();

        ResultadoConexion resultado = await new ProbadorConexion(manejador).ProbarAsync("http://192.168.1.25:5000/api");

        Assert.False(resultado.Exitosa);
        Assert.Contains("firewall permita el puerto 5000", resultado.Mensaje);
    }

    [Fact]
    public async Task DeberiaInformarSiElServidorEstaApagado()
    {
        manejador.Excepcion = new HttpRequestException("Conexión rechazada");

        ResultadoConexion resultado = await new ProbadorConexion(manejador).ProbarAsync("http://192.168.1.25:5000/api");

        Assert.False(resultado.Exitosa);
        Assert.Contains("PREPARAR_SERVIDOR_REST_5000.bat", resultado.Mensaje);
    }
}
