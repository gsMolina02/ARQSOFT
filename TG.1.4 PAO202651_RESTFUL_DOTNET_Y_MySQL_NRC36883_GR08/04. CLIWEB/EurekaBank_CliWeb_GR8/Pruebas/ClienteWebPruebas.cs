using System.Net;
using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using ec.edu.monster.Models;
using ec.edu.monster.Servicios;
using ec.edu.monster.Util;
using Xunit;

namespace ec.edu.monster.Pruebas;

/// <summary>
/// Cliente REST de la web (token en la sesión del navegador) y servicios de sesión y operaciones.
/// </summary>
public class ClienteWebPruebas
{
    private const string Url = "http://192.168.1.25:5000/api";

    private readonly ManejadorHttpPrueba manejador = new();
    private readonly AlmacenTokenMemoria almacen = new();
    private readonly ClienteWSEurekaBank cliente;
    private readonly ServicioSesion servicioSesion;
    private readonly ServicioOperaciones servicioOperaciones;

    public ClienteWebPruebas()
    {
        cliente = new ClienteWSEurekaBank(new HttpClient(manejador), new ManejadorTokenRESTFUL(almacen), Url);
        servicioSesion = new ServicioSesion(cliente);
        servicioOperaciones = new ServicioOperaciones(cliente);
    }

    [Fact]
    public async Task DeberiaGuardarTokenYEmpleadoEnLaSesionDelNavegador()
    {
        await IniciarSesionAsync();

        Assert.Equal(("t-123", "Monster EurekaBank"), (almacen.Token, almacen.Empleado));
        Assert.Equal("Monster EurekaBank", cliente.Empleado);
    }

    [Fact]
    public async Task DeberiaBorrarLaSesionSiElServidorRespondeQueVencio()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.Unauthorized, "{\"codigo\":401,\"mensaje\":\"La sesión ha expirado. Inicie sesión nuevamente.\"}");

        await Assert.ThrowsAsync<ExcepcionClienteWS>(() => servicioOperaciones.ConsultarAsync("00100001"));

        Assert.Null(almacen.Token);
        Assert.Null(almacen.Empleado);
    }

    [Fact]
    public async Task DeberiaExigirCredenciales()
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() => servicioSesion.IniciarSesionAsync(new LoginModelo("", "")));

        Assert.Equal(ServicioSesion.MensajeCredencialesVacias, error.Message);
        Assert.Equal(0, manejador.Peticiones);
    }

    [Fact]
    public async Task DeberiaInformarQueNoSeAvisoAlServidorSiNoHayConexion()
    {
        await IniciarSesionAsync();
        manejador.Excepcion = new HttpRequestException("Red caída");

        bool notificado = await servicioSesion.CerrarSesionAsync();

        Assert.False(notificado);
        Assert.Null(almacen.Token);
    }

    [Fact]
    public async Task DeberiaValidarElCambioDeContraseniaAntesDeEnviarlo()
    {
        var error = await Assert.ThrowsAsync<ArgumentException>(() => servicioSesion.CambiarClaveAsync(
                new CambioContraseniaModelo { ContraseniaActual = "Monster9", ContraseniaNueva = "NuevaClave", Confirmacion = "Otra" }));

        Assert.Equal(ValidacionUtil.MensajeConfirmacion, error.Message);
    }

    [Fact]
    public async Task DeberiaRegistrarUnaTransferenciaDelFormulario()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.Created,
                "{\"saldo\":540.00,\"numeroMovimiento\":8,\"codigo\":201,\"mensaje\":\"Transferencia registrada\"}");

        RespuestaTransaccion respuesta = await servicioOperaciones.RegistrarAsync(new OperacionesModelo
        {
            Operacion = TipoOperacion.Transferencia,
            Cuenta = "00100001",
            CuentaDestino = "00200001",
            Importe = "25,50"
        });

        Assert.Equal(8, respuesta.NumeroMovimiento);
        Assert.Equal(Url + "/transferencias", manejador.UltimaUrl!.ToString());
        Assert.Equal("{\"cuentaOrigen\":\"00100001\",\"cuentaDestino\":\"00200001\",\"importe\":25.50}", manejador.UltimoCuerpo);
    }

    [Theory]
    [InlineData(TipoOperacion.Deposito, "123", "100", "cuenta", ValidacionUtil.MensajeCuenta)]
    [InlineData(TipoOperacion.Retiro, "00100001", "10.555", "importe", ValidacionUtil.MensajeImporte)]
    [InlineData(TipoOperacion.Transferencia, "00100001", "10", "destino", ValidacionUtil.MensajeCuentasIguales)]
    public async Task DeberiaIndicarQueCampoDelFormularioEsIncorrecto(TipoOperacion operacion, string cuenta, string importe,
            string campo, string mensaje)
    {
        var error = await Assert.ThrowsAsync<ExcepcionCampo>(() => servicioOperaciones.RegistrarAsync(new OperacionesModelo
        {
            Operacion = operacion,
            Cuenta = cuenta,
            CuentaDestino = cuenta,
            Importe = importe
        }));

        Assert.Equal((campo, mensaje), (error.Campo, error.Message));
        Assert.Equal(0, manejador.Peticiones);
    }

    [Theory]
    [InlineData(TipoOperacion.Deposito, "/cuentas/00100001/depositos")]
    [InlineData(TipoOperacion.Retiro, "/cuentas/00100001/retiros")]
    public async Task DeberiaLlamarALaRutaDeCadaOperacion(TipoOperacion operacion, string ruta)
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.Created, "{\"saldo\":1,\"numeroMovimiento\":2,\"codigo\":201,\"mensaje\":\"ok\"}");

        await servicioOperaciones.RegistrarAsync(new OperacionesModelo { Operacion = operacion, Cuenta = "00100001", Importe = "10" });

        Assert.Equal(Url + ruta, manejador.UltimaUrl!.ToString());
        Assert.Equal("t-123", manejador.UltimoToken);
    }

    private async Task IniciarSesionAsync()
    {
        manejador.Responder(HttpStatusCode.OK,
                "{\"token\":\"t-123\",\"usuario\":\"Monster\",\"empleado\":\"Monster EurekaBank\",\"codigo\":200,\"mensaje\":\"Bienvenido\"}");
        await servicioSesion.IniciarSesionAsync(new LoginModelo("Monster", "Monster9"));
    }
}
