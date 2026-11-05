using System.Net;
using ec.edu.monster.Cliente;
using ec.edu.monster.Cliente.Dto;
using Xunit;

namespace ec.edu.monster.Pruebas;

public class ClienteWSEurekaBankPruebas
{
    private const string Url = "http://192.168.1.25:5000/api";
    private const string Token = "695bd270-d35f-48c9-802a-c0db751b576f";

    private readonly ManejadorHttpPrueba manejador = new();
    private readonly ClienteWSEurekaBank cliente;

    public ClienteWSEurekaBankPruebas()
    {
        cliente = new ClienteWSEurekaBank(Url + "/", manejador);
    }

    [Fact]
    public async Task DeberiaIniciarSesionYGuardarElToken()
    {
        await IniciarSesionAsync();

        Assert.Equal(HttpMethod.Post, manejador.UltimoMetodo);
        Assert.Equal(Url + "/sesion/login", manejador.UltimaUrl!.ToString());
        Assert.Equal("{\"usuario\":\"Monster\",\"clave\":\"Monster9\"}", manejador.UltimoCuerpo);
        Assert.True(cliente.HaySesionActiva);
        Assert.Equal(Token, cliente.TokenSesion);
        Assert.Equal("Monster EurekaBank", cliente.Empleado);
    }

    [Fact]
    public async Task DeberiaEnviarElTokenBearerEnLasOperacionesProtegidas()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.OK, """
            {"cuenta":{"numero":"00100001","cliente":"Carlos Andrés Pérez Salazar","cedula":"1712345675","moneda":"USD",
             "simboloMoneda":"$","sucursal":"Matriz Quito","estado":"ACTIVO","saldo":540.00,"fechaApertura":"2026-01-15",
             "contadorMovimientos":1},
             "movimientos":[{"numero":1,"fechaHora":"2026-01-15T09:10:00","codigoTipo":"001","tipo":"Apertura de cuenta",
             "accion":"INGRESO","importe":500.00,"cuentaReferencia":null,"empleado":"0001"}],
             "codigo":200,"mensaje":"La cuenta registra 1 movimiento(s)"}
            """);

        RespuestaMovimientos respuesta = await cliente.ConsultarMovimientosAsync("00100001");

        Assert.Equal(HttpMethod.Get, manejador.UltimoMetodo);
        Assert.Equal(Url + "/cuentas/00100001/movimientos", manejador.UltimaUrl!.ToString());
        Assert.Equal(Token, manejador.UltimoToken);
        Assert.Equal(540.00m, respuesta.Cuenta!.Saldo);
        Assert.Equal(new DateOnly(2026, 1, 15), respuesta.Cuenta.FechaApertura);
        MovimientoDto movimiento = Assert.Single(respuesta.Movimientos);
        Assert.True(movimiento.EsIngreso);
        Assert.Equal(new DateTime(2026, 1, 15, 9, 10, 0), movimiento.FechaHora);
    }

    [Fact]
    public async Task DeberiaListarLasCuentasConElToken()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.OK, """
            {"cuentas":[{"numero":"00100001","cliente":"Carlos Andrés Pérez Salazar","estado":"ACTIVO","saldo":540.00},
                        {"numero":"00300002","cliente":"Ana Lucía Torres","estado":"CANCELADO","saldo":0}],
             "codigo":200,"mensaje":"Se encontraron 2 cuenta(s)"}
            """);

        RespuestaCuentas respuesta = await cliente.ListarCuentasAsync();

        Assert.Equal(HttpMethod.Get, manejador.UltimoMetodo);
        Assert.Equal(Url + "/cuentas", manejador.UltimaUrl!.ToString());
        Assert.Equal(Token, manejador.UltimoToken);
        Assert.Equal(["00100001", "00300002"], respuesta.Cuentas.Select(c => c.Numero));
        Assert.False(respuesta.Cuentas[1].EstaActiva);
    }

    [Fact]
    public async Task DeberiaRegistrarDepositosRetirosYTransferencias()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.Created,
                "{\"saldo\":640.50,\"numeroMovimiento\":5,\"codigo\":201,\"mensaje\":\"Depósito registrado\"}");

        RespuestaTransaccion deposito = await cliente.RegistrarDepositoAsync("00100001", 100.50m);
        Assert.Equal((Url + "/cuentas/00100001/depositos", "{\"importe\":100.50}"),
                (manejador.UltimaUrl!.ToString(), manejador.UltimoCuerpo));
        Assert.Equal((640.50m, 5), (deposito.Saldo, deposito.NumeroMovimiento));

        await cliente.RegistrarRetiroAsync("00100001", 50m);
        Assert.Equal(Url + "/cuentas/00100001/retiros", manejador.UltimaUrl!.ToString());

        await cliente.RegistrarTransferenciaAsync("00100001", "00200001", 25m);
        Assert.Equal(Url + "/transferencias", manejador.UltimaUrl!.ToString());
        Assert.Equal("{\"cuentaOrigen\":\"00100001\",\"cuentaDestino\":\"00200001\",\"importe\":25}", manejador.UltimoCuerpo);
        Assert.Equal(Token, manejador.UltimoToken);
    }

    [Fact]
    public async Task DeberiaMostrarElMensajeDelServidorEnLosErrores()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.Conflict, "{\"codigo\":409,\"mensaje\":\"Saldo insuficiente. Saldo disponible: $540.00\"}");

        var error = await Assert.ThrowsAsync<ExcepcionClienteWS>(() => cliente.RegistrarRetiroAsync("00100001", 999m));

        Assert.Equal(409, error.CodigoEstado);
        Assert.Equal("Saldo insuficiente. Saldo disponible: $540.00", error.Message);
        Assert.True(cliente.HaySesionActiva);
    }

    [Fact]
    public async Task DeberiaOlvidarLaSesionSiElServidorRespondeQueVencio()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.Unauthorized, "{\"codigo\":401,\"mensaje\":\"La sesión ha expirado. Inicie sesión nuevamente.\"}");

        var error = await Assert.ThrowsAsync<ExcepcionClienteWS>(() => cliente.ConsultarMovimientosAsync("00100001"));

        Assert.True(error.EsErrorAutenticacion);
        Assert.False(cliente.HaySesionActiva);
        Assert.Null(cliente.Empleado);
    }

    [Fact]
    public async Task DeberiaExigirSesionAntesDeLlamarAlServidor()
    {
        var error = await Assert.ThrowsAsync<ExcepcionClienteWS>(() => cliente.ConsultarMovimientosAsync("00100001"));

        Assert.True(error.EsErrorAutenticacion);
        Assert.Equal(0, manejador.Peticiones);
    }

    [Fact]
    public async Task DeberiaInformarCuandoNoHayConexion()
    {
        manejador.Excepcion = new HttpRequestException("No se puede establecer una conexión");

        var error = await Assert.ThrowsAsync<ExcepcionClienteWS>(() => cliente.IniciarSesionAsync("Monster", "Monster9"));

        Assert.True(error.EsErrorConexion);
        Assert.Contains(Url, error.Message);
    }

    [Fact]
    public async Task DeberiaBorrarElTokenAlCerrarSesionAunqueNoHayaConexion()
    {
        await IniciarSesionAsync();
        manejador.Excepcion = new HttpRequestException("Red caída");

        await Assert.ThrowsAsync<ExcepcionClienteWS>(() => cliente.CerrarSesionAsync());

        Assert.False(cliente.HaySesionActiva);
    }

    [Fact]
    public async Task DeberiaCerrarSesionEnElServidor()
    {
        await IniciarSesionAsync();
        manejador.Responder(HttpStatusCode.OK, "{\"codigo\":200,\"mensaje\":\"Sesión cerrada correctamente\"}");

        string mensaje = await cliente.CerrarSesionAsync();

        Assert.Equal("Sesión cerrada correctamente", mensaje);
        Assert.Equal((HttpMethod.Post, Url + "/sesion/logout"), (manejador.UltimoMetodo, manejador.UltimaUrl!.ToString()));
        Assert.False(cliente.HaySesionActiva);
    }

    [Fact]
    public async Task DeberiaAvisarSiLaRespuestaNoEsJson()
    {
        manejador.Responder(HttpStatusCode.InternalServerError, "<html>Error</html>");

        var error = await Assert.ThrowsAsync<ExcepcionClienteWS>(() => cliente.IniciarSesionAsync("Monster", "Monster9"));

        Assert.Equal("El servidor respondió con el código HTTP 500.", error.Message);
    }

    private async Task IniciarSesionAsync()
    {
        manejador.Responder(HttpStatusCode.OK, $$"""
            {"token":"{{Token}}","usuario":"Monster","empleado":"Monster EurekaBank","minutosVigencia":30,
             "codigo":200,"mensaje":"Bienvenido, Monster EurekaBank"}
            """);
        await cliente.IniciarSesionAsync("Monster", "Monster9");
    }
}
