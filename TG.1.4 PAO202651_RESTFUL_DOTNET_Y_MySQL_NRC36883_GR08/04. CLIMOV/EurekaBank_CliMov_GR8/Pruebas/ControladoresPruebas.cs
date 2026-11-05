using System.Net;
using ec.edu.monster.Cliente;
using ec.edu.monster.Controlador;
using ec.edu.monster.Modelo;
using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ControladoresPruebas
{
    private readonly ManejadorHttpPrueba manejador = new();
    private readonly LoginControlador loginControlador;
    private readonly CuentaControlador cuentaControlador;

    public ControladoresPruebas()
    {
        var cliente = new ClienteWSEurekaBank("http://localhost:5000/api", manejador);
        loginControlador = new LoginControlador(cliente);
        cuentaControlador = loginControlador.CrearControladorCuenta();
    }

    [Theory]
    [InlineData("", "Monster9")]
    [InlineData("Monster", "")]
    public async Task DeberiaExigirCredencialesSinLlamarAlServidor(string usuario, string clave)
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() =>
                loginControlador.IniciarSesionAsync(new LoginModelo(usuario, clave)));

        Assert.Equal(LoginControlador.MensajeCredencialesVacias, error.Message);
        Assert.Equal(0, manejador.Peticiones);
    }

    [Fact]
    public async Task DeberiaEnviarElUsuarioSinEspacios()
    {
        manejador.Responder(HttpStatusCode.OK,
                "{\"token\":\"t\",\"usuario\":\"Monster\",\"empleado\":\"Monster EurekaBank\",\"codigo\":200}");

        await loginControlador.IniciarSesionAsync(new LoginModelo("  Monster ", "Monster9"));

        Assert.Contains("\"usuario\":\"Monster\"", manejador.UltimoCuerpo);
        Assert.Equal("Monster EurekaBank", loginControlador.Empleado);
    }

    [Fact]
    public async Task DeberiaValidarLaCuentaAntesDeConsultar()
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() => cuentaControlador.ConsultarAsync("123"));

        Assert.Equal(ValidacionUtil.MensajeCuenta, error.Message);
        Assert.Equal(0, manejador.Peticiones);
    }

    [Fact]
    public async Task DeberiaRechazarTransferenciasALaMismaCuenta()
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() =>
                cuentaControlador.TransferirAsync("00100001", "00100001", 10m));

        Assert.Equal(ValidacionUtil.MensajeCuentasIguales, error.Message);
        Assert.Equal(0, manejador.Peticiones);
    }

    [Fact]
    public async Task DeberiaRechazarImportesConMasDeDosDecimales()
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() => cuentaControlador.DepositarAsync("00100001", 1.555m));

        Assert.Equal(ValidacionUtil.MensajeImporte, error.Message);
    }

    [Fact]
    public async Task DeberiaValidarLaConfirmacionDeLaNuevaContrasenia()
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() =>
                loginControlador.CambiarClaveAsync("Monster9", "NuevaClave", "Distinta"));

        Assert.Equal(ValidacionUtil.MensajeConfirmacion, error.Message);
        Assert.Equal(0, manejador.Peticiones);
    }
}
